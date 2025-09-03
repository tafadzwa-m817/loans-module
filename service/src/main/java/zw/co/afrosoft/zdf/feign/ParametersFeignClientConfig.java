package zw.co.afrosoft.zdf.feign;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;


@Configuration
public class ParametersFeignClientConfig {

    @Value("${parameters.url}")
    private String url;
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
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ParameterServiceClient parameterClient(FeignClientInterceptor feignClientInterceptor) {
        return Feign.builder()
                .encoder(feignEncoder())
                .decoder(feignDecoder())
                .logLevel(feignLoggerLevel())
                .requestInterceptor(feignClientInterceptor)
                .contract(feignContract())
                .target(ParameterServiceClient.class, url);
    }
}
