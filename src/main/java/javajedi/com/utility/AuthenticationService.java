package javajedi.com.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {

    public String getPrincipal() {
        return getContext().getAuthentication().getPrincipal().toString();
    }

}
