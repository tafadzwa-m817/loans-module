package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.enums.SecuritiesCategory;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "Request DTO to update loan securities payments")
public class UpdateLoanRequestDto {

    @Schema(
            description = "Category of securities (e.g., LOAN or PROJECT)",
            example = "LOAN"
    )
    private SecuritiesCategory category;

    @Schema(
            description = "Amount paid towards the securities",
            example = "150.00"
    )
    private Double paidAmount;
}
