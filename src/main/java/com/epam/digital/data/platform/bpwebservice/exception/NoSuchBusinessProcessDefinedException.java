package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when user tries start business process that is not defined in
 * application properties.
 * <p>
 * Is mapped to a soap fault with code CLIENT
 */
@SoapFault(
    faultCode = FaultCode.CLIENT,
    faultStringOrReason = "No such business process defined"
)
public class NoSuchBusinessProcessDefinedException extends RuntimeException {

}
