package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when application faced any BPMS related errors.
 * <p>
 * Is mapped to a soap fault with code SERVER
 */
@SoapFault(
    faultCode = FaultCode.SERVER,
    faultStringOrReason = "Bpms connection runtime error"
)
public class BpmsConnectionException extends RuntimeException {

  public BpmsConnectionException(Throwable cause) {
    super(cause);
  }

}
