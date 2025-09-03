package zw.co.afrosoft.zdf.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import zw.co.afrosoft.zdf.enums.LoanStatus;



@Builder
public record LoanUpdateStatusRequest(
        @Schema(
                description = "Loan Id", example = "4"
        )
        Long loanId,
        @Schema(
                description = "Loan status", example = "OPEN"
        )
        LoanStatus loanStatus,
        @Schema(
                description = "Loan comment", example = "Reason for updating"
        )
        String comment
) {
}
