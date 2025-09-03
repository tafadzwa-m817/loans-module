package zw.co.afrosoft.zdf.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;



@Component
public class StaticContextAccessor implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }
}

