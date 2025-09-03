package zw.co.afrosoft.zdf.dto;

import lombok.Builder;
import lombok.Data;
import zw.co.afrosoft.zdf.claim.ClaimStatus;
import zw.co.afrosoft.zdf.claim.ClaimType;
import zw.co.afrosoft.zdf.claim.ClaimantDetails;
import zw.co.afrosoft.zdf.entity.Audit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;



@Data
@Schema(description = "DTO representing a member's claim details")
public class ClaimDto {

    @Schema(description = "Unique identifier of the claim", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Force number of the member", example = "12345678X")
    private String forceNumber;

    @Schema(description = "First name of the account holder", example = "John")
    private String accountHolderName;

    @Schema(description = "Surname of the account holder", example = "Doe")
    private String accountHolderSurname;

    @Schema(description = "Details of the person making the claim")
    private ClaimantDetails claimantDetails;

    @Schema(description = "Date when the claim was made", example = "2025-05-01")
    private LocalDate claimDate;

    @Schema(description = "Date when the claim was approved", example = "2025-05-01")
    private LocalDate approvedDate;

    @Schema(description = "Retirement date of the member (if applicable)", example = "2024-12-31", nullable = true)
    private LocalDate retirementDate;

    @Schema(description = "USD balance for loan securities", example = "1500.00")
    private Double loanSecuritiesUsdBalance;

    @Schema(description = "Local currency balance for loan securities", example = "450000.00")
    private Double loanSecuritiesLocalBalance;

    @Schema(description = "USD balance for project securities", example = "1200.00")
    private Double projectSecuritiesUsdBalance;

    @Schema(description = "Local currency balance for project securities", example = "300000.00")
    private Double projectSecuritiesLocalBalance;

    @Schema(description = "Total securities balance for loans and projects", example = "570000.00")
    private Double loanProjectSecuritiesBalance;

    @Schema(description = "Outstanding loan usd balance", example = "700.00")
    private Double loanUsdBalance;

    @Schema(description = "Outstanding loan local currency balance", example = "1230.00")
    private Double loanLocalBalance;

    @Schema(description = "Outstanding project usd balance", example = "500.00")
    private Double projectUsdBalance;

    @Schema(description = "Outstanding project local currency balance", example = "1500.00")
    private Double projectLocalBalance;

    @Schema(description = "Combined balance of loans and projects in local currency", example = "1200.00")
    private Double totalLoanProjectBalance;

    @Schema(description = "Total amount owed by the member", example = "1700.00")
    private Double totalOwing;

    @Schema(description = "USD subscription balance", example = "2000.00")
    private Double subscriptionUSDBalance;

    @Schema(description = "Local currency subscription balance", example = "600000.00")
    private Double subscriptionLocalCurrencyBalance;

    @Schema(description = "Total subscription balance", example = "602000.00")
    private Double subscriptionTotalBalance;

    @Schema(description = "Amount being claimed", example = "500.00")
    private Double claimAmount;

    @Schema(description = "Currency ID used in the claim", example = "1")
    private Long currencyId;

    @Schema(description = "Rate used in processing the claim", example = "0.12")
    private Double rate;

    @Schema(description = "Type of the claim", example = "DEATH_CLAIM")
    private ClaimType claimType;

    @Schema(description = "Status of the claim", example = "APPROVED")
    private ClaimStatus claimStatus;

    @Schema(description = "Audit metadata (timestamps, users, etc.)", accessMode = Schema.AccessMode.READ_ONLY)
    private Audit audit;
}
