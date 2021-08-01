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
