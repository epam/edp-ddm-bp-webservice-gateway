package com.epam.digital.data.platform.bpwebservice.interceptor;

import com.epam.digital.data.platform.bpwebservice.util.AccessTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    template.header("X-Access-Token", accessTokenProvider.getToken());
  }
}
