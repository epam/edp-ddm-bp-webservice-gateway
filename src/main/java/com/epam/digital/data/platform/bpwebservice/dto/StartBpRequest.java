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
