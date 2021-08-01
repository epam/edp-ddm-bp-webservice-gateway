package com.epam.digital.data.platform.bpwebservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception that should be thrown when user tries start business process with some missed required
 * business processes.
 * <p>
 * Is mapped to a soap fault with code CLIENT
 */
@SoapFault(
    faultCode = FaultCode.CLIENT,
    faultStringOrReason = "Missed required business process input parameter"
)
public class MissedRequiredBusinessProcessInputParameterException extends RuntimeException {

}
