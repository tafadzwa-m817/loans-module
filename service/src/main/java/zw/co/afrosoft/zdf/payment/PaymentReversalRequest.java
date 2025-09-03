package zw.co.afrosoft.zdf.payment;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "Request payload for reversing a payment transaction.")
public class PaymentReversalRequest {

    @NotBlank(message = "Force number is required")
    @Schema(description = "Unique force number identifying the member", example = "ZNA123456")
    private String forceNumber;

    @NotNull(message = "Reversal amount is required")
    @Positive(message = "Reversal amount must be a positive number")
    @Schema(description = "Amount to be reversed", example = "150.75")
    private Double reversalAmount;

    @NotNull(message = "Transaction ID is required")
    @Schema(description = "ID of the transaction to be reversed", example = "1024")
    private Long transactionId;

    @NotNull(message = "Currency ID is required")
    @Schema(description = "ID of the currency associated with the transaction", example = "1")
    private Long currencyId;
}

