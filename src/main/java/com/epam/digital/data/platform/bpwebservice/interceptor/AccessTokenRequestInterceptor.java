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

package com.epam.digital.data.platform.bpwebservice.interceptor;

import com.epam.digital.data.platform.bpwebservice.util.AccessTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Feign interceptor that is used for adding X-Access-Token header to all feign requests
 */
@Component
public class AccessTokenRequestInterceptor implements RequestInterceptor {

  @Autowired
  private AccessTokenProvider accessTokenProvider;

  @Override
  public void apply(RequestTemplate template) {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    var token = Objects.isNull(auth) ?
        accessTokenProvider.getToken() : (String) auth.getCredentials();
    template.header("X-Access-Token", token);
  }
}
