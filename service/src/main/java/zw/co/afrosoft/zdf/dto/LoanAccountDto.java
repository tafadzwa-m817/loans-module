package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.loans.AccountType;
import zw.co.afrosoft.zdf.loans.Loan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;



@Data
@Schema(name = "LoanAccountDto", description = "DTO representing a member's loan account details")
public class LoanAccountDto {

    @Schema(description = "Unique identifier of the loan account", example = "101")
    private Long id;

    @Schema(description = "Loan account number", example = "LN-ACC-2025-0001")
    private String loanAccountNumber;

    @Schema(description = "Force number of the member associated with the loan account", example = "F12345")
    private String forceNumber;

    @Schema(description = "Membership number linked to the account", example = "M-987654")
    private String membershipNumber;

    @Schema(description = "Type of account")
    private AccountType accountType;

    @Schema(description = "Date and time when the loan account was created", example = "2025-05-22T10:15:30")
    private LocalDateTime dateCreated;

    @Schema(description = "List of loans under this loan account")
    private List<Loan> loans;

    @Schema(description = "Audit details for the loan account")
    private Audit audit;
}

