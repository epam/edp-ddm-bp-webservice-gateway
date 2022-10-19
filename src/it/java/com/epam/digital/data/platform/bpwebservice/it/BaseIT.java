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

package com.epam.digital.data.platform.bpwebservice.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.epam.digital.data.platform.integration.idm.service.IdmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.xml.transform.Source;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.ResourceSource;
import redis.embedded.RedisCluster;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {

  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  @Qualifier("digitalSignatureMockServer")
  private WireMockServer digitalSignatureMockServer;
  @Autowired
  @Qualifier("keycloakMockServer")
  private WireMockServer keycloakMockServer;
  @Autowired
  @Qualifier("cephMockServer")
  protected WireMockServer cephMockServer;
  @Autowired
  @Qualifier("bpmsMockServer")
  protected WireMockServer bpmsMockServer;
  @Autowired
  protected IdmService idmService;
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;
  private MockWebServiceClient mockClient;
  @Autowired
  protected RedisCluster redisCluster;

  @BeforeEach
  public void init() {
    mockClient = MockWebServiceClient.createClient(applicationContext);
    mockConnectToKeycloak();
    mockGetBucket();
  }

  protected MockWebServiceClient mockClient() {
    return mockClient;
  }

  protected WireMockServer digitalSignatureMockServer() {
    return digitalSignatureMockServer;
  }

  protected WireMockServer keycloakMockServer() {
    return keycloakMockServer;
  }

  protected WireMockServer cephMockServer() {
    return cephMockServer;
  }

  protected WireMockServer bpmsMockServer() {
    return bpmsMockServer;
  }

  @SneakyThrows
  protected Source fileSource(String filePath) {
    return new ResourceSource(new ClassPathResource(filePath));
  }

  @SneakyThrows
  protected String fileContent(String filePath) {
    return new String(new ClassPathResource(filePath).getInputStream().readAllBytes());
  }

  protected void mockPutCephStartForm(String businessProcessDefinitionKey, String body) {
    cephMockServer.addStubMapping(
        stubFor(put(urlPathMatching(
            "/bucket/lowcode_" + businessProcessDefinitionKey + "_start_form_.+"))
            .withRequestBody(containing(body))
            .willReturn(aResponse().withStatus(200))));
  }

  private void mockConnectToKeycloak() {
    keycloakMockServer.addStubMapping(
        stubFor(post(urlPathEqualTo("/auth/realms/test-realm/protocol/openid-connect/token"))
            .withRequestBody(equalTo("grant_type=client_credentials"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(fileContent("/keycloakCommon/keycloakConnectResponse.json")))));
  }

  private void mockGetBucket() {
    cephMockServer.addStubMapping(
        stubFor(get(urlPathEqualTo("/"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-type", "application/xml")
                .withBody(fileContent("/cephCommon/getCephBucketResponse.xml")
                    .replaceAll(" {2}|\r|\n", "")))));
  }
}
