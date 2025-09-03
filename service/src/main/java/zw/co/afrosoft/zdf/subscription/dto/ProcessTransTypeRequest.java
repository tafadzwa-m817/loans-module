package zw.co.afrosoft.zdf.subscription.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;



@Builder
@Schema(description = "Request object for processing a transaction by type")
public record ProcessTransTypeRequest(

        @Schema(description = "Unique code identifying the transaction type", example = "LOAN_REPAYMENT")
        String transactionTypeCode,

        @Schema(description = "Amount to be processed for the transaction", example = "1500.00")
        BigDecimal amount,

        @Schema(description = "Detailed description of the transaction", example = "Monthly loan repayment for May")
        String description,

        @Schema(description = "Reference string associated with this transaction", example = "LOAN#12345-MAY")
        String reference

) {}

