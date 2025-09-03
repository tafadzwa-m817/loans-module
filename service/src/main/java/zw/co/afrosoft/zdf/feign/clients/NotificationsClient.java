package zw.co.afrosoft.zdf.feign.clients;

import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import zw.co.afrosoft.zdf.feign.configs.NotificationsFeignConfig;
import zw.co.afrosoft.zdf.notification.NotificationRequest;

@FeignClient(configuration = NotificationsFeignConfig.class)
public interface NotificationsClient {
    @RequestLine("POST/notifications/send-notifications")
    @Headers("Content-Type: application/json")
    void sendNotifications(@RequestBody NotificationRequest notificationsRequest);
}
