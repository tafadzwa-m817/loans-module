package zw.co.afrosoft.zdf.interest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;



@Data
@Builder
@ToString
@Schema(name = "InterestResponse",
        description = "Schema to hold InterestResponse information"
)
public class InterestResponse {

    @Schema(
            description = "Success message", example = "Loan interest applied successfully"
    )
    private String message;
    @Schema(
            description = "Number of loans interest has been applied to", example = "6"
    )
    private int numberOfLoansAffected;
    @Schema(
            description = "Number of projects interest has been applied to", example = "6"
    )
    private int numberOfProjectsAffected;
    @Schema(
            description = "Number of subscriptions interest has been applied to", example = "6"
    )
    private int numberOfSubscriptionsAffected;
    @Schema(
            description = "List of records interest has been applied to"
    )
    private String interestRunBy;
    @Schema(
            description = "Date and Time interest was applied"
    )
    private LocalDateTime interestRunAt;
}
