package zw.co.afrosoft.zdf.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for sending notifications")
public class NotificationRequest {

    @Schema(description = "Notification content/body", example = "Your loan has been approved.")
    private String content;

    @Schema(description = "Subject of the notification", example = "Loan Approval")
    private String subject;

    @Schema(description = "Optional link associated with the notification", example = "https://app.example.com/loan/123")
    private String messageLink;

    @Schema(description = "List of recipients who will receive the notification")
    private List<Recipient> recipient;

    @Schema(description = "Flag indicating if the notification should be sent via email", example = "true")
    private boolean isEmail;

    @Schema(description = "Flag indicating if the notification should be sent via SMS", example = "false")
    private boolean isSMS;

    @Schema(description = "Flag indicating if the notification should be sent via push notification", example = "true")
    private boolean isPush;
}
