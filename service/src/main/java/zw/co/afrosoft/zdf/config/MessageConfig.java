package zw.co.afrosoft.zdf.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Value;

/*
 * created by  Romeo Jerenyama
 * created on  21/2/2025 at 14:03
 */

@Configuration
public class MessageConfig {

    @Value("${app.message.basename:classpath:messages}")
    private String basename;

    @Value("${app.message.encoding:UTF-8}")
    private String encoding;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(encoding);
        return messageSource;
    }
}

