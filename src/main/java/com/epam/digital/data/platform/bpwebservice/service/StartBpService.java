package com.epam.digital.data.platform.bpwebservice.service;

import com.epam.digital.data.platform.bpms.client.ProcessDefinitionRestClient;
import com.epam.digital.data.platform.bpwebservice.config.BusinessProcessProperties;
import com.epam.digital.data.platform.bpwebservice.config.TrembitaBusinessProcessProperties;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.exception.BpmsConnectionException;
import com.epam.digital.data.platform.bpwebservice.exception.CephConnectionException;
import com.epam.digital.data.platform.bpwebservice.exception.DsoSignatureException;
import com.epam.digital.data.platform.bpwebservice.exception.KeycloakAuthException;
import com.epam.digital.data.platform.bpwebservice.exception.MissedRequiredBusinessProcessInputParameterException;
import com.epam.digital.data.platform.bpwebservice.exception.NoSuchBusinessProcessDefinedException;
import com.epam.digital.data.platform.bpwebservice.util.AccessTokenProvider;
import com.epam.digital.data.platform.bpwebservice.util.CephKeyProvider;
import com.epam.digital.data.platform.dso.api.dto.SignRequestDto;
import com.epam.digital.data.platform.dso.client.DigitalSealRestClient;
import com.epam.digital.data.platform.dso.client.exception.BaseException;
import com.epam.digital.data.platform.integration.ceph.dto.FormDataDto;
import com.epam.digital.data.platform.integration.ceph.exception.CephCommunicationException;
import com.epam.digital.data.platform.integration.ceph.exception.MisconfigurationException;
import com.epam.digital.data.platform.integration.ceph.service.FormDataCephService;
import com.epam.digital.data.platform.starter.errorhandling.exception.SystemException;
import com.epam.digital.data.platform.starter.errorhandling.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceWithVariablesDto;
import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.springframework.stereotype.Service;

