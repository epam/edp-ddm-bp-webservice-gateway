package com.epam.digital.data.platform.bpwebservice.wsendpoint;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.dto.factory.ObjectFactory;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import javax.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * SOAP endpoint that is used for starting business-process
 */
@Endpoint
@RequiredArgsConstructor
public class StartBpEndpoint {

  private final ObjectFactory objectFactory;
  private final StartBpService service;

  /**
   * Method that is used for wsdl operation generation
   * <p>
   * Is invoked when soap service receives {@link StartBpRequest startBpRequest} message
   * <p>
   * Delegates an invocation to {@link StartBpService#startBp(StartBpRequest)}
   *
   * @param startBpRequest received {@link StartBpRequest startBpRequest} message
   * @return created {@link JAXBElement JAXB structure} over {@link StartBpResponse startBpResponse}
   * message that will be returned as SOAP response envelope
   */
  @PayloadRoot(namespace = Constants.NAMESPACE, localPart = StartBpRequest.START_BP_REQUEST_NAME)
  @ResponsePayload
  public JAXBElement<StartBpResponse> startBp(@RequestPayload StartBpRequest startBpRequest) {
    return objectFactory.createStartBpResponse(service.startBp(startBpRequest));
  }
}
