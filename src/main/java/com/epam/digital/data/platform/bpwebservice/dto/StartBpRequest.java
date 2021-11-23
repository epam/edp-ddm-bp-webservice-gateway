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

package com.epam.digital.data.platform.bpwebservice.dto;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

/**
 * Class that represents request envelope body for startBp operation
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    namespace = Constants.NAMESPACE,
    name = StartBpRequest.START_BP_REQUEST_NAME
)
@Data
public class StartBpRequest {

  public static final String START_BP_REQUEST_NAME = "startBpRequest";

  @XmlElement(required = true)
  private String businessProcessDefinitionKey;
  @XmlElement
  private Map<String, String> startVariables;
}
