/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bpwebservice.it.wsendpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.ws.test.server.RequestCreators.withSoapEnvelope;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.serverOrReceiverFault;
import static org.springframework.ws.test.server.ResponseMatchers.soapEnvelope;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class StartBpEndpointIT extends BaseIT {

  @Test
  public void startBp() {
    mockPutCephStartForm("happyPathBusinessProcess",
        fileContent("/startBp/happyPath/json/cephContent.json").replaceAll("[ \r\n]", ""));

    bpmsMockServer.addStubMapping(stubFor(
        post(urlPathEqualTo("/api/process-definition/key/happyPathBusinessProcess/start"))
            .withHeader("X-Access-Token", equalTo("token"))
            .withRequestBody(matching(
                fileContent("/startBp/happyPath/json/startProcessBpRequestRegex.json")
                    .replaceAll("[ \r\n]", "")
                    .replaceAll("\\{", "\\\\{")))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(fileContent("/startBp/happyPath/json/startProcessBpResponse.json")))));

    var requestEnvelope = fileSource("/startBp/happyPath/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/happyPath/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(noFault())
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_noSuchBpIsDefined() {
    var requestEnvelope = fileSource("/startBp/noSuchBpIsDefined/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/noSuchBpIsDefined/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(clientOrSenderFault("No such business process defined"))
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_missedRequiredBpInputParameter() {
    var requestEnvelope = fileSource(
        "/startBp/missedRequiredBpInputParameter/xml/startBpRequest.xml");
    var responseEnvelope = fileSource(
        "/startBp/missedRequiredBpInputParameter/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(clientOrSenderFault("Missed required business process input parameter"))
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_dsoError() {
    var dsoRequest = fileContent("/startBp/keycloakError/json/dsoRequest.json");
    digitalSignatureMockServer().addStubMapping(stubFor(post(urlPathEqualTo("/api/eseal/sign"))
        .withRequestBody(equalToJson(dsoRequest))
        .withHeader("X-Access-Token", equalTo("token"))
        .willReturn(aResponse().withStatus(400)
            .withHeader("Content-Type", "application/json")
            .withBody("{}"))));

    var requestEnvelope = fileSource("/startBp/dsoError/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/dsoError/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(serverOrReceiverFault("Digital signature runtime error"))
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_keycloakError() {
    keycloakMockServer().resetAll();
    ReflectionTestUtils.setField(keycloak().tokenManager(), "currentToken", null);

    var requestEnvelope = fileSource("/startBp/keycloakError/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/keycloakError/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(serverOrReceiverFault("Keycloak authentication runtime error"))
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_cephError() {
    var requestEnvelope = fileSource("/startBp/cephError/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/cephError/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(serverOrReceiverFault("Storage connection runtime error"))
        .andExpect(soapEnvelope(responseEnvelope));
  }

  @Test
  public void startBp_bpmsError() {
    mockPutCephStartForm("bpmsError",
        fileContent("/startBp/bpmsError/json/cephContent.json").replaceAll("[ \r\n]", ""));

    var requestEnvelope = fileSource("/startBp/bpmsError/xml/startBpRequest.xml");
    var responseEnvelope = fileSource("/startBp/bpmsError/xml/startBpResponse.xml");
    mockClient().sendRequest(withSoapEnvelope(requestEnvelope))
        .andExpect(serverOrReceiverFault("Bpms connection runtime error"))
        .andExpect(soapEnvelope(responseEnvelope));
  }
}
