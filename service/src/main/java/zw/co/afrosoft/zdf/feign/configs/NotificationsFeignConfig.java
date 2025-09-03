package zw.co.afrosoft.zdf.feign.configs;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zw.co.afrosoft.zdf.feign.FeignClientInterceptor;
import zw.co.afrosoft.zdf.feign.JacksonDecoder;
import zw.co.afrosoft.zdf.feign.JacksonEncoder;
import zw.co.afrosoft.zdf.feign.clients.NotificationsClient;

@Configuration
public class NotificationsFeignConfig {
    @Value("${notifications-service.url}")
    private String tokenUrl;

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder();
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
    public NotificationsClient notificationsClientService(FeignClientInterceptor feignClientInterceptor) {
        return Feign.builder()
                .encoder(feignEncoder())
                .decoder(feignDecoder())
                .logLevel(feignLoggerLevel())
                .requestInterceptor(feignClientInterceptor)
                .contract(feignContract())
                .target(NotificationsClient.class, tokenUrl);
    }
}
