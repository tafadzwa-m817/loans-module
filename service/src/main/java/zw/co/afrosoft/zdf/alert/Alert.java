package zw.co.afrosoft.zdf.alert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * created by  Romeo Jerenyama
 * created on  20/3/2025 at 15:56
 */

@Data
@Builder
@ToString
@Schema(description = "Represents an alert with a type, message, timestamp, and related action items")
public class Alert<T> {

    @Schema(description = "Type of the alert, e.g., 'INFO', 'ERROR', 'WARNING'", example = "ERROR")
    private String type;

    @Schema(description = "Detailed alert message", example = "Loan payment overdue for member 12345")
    private String message;

    @Schema(description = "Timestamp when the alert was created", example = "2025-05-27T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "List of action items related to the alert")
    private List<T> actionItems;
}

