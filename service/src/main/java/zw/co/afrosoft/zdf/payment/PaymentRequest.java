package zw.co.afrosoft.zdf.payment;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



@Data
@Schema(name = "PaymentRequest", description = "Schema for submitting a payment")
public class PaymentRequest {

    @NotBlank(message = "Force number is required")
    @Schema(description = "Force number of the member making the payment", example = "98041386P")
    private String forceNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(description = "Payment amount", example = "150.75")
    private Double amount;

    @NotNull(message = "Currency ID is required")
    @Schema(description = "ID of the currency used for payment", example = "1")
    private Long currencyId;

    @Schema(description = "reference ID used to reference what the payment is for", example = "1")
    private Long referenceId;

}
