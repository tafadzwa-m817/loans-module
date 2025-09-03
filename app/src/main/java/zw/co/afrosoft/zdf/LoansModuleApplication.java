package zw.co.afrosoft.zdf;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@SecurityScheme(name = "authorization",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@OpenAPIDefinition(
        info = @Info(
                title = "Zdf Loans service REST API Documentation",
                description = "Zdf Loans microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Tafadzwa",
                        email = "mushipetafafdzwa99@gmail.com"
                ),
                license = @License(
                        name = "Mhofu Holdings"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Zdf Loans microservice REST API Documentation",
                url = "http://192.168.10.40:7200/swagger-ui/index.html"
        )
)
public class LoansModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoansModuleApplication.class, args);
    }

}
