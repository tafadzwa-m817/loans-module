package zw.co.afrosoft.zdf.notification;

import lombok.Builder;

@Builder
public record NotificationResponse(String status,
                                   String message) {
}
