package com.epam.digital.data.platform.bpwebservice.it.wsendpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.xml.transform.Source;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.ResourceSource;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
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
  private Keycloak keycloak;

  private MockWebServiceClient mockClient;

  @Before
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

  protected Keycloak keycloak() {
    return keycloak;
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
