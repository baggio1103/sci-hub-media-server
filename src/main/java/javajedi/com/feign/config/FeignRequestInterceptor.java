package javajedi.com.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        var token = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        requestTemplate.header("Authorization", "Bearer " + token);
    }

}
