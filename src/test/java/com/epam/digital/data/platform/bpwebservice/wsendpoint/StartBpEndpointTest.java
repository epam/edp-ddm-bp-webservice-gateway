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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.bpwebservice.dto.StartBpDto;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.dto.soap.StartBpSoapRequest;
import com.epam.digital.data.platform.bpwebservice.dto.soap.factory.ObjectFactory;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartBpEndpointTest {

  @Mock
  private StartBpService service;
  @Mock
  private ObjectFactory objectFactory;
  @InjectMocks
  private StartBpEndpoint startBpEndpoint;

  @Captor
  private ArgumentCaptor<StartBpDto> startBpDtoArgumentCaptor;

  @Mock
  private JAXBElement<StartBpResponse> startBpResponseJAXBElement;

  @Test
  void startBp() {
    var businessProcessDefinitionKey = "businessProcessDefinitionKey";
    var startVariables = Map.of("startVar", "startValue");

    var startBpRequest = new StartBpSoapRequest();
    startBpRequest.setBusinessProcessDefinitionKey(businessProcessDefinitionKey);
    startBpRequest.setStartVariables(startVariables);
    var startBpResponse = new StartBpResponse();
    when(service.startBp(startBpDtoArgumentCaptor.capture())).thenReturn(startBpResponse);
    when(objectFactory.createStartBpResponse(startBpResponse))
        .thenReturn(startBpResponseJAXBElement);

    var result = startBpEndpoint.startBp(startBpRequest);

    assertThat(result).isSameAs(startBpResponseJAXBElement);
    assertThat(startBpDtoArgumentCaptor.getValue()).isNotNull()
        .hasFieldOrPropertyWithValue("businessProcessDefinitionKey", businessProcessDefinitionKey)
        .hasFieldOrPropertyWithValue("startVariables", startVariables);
  }
}
