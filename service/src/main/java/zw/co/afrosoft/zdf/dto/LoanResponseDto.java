package zw.co.afrosoft.zdf.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.member.PersonalDetails;

import java.time.LocalDateTime;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LoanResponseDto", description = "Schema to hold successful loan response information")
public class LoanResponseDto {

    @Schema(description = "Loan unique ID", example = "123")
    private Long id;

    @Schema(description = "Member force number", example = "98041386P")
    private String forceNumber;

    @Schema(description = "Loan advance disbursed", example = "2000.00")
    private Double loanAdvance;

    @Schema(description = "Member personal details")
    private PersonalDetails personalDetails;

    @Schema(description = "Member rank ID", example = "5")
    private Long rankId;

    @Schema(description = "Member unit ID", example = "10")
    private Long unitId;

    @Schema(description = "Current loan balance", example = "1500.00")
    private Double balance;

    @Schema(description = "Amount paid towards loan", example = "500.00")
    private Double amountPaid;

    @Schema(description = "Loan currency ID", example = "1")
    private Long currencyId;

    @Schema(description = "Over-paid loan amount", example = "50.00")
    private Double overPaidAmount;

    @Schema(description = "Loan number", example = "LN-2023-001")
    private String loanNumber;

    @Schema(description = "Loan duration in days", example = "30")
    private int duration;

    @Schema(description = "Loan due date", example = "2025-06-30T23:59:59", format = "date-time")
    private LocalDateTime dueDate;

    @Schema(description = "Loan status", example = "ACTIVE")
    private LoanStatus loanStatus;

    @Schema(description = "Interest accrued to date", example = "150.00")
    private Double interestToDate;

    @Schema(description = "Total interest on loan", example = "300.00")
    private Double totalInterest;

    @Schema(description = "Interest amount for the current period", example = "20.00")
    private Double interestAmount;

    @Schema(description = "Is the loan defaulted?", example = "false")
    private boolean isDefaulted;

    @Schema(description = "Loan closure date", example = "2025-07-15T12:00:00", format = "date-time")
    private LocalDateTime dateClosed;

    @Schema(description = "Type of loan", example = "LOAN")
    private LoanType loanType;

    @Schema(description = "Audit details")
    private Audit audit;
}
