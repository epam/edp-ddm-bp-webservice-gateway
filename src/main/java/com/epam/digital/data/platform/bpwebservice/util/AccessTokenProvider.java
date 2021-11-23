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

package com.epam.digital.data.platform.bpwebservice.util;

import com.epam.digital.data.platform.bpwebservice.exception.KeycloakAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;

/**
 * Util bean that is used for getting keycloak service account access token
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenProvider {

  private final Keycloak keycloak;

  /**
   * @return current service account access token
   * @throws KeycloakAuthException in case of any error
   */
  public String getToken() {
    try {
      return keycloak.tokenManager().getAccessTokenString();
    } catch (Exception e) {
      log.error("Faced keycloak error", e);
      throw new KeycloakAuthException(e);
    }
  }
}
