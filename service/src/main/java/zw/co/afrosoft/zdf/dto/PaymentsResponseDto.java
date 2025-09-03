package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.entity.Audit;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "DTO representing a loan payment record")
public class PaymentsResponseDto {

    @Schema(description = "Unique identifier for the payment record", example = "123")
    private Long id;

    @Schema(description = "Force number of the member", example = "98041386P")
    private String forceNumber;

    @Schema(description = "Loan number associated with the payment", example = "LN-2024-0001")
    private String loanNumber;

    @Schema(description = "Amount paid towards the loan", example = "250.00")
    private Double amountPaid;

    @Schema(description = "Audit details for tracking creation and modification metadata")
    private Audit audit;
}

