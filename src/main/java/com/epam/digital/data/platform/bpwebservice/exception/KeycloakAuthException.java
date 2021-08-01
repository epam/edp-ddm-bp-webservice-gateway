package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when application faced any Keycloak related errors.
 * <p>
 * Is mapped to a soap fault with code SERVER
 */
@SoapFault(
    faultCode = FaultCode.SERVER,
    faultStringOrReason = "Keycloak authentication runtime error"
)
public class KeycloakAuthException extends RuntimeException {

  public KeycloakAuthException(Throwable cause) {
    super(cause);
  }

}
