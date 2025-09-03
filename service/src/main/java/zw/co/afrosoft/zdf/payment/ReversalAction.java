package zw.co.afrosoft.zdf.payment;

import io.swagger.v3.oas.annotations.media.Schema;



/**
 * Represents the type of reversal action that can be applied to a transaction.
 */
@Schema(description = "The type of reversal action that can be applied to a transaction.")
public enum ReversalAction {

    @Schema(description = "Reversal of a loan repayment transaction.")
    LOAN,

    @Schema(description = "Reversal of a project repayment transaction.")
    PROJECT,

    @Schema(description = "Reversal of a subscription transaction.")
    SUBSCRIPTION,

    @Schema(description = "Reversal of a securities-related transaction.")
    SECURITIES
}
