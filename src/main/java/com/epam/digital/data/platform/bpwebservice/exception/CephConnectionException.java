package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when application faced any Ceph related errors.
 * <p>
 * Is mapped to a soap fault with code SERVER
 */
@SoapFault(
    faultCode = FaultCode.SERVER,
    faultStringOrReason = "Ceph connection runtime error"
)
public class CephConnectionException extends RuntimeException {

  public CephConnectionException(Throwable cause) {
    super(cause);
  }

}
