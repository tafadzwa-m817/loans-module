package zw.co.afrosoft.zdf.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.afrosoft.zdf.enums.LoanType;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanRequestDto", description = "Schema to hold Loan information")
public class LoanRequestDto {

    @NotEmpty(message = "Force Number cannot be null or empty")
    @Schema(description = "Force Number of Member", example = "98041386P")
    private String forceNumber;

    @NotNull(message = "Principal Loan amount cannot be null")
    @Schema(description = "Principal Loan amount credited", example = "200.0")
    private Double principalAmount;

    @NotNull(message = "Currency cannot be null")
    @Schema(description = "Currency ID", example = "1")
    private Long currencyId;

    @NotNull(message = "Duration cannot be null")
    @Schema(description = "Duration of the loan in days", example = "30")
    private Integer duration;

    @NotNull(message = "LoanType cannot be null")
    @Schema(description = "Loan type being applied for", example = "LOAN")
    private LoanType loanType;
}
