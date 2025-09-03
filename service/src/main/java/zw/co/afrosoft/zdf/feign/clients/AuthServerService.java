package zw.co.afrosoft.zdf.feign.clients;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import zw.co.afrosoft.zdf.feign.AuthFeignConfiguration;
import zw.co.afrosoft.zdf.security.AuthValidationResponse;
import zw.co.afrosoft.zdf.security.User;

import java.util.List;

@FeignClient(configuration = AuthFeignConfiguration.class)
@Component
public interface AuthServerService {
    @RequestLine("GET/jwt/validate-token")
    AuthValidationResponse validateUser(@RequestBody String token);

    @RequestLine("GET/users/username?username={username}")
    User getUserDetails(@Param("username") String username);

//    @RequestLine("GET/members/force-number/{forceNumber}")
//    Member getByForceNumber(@Param("forceNumber") String forceNumber);

    @RequestLine("GET/users/role/{roleId}")
    List<User> getUsers(@Param Long roleId );
}