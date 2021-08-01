package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when application faced any DSO related errors.
 * <p>
 * Is mapped to a soap fault with code SERVER
 */
@SoapFault(
    faultCode = FaultCode.SERVER,
    faultStringOrReason = "Digital signature runtime error"
)
public class DsoSignatureException extends RuntimeException {

  public DsoSignatureException(Throwable cause) {
    super(cause);
  }

}
