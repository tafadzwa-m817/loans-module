package zw.co.afrosoft.zdf.feign;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;



@Getter
@Schema(description = "Transaction categories for financial records")
public enum TransactionCategory {

    @Schema(description = "Securities Schedule")
    SECURITIES("Securities Schedule"),

    @Schema(description = "Subscription Fees")
    SUBSCRIPTIONS("Subscription Fees"),

    @Schema(description = "Loan Payment")
    LOANS("Loan Payment"),

    @Schema(description = "Project Funding (Payment)")
    PROJECT("Project Funding (Payment)"),

    @Schema(description = "Claim Payment")
    CLAIM("Claim Payment"),

    @Schema(description = "Not Applicable")
    OTHER("N/A"),

    @Schema(description = "Reversal for payment")
    REVERSAL("Reversal for payment"),

    @Schema(description = "Interests for Loans")
    LOANS_INTEREST("Interests for Loans"),

    @Schema(description = "Interests for Project")
    PROJECTS_INTEREST("Interests for Project"),

    @Schema(description = "Interest for Subscriptions")
    SUBSCRIPTIONS_INTEREST("Interest for Subscriptions");

    private final String description;

    TransactionCategory(String description) {
        this.description = description;
    }
}