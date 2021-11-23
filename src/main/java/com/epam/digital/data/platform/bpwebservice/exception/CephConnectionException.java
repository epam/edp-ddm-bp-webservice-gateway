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
