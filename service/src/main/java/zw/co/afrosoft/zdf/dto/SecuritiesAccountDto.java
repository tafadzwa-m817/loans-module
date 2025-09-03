package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.loans.Loan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;



@Data
@Schema(description = "DTO representing a securities account with associated loan data")
public class SecuritiesAccountDto {

    @Schema(description = "Unique identifier of the securities account", example = "1001")
    private Long id;

    @Schema(description = "Securities account number", example = "SEC-2025-0001")
    private String securitiesAccountNumber;

    @Schema(description = "Member's force number", example = "98041386P")
    private String forceNumber;

    @Schema(description = "Member's membership number", example = "M123456")
    private String membershipNumber;

    @Schema(description = "Type of loan associated with the securities account", example = "LOAN")
    private LoanType loanType;

    @Schema(description = "Date and time when the securities account was created", example = "2025-05-01T10:30:00")
    private LocalDateTime dateCreated;

    @Schema(description = "List of loans linked to the securities account")
    private List<Loan> loans;

    @Schema(description = "Audit metadata including who created/modified and when")
    private Audit audit;
}

