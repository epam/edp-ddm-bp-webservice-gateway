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

package com.epam.digital.data.platform.bpwebservice.config;

import com.epam.digital.data.platform.integration.idm.client.KeycloakAdminClient;
import com.epam.digital.data.platform.integration.idm.dto.KeycloakClientProperties;
import com.epam.digital.data.platform.integration.idm.factory.IdmClientFactory;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that creates {@link Keycloak} client object for connecting to keycloak
 */
@Configuration
public class KeycloakClientConfig {

  @Bean
  @ConfigurationProperties(prefix = "keycloak")
  public KeycloakClientProperties keycloakClientProperties() {
    return new KeycloakClientProperties();
  }

  @Bean
  public KeycloakAdminClient keycloakAdminClient(@Value("${keycloak.url}") String url,
      KeycloakClientProperties keycloakClientProperties) {
    return new IdmClientFactory().keycloakAdminClient(url, keycloakClientProperties);
  }
}
