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

import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.dto.factory.ObjectFactory;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import javax.xml.bind.JAXBElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StartBpEndpointTest {

  @Mock
  private StartBpService service;
  @Mock
  private ObjectFactory objectFactory;
  @InjectMocks
  private StartBpEndpoint startBpEndpoint;

  @Mock
  private JAXBElement<StartBpResponse> startBpResponseJAXBElement;

  @Test
  public void startBp() {
    var startBpRequest = new StartBpRequest();
    var startBpResponse = new StartBpResponse();
    when(service.startBp(startBpRequest)).thenReturn(startBpResponse);
    when(objectFactory.createStartBpResponse(startBpResponse))
        .thenReturn(startBpResponseJAXBElement);

    var result = startBpEndpoint.startBp(startBpRequest);

    assertThat(result).isSameAs(startBpResponseJAXBElement);
  }
}
