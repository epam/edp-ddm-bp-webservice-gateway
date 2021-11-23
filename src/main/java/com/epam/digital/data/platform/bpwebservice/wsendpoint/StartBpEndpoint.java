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

package com.epam.digital.data.platform.bpwebservice.wsendpoint;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.dto.factory.ObjectFactory;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import javax.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * SOAP endpoint that is used for starting business-process
 */
@Endpoint
@RequiredArgsConstructor
public class StartBpEndpoint {

  private final ObjectFactory objectFactory;
  private final StartBpService service;

  /**
   * Method that is used for wsdl operation generation
   * <p>
   * Is invoked when soap service receives {@link StartBpRequest startBpRequest} message
   * <p>
   * Delegates an invocation to {@link StartBpService#startBp(StartBpRequest)}
   *
   * @param startBpRequest received {@link StartBpRequest startBpRequest} message
   * @return created {@link JAXBElement JAXB structure} over {@link StartBpResponse startBpResponse}
   * message that will be returned as SOAP response envelope
   */
  @PayloadRoot(namespace = Constants.NAMESPACE, localPart = StartBpRequest.START_BP_REQUEST_NAME)
  @ResponsePayload
  public JAXBElement<StartBpResponse> startBp(@RequestPayload StartBpRequest startBpRequest) {
    return objectFactory.createStartBpResponse(service.startBp(startBpRequest));
  }
}
