package zw.co.afrosoft.zdf.enums;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Status of the loan")
public enum LoanStatus {

    @Schema(description = "Loan has been fully paid")
    PAID,

    @Schema(description = "Loan is currently active/open")
    OPEN,

    @Schema(description = "Loan has been closed")
    CLOSED,

    @Schema(description = "Loan is pending processing")
    PENDING,

    @Schema(description = "Loan payment is overdue")
    OVERDUE,

    @Schema(description = "Loan has been approved")
    APPROVED,

    @Schema(description = "Loan has been rejected")
    REJECTED,

    @Schema(description = "Loan is in default")
    DEFAULTED,

    @Schema(description = "Loan amount paid exceeds balance")
    OVER_PAID,

    @Schema(description = "Loan is waiting for approval")
    WAIT_APPROVAL
}
