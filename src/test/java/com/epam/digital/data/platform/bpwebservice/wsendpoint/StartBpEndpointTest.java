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
