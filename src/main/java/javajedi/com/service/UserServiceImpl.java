package javajedi.com.service;

import javajedi.com.data.UserData;
import javajedi.com.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserFeignClient userFeignClient;

    @Override
    public List<UserData> findAllUsers() {
        return userFeignClient.getAllUsers().getBody();
    }

    @Override
    public UserData findUserByUsername(String username) {
       return userFeignClient.getUserByUsername(username).getBody();
    }

}
