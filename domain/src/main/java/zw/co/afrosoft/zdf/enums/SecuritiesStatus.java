package zw.co.afrosoft.zdf.enums;
import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Status of the securities transaction")
public enum SecuritiesStatus {
    @Schema(description = "Securities payment has been fully paid")
    PAID,

    @Schema(description = "Securities payment is pending")
    PENDING,

    @Schema(description = "Securities payment is overdue")
    OVERDUE,

    @Schema(description = "Securities payment amount exceeds the required amount")
    OVER_PAID,

    @Schema(description = "Securities account is closed")
    CLOSED
}



