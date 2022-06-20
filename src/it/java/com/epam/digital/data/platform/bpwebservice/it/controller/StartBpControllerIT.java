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

package com.epam.digital.data.platform.bpwebservice.it.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bpwebservice.it.BaseIT;
import com.epam.digital.data.platform.integration.idm.client.KeycloakAdminClient;
import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class StartBpControllerIT extends BaseIT {

  private String testUserToken;

  @BeforeEach
  void setUp() throws IOException {
    testUserToken = new String(ByteStreams.toByteArray(Objects.requireNonNull(
        getClass().getResourceAsStream("/testuserAccessToken.txt"))));
  }

  @Test
  void startBp() throws Exception {
    mockPutCephStartForm("happyPathBusinessProcess",
        fileContent("/startBp/happyPath/json/cephContent.json").replaceAll("[ \r\n]", ""));

    bpmsMockServer.addStubMapping(stubFor(
        post(urlPathEqualTo("/api/process-definition/key/happyPathBusinessProcess/start"))
            .withHeader("X-Access-Token", equalTo(testUserToken))
            .withRequestBody(matching(
                fileContent("/startBp/happyPath/json/startProcessBpRequestRegex.json")
                    .replaceAll("[ \r\n]", "")
                    .replaceAll("\\{", "\\\\{")))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    fileContent("/startBp/happyPath/json/bpmsStartProcessBpResponse.json")))));

    var request = fileContent("/startBp/happyPath/json/startBpRequest.json");
    var expectedResponse = fileContent("/startBp/happyPath/json/startBpResponse.json");

    mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void startBp_unauthorized() throws Exception {
    var request = fileContent("/startBp/happyPath/json/startBpRequest.json");

    mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void startBp_noSuchBpIsDefined() throws Exception {
    var request = fileContent("/startBp/noSuchBpIsDefined/json/startBpRequest.json");

    var responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isUnprocessableEntity())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var response = objectMapper.readValue(responseString, SystemErrorDto.class);
    assertThat(response)
        .hasFieldOrProperty("traceId")
        .hasFieldOrPropertyWithValue("code", "422")
        .hasFieldOrPropertyWithValue("localizedMessage", null)
        .hasFieldOrPropertyWithValue("message",
            "No such business process no-such-business-process-defined is defined in trembita.process_definitions");
  }

  @Test
  void startBp_missedRequiredBpInputParameter() throws Exception {
    var request = fileContent("/startBp/missedRequiredBpInputParameter/json/startBpRequest.json");

    var responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isUnprocessableEntity())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var response = objectMapper.readValue(responseString, SystemErrorDto.class);
    assertThat(response)
        .hasFieldOrProperty("traceId")
        .hasFieldOrPropertyWithValue("code", "422")
        .hasFieldOrPropertyWithValue("localizedMessage", null)
        .hasFieldOrPropertyWithValue("message",
            "No such input param missed_var is defined in request for missedRequiredBpInputParameter");
  }

  @Test
  void startBp_dsoError() throws Exception {
    var dsoRequest = fileContent("/startBp/dsoError/json/dsoRequest.json");
    digitalSignatureMockServer().addStubMapping(stubFor(post(urlPathEqualTo("/api/eseal/sign"))
        .withRequestBody(equalToJson(dsoRequest))
        .withHeader("X-Access-Token", equalTo(testUserToken))
        .willReturn(aResponse().withStatus(400)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"message\":\"message\",\"localizedMessage\":\"localizedMessage\"}"))));

    var request = fileContent("/startBp/dsoError/json/startBpRequest.json");

    var responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isInternalServerError())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var response = objectMapper.readValue(responseString, SystemErrorDto.class);
    assertThat(response)
        .hasFieldOrProperty("traceId")
        .hasFieldOrPropertyWithValue("code", "500")
        .hasFieldOrPropertyWithValue("localizedMessage", "localizedMessage")
        .hasFieldOrPropertyWithValue("message", "message");
  }

  @Test
  void startBp_cephError() throws Exception {
    var request = fileContent("/startBp/cephError/json/startBpRequest.json");

    var responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isInternalServerError())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var response = objectMapper.readValue(responseString, SystemErrorDto.class);
    assertThat(response)
        .hasFieldOrProperty("traceId")
        .hasFieldOrPropertyWithValue("code", "500")
        .hasFieldOrPropertyWithValue("localizedMessage", null)
        .hasFieldOrProperty("message");
  }

  @Test
  void startBp_bpmsError() throws Exception {
    mockPutCephStartForm("bpmsError",
        fileContent("/startBp/bpmsError/json/cephContent.json").replaceAll("[ \r\n]", ""));

    var request = fileContent("/startBp/bpmsError/json/startBpRequest.json");

    var responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/start-bp")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Access-Token", testUserToken)
            .content(request))
        .andExpect(status().isInternalServerError())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var response = objectMapper.readValue(responseString, SystemErrorDto.class);
    assertThat(response)
        .hasFieldOrProperty("traceId")
        .hasFieldOrPropertyWithValue("code", "500")
        .hasFieldOrPropertyWithValue("localizedMessage", null)
        .hasFieldOrPropertyWithValue("message", null);
  }
}
