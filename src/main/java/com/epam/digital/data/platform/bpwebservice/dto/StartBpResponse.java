package com.epam.digital.data.platform.bpwebservice.dto;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

/**
 * Class that represents response envelope body for startBp operation
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    namespace = Constants.NAMESPACE,
    name = StartBpResponse.START_BP_RESPONSE_NAME
)
@Data
public class StartBpResponse {

  public static final String START_BP_RESPONSE_NAME = "startBpResponse";

  @XmlElement(required = true)
  private Map<String, String> resultVariables;
}
