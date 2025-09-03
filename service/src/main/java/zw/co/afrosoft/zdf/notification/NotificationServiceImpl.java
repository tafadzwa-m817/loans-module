package zw.co.afrosoft.zdf.notification;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.feign.clients.NotificationsClient;

import java.util.List;
import java.util.stream.Collectors;



@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl {

    private final NotificationsClient notificationsClient;

    @Async
    public void sendNotification(String message, List<Recipient> recipients, String subject,
                                 boolean isEmail, boolean isSms, boolean isPush) {
        log.info("Sending notification with subject: {}", subject);
        log.debug("Notification message: {}", message);
        log.debug("Recipients: {}", recipients);
        log.debug("Notification mediums - Email: {}, SMS: {}, Push: {}", isEmail, isSms, isPush);


        NotificationRequest notificationRequest = new NotificationRequest(
                message, subject, "",
                recipients, isEmail, isSms, isPush
        );

        try {
            notificationsClient.sendNotifications(notificationRequest);
            log.info("Notification successfully sent to recipients");
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
    public List<Recipient> getRecipients(List<RecipientRequest> members) {

        List<Recipient> recipients = members.stream()
                .map(member -> {
                    log.debug("Mapping member '{}' with recipient", member.getEmail());
                    return new Recipient(
                            member.getFullName(),
                            member.getPhoneNumber(),
                            member.getEmail(),
                            ""
                    );
                })
                .collect(Collectors.toList());

        log.debug("Recipients generated:: {}", recipients);
        return recipients;
    }

}
