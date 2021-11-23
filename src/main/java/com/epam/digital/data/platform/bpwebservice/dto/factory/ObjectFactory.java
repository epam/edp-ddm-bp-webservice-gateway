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

package com.epam.digital.data.platform.bpwebservice.dto.factory;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.springframework.stereotype.Component;

/**
 * Class that is used by jaxb2-maven-plugin for creating {@code <element>} tags in the generated xsd
 * file for creating.
 * <p>
 * These {@code <element>} tags are needed for wsdl generator to create {@code <message>} tags in
 * wsdl file (every element produces one wsdl message)
 * <p>
 * Every method in this class is used for created one xsd element
 */
@Component
@XmlRegistry
public class ObjectFactory {

  @XmlElementDecl(namespace = Constants.NAMESPACE, name = StartBpRequest.START_BP_REQUEST_NAME)
  public JAXBElement<StartBpRequest> createStartBpRequest(StartBpRequest value) {
    var qName = new QName(Constants.NAMESPACE, StartBpRequest.START_BP_REQUEST_NAME);
    return new JAXBElement<>(qName, StartBpRequest.class, null, value);
  }

  @XmlElementDecl(namespace = Constants.NAMESPACE, name = StartBpResponse.START_BP_RESPONSE_NAME)
  public JAXBElement<StartBpResponse> createStartBpResponse(StartBpResponse value) {
    var qName = new QName(Constants.NAMESPACE, StartBpResponse.START_BP_RESPONSE_NAME);
    return new JAXBElement<>(qName, StartBpResponse.class, null, value);
  }
}
