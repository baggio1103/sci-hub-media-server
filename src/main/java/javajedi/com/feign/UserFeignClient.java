package javajedi.com.feign;

import javajedi.com.data.UserData;
import javajedi.com.feign.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "media-service", url = "${microservices.media-service.host}",
        configuration = FeignClientConfiguration.class)
public interface UserFeignClient {

    @GetMapping("/users")
    ResponseEntity<List<UserData>> getAllUsers();

    @GetMapping("/users/{username}")
    ResponseEntity<UserData> getUserByUsername(@PathVariable("username") String username);


}
