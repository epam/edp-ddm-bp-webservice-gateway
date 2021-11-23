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

package com.epam.digital.data.platform.bpwebservice.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Base class for all trembita business process properties. Consists of list of {@link
 * BusinessProcessProperties} objects for every {@code trembita.process_definitions} that are
 * defined in application properties
 *
 * @see BusinessProcessProperties
 */
@Component
@ConfigurationProperties(prefix = "trembita")
public class TrembitaBusinessProcessProperties {

  @Setter
  private List<BusinessProcessProperties> processDefinitions;

  private Map<String, BusinessProcessProperties> businessProcessPropertiesMap;

  @PostConstruct
  public void setUp() {
    if (Objects.nonNull(processDefinitions)) {
      businessProcessPropertiesMap = processDefinitions.stream().collect(
          Collectors.toMap(BusinessProcessProperties::getProcessDefinitionId, Function.identity()));
    } else {
      businessProcessPropertiesMap = Map.of();
    }
  }

  public BusinessProcessProperties findBusinessProcessProperties(String processDefinitionId) {
    return businessProcessPropertiesMap.get(processDefinitionId);
  }

  @Override
  public String toString() {
    return "TrembitaBusinessProcessProperties{" +
        "businessProcessPropertiesMap=" + businessProcessPropertiesMap +
        '}';
  }
}