/**
 * Service that is used for starting business-process
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StartBpService {

  private static final String START_FORM_CEPH_KEY = "start_form_ceph_key";

  private final TrembitaBusinessProcessProperties businessProcessProperties;

  private final DigitalSealRestClient digitalSealRestClient;
  private final ObjectMapper objectMapper;
  private final FormDataCephService formDataCephService;
  private final AccessTokenProvider accessTokenProvider;
  private final CephKeyProvider cephKeyProvider;
  private final ProcessDefinitionRestClient processDefinitionRestClient;

  /**
   * Method that is used by startBp WS endpoint. It does:
   * <ol>
   * <li>Check if requested business process is defined in application properties</li>
   * <li>Check if there is missed some required {@code trembita.process_definitions.start_vars}
   * in request and creates map of <i>required business process start vars</i></li>
   * <li>Sign <i>required business process start vars</i> with system key if it's required ({@code trembita.process_definitions.requires_signature})</li>
   * <li>Retrieve keycloak <i>service account access token</i></li>
   * <li>Put <i>required business process start vars</i> with <i>service account access token</i>
   * and signature to ceph</li>
   * <li>Starts required business process with variables in return</li>
   * <li>Maps returned variables to list of required return variables defined in
   * {@code trembita.process_definitions.return_vars}</li>
   * </ol>
   *
   * @param startBpRequest dto representation of SOAP request envelope
   * @return dto representation of SOAP response envelope with result variables
   * @throws NoSuchBusinessProcessDefinedException                if no bp is defined in application
   *                                                              properties
   * @throws MissedRequiredBusinessProcessInputParameterException in case if there is missed input
   *                                                              parameter in startBpRequest
   * @throws KeycloakAuthException                                in case of facing Keycloak related
   *                                                              exception
   * @throws DsoSignatureException                                in case of facing dso or json
   *                                                              processing exception
   * @throws CephConnectionException                              in case of facing ceph related
   *                                                              exception
   * @throws BpmsConnectionException                              in case of facing BPMS related
   *                                                              exception
   */
  public StartBpResponse startBp(StartBpRequest startBpRequest) {
    var bpDefinitionKey = startBpRequest.getBusinessProcessDefinitionKey();
    log.info("Executing process {}", bpDefinitionKey);

    var bpProperties = getBusinessProcessProperties(bpDefinitionKey);
    var bpInputParameters = getBusinessProcessInputParameters(startBpRequest, bpProperties);
    var signature = bpProperties.isRequiresSignature() ?
        getInputParamsDigitalSignature(bpInputParameters) : null;
    var cephKey = putInputParamsToCeph(bpDefinitionKey, bpInputParameters, signature);
    var processInstance = startProcessInstance(bpDefinitionKey, cephKey);
    var bpOutputParameters = getBusinessProcessOutputParameters(bpProperties, processInstance);

    var startBpResponse = new StartBpResponse();
    startBpResponse.setResultVariables(bpOutputParameters);
    log.info("Process {} finished", bpDefinitionKey);
    return startBpResponse;
  }

  /**
   * @param bpDefinitionKey bp that has to be started
   * @return {@link BusinessProcessProperties} object with defined bp start vars and return vars
   * @throws NoSuchBusinessProcessDefinedException if no bp is defined in application properties
   */
  private BusinessProcessProperties getBusinessProcessProperties(String bpDefinitionKey) {
    log.debug("Trying to find business process {} in trembita.process_definitions - {}",
        bpDefinitionKey, businessProcessProperties);
    var bpProperties = businessProcessProperties.findBusinessProcessProperties(bpDefinitionKey);

    if (Objects.isNull(bpProperties)) {
      log.info("No such business process {} is defined in trembita.process_definitions",
          bpDefinitionKey);
      throw new NoSuchBusinessProcessDefinedException();
    }
    return bpProperties;
  }

  /**
   * @param startBpRequest start business process request that contains input parameters
   * @param bpProperties   business process properties object that contains required param names
   * @return map that contains required business process parameters
   * @throws MissedRequiredBusinessProcessInputParameterException in case if there is missed input
   *                                                              parameter in startBpRequest
   */
  private Map<String, String> getBusinessProcessInputParameters(StartBpRequest startBpRequest,
      BusinessProcessProperties bpProperties) {
    var bpDefinitionKey = startBpRequest.getBusinessProcessDefinitionKey();
    log.debug("Getting business process {} input parameters", bpDefinitionKey);
    if (Objects.isNull(bpProperties.getStartVars())) {
      return Collections.emptyMap();
    }

    var bpInputParameters = new HashMap<String, String>();
    var requestStartVars = startBpRequest.getStartVariables();

    bpProperties.getStartVars().forEach(startVar -> {
      if (Objects.isNull(requestStartVars) || !requestStartVars.containsKey(startVar)) {
        log.info("No such input param {} is defined in request for {}", startVar, bpDefinitionKey);
        throw new MissedRequiredBusinessProcessInputParameterException();
      }
      bpInputParameters.put(startVar, startBpRequest.getStartVariables().get(startVar));
    });
    log.debug("Founded params for {} - {}", bpDefinitionKey, bpInputParameters);
    return Collections.unmodifiableMap(bpInputParameters);
  }

  /**
   * @param bpInputParameters data that has to be signed
   * @return signature as string
   * @throws DsoSignatureException in case of facing dso or json processing exception
   */
  private String getInputParamsDigitalSignature(Map<String, String> bpInputParameters) {
    log.debug("Signing input params with system signature - {}", bpInputParameters);
    try {
      var data = objectMapper.writeValueAsString(bpInputParameters);
      var signRequestDto = SignRequestDto.builder().data(data).build();
      var signResponse = digitalSealRestClient.sign(signRequestDto);
      log.debug("Input params were signed. Signature - {}", signResponse.getSignature());
      return signResponse.getSignature();
    } catch (JsonProcessingException | BaseException e) {
      log.error("Faced dso error", e);
      throw new DsoSignatureException(e);
    }
  }

  /**
   * Put start bp input params to ceph
   *
   * @param bpDefinitionKey   business process definition key
   * @param bpInputParameters input params that has to be put to ceph
   * @param signature         signature of input params
   * @return ceph key of created document
   * @throws CephConnectionException in case of facing ceph related exception
   */
  private String putInputParamsToCeph(String bpDefinitionKey, Map<String, String> bpInputParameters,
      String signature) {
    log.debug("Saving input parameters for {} to Ceph - {}", bpDefinitionKey, bpInputParameters);
    var uuid = UUID.randomUUID().toString();
    var cephKey = cephKeyProvider.generateStartFormKey(bpDefinitionKey, uuid);

    var formData = FormDataDto.builder()
        .accessToken(accessTokenProvider.getToken())
        .data(new LinkedHashMap<>(bpInputParameters))
        .signature(signature)
        .build();
    try {
      formDataCephService.putFormData(cephKey, formData);
    } catch (MisconfigurationException | CephCommunicationException e) {
      log.error("Faced ceph error", e);
      throw new CephConnectionException(e);
    }
    log.debug("Input parameters for {} saved in Ceph", bpDefinitionKey);
    return cephKey;
  }

  /**
   * Starts business process and returns process instance dto with variables
   *
   * @param bpDefinitionKey business process definition key
   * @param startFormKey    start form ceph key
   * @return process instance dto with variables
   * @throws BpmsConnectionException in case of facing BPMS related exception
   */
  private ProcessInstanceWithVariablesDto startProcessInstance(String bpDefinitionKey,
      String startFormKey) {
    log.debug("Starting {} process instance", bpDefinitionKey);
    var startProcessInstanceDto = new StartProcessInstanceDto();
    var variableValueDto = new VariableValueDto();
    variableValueDto.setValue(startFormKey);
    startProcessInstanceDto.setVariables(Map.of(START_FORM_CEPH_KEY, variableValueDto));
    startProcessInstanceDto.setWithVariablesInReturn(true);
    try {
      return processDefinitionRestClient
          .startProcessInstanceByKey(bpDefinitionKey, startProcessInstanceDto);
    } catch (ValidationException | SystemException e) {
      log.error("Faced bpms error", e);
      throw new BpmsConnectionException(e);
    }
  }

  /**
   * Get required business process output parameters
   *
   * @param bpProperties    business process properties object that contains required variable
   *                        names
   * @param processInstance process instance dto with all returned variables
   * @return map of required business process output parameters
   */
  private Map<String, String> getBusinessProcessOutputParameters(
      BusinessProcessProperties bpProperties, ProcessInstanceWithVariablesDto processInstance) {
    var processInstanceVars = processInstance.getVariables();
    if (Objects.isNull(bpProperties.getReturnVars()) || Objects.isNull(processInstanceVars)) {
      return Collections.emptyMap();
    }

    var bpOutputParameters = new HashMap<String, String>();
    bpProperties.getReturnVars().forEach(returnVar -> {
      var value = processInstanceVars.get(returnVar);
      bpOutputParameters.put(returnVar, Objects.isNull(value) || Objects.isNull(value.getValue())
          ? null : value.getValue().toString());
    });
    return Collections.unmodifiableMap(bpOutputParameters);
  }
}
