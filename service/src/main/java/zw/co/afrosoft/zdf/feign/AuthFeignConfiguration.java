package zw.co.afrosoft.zdf.feign;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zw.co.afrosoft.zdf.feign.clients.AuthServerService;

@Configuration
public class AuthFeignConfiguration {
    @Value("${spring.security.oauth2.resource-server.jwt.issuer-uri}")
    private String tokenUrl;

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder();
    }

    @Bean
    public FeignClientInterceptor feignClientInterceptor() {
        return new FeignClientInterceptor();
    }

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public AuthServerService authServerService(FeignClientInterceptor feignClientInterceptor) {
        return Feign.builder()
                .encoder(feignEncoder())
                .decoder(feignDecoder())
                .logLevel(feignLoggerLevel())
                .requestInterceptor(feignClientInterceptor)
                .contract(feignContract())
                .target(AuthServerService.class, tokenUrl);
    }
}