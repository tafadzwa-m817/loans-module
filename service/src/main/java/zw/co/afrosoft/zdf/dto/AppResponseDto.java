package zw.co.afrosoft.zdf.dto;


import lombok.Data;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.SecuritiesCategory;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;



@Data
@Schema(name = "AppResponseDto", description = "DTO representing securities account transaction details")
public class AppResponseDto {

    @Schema(description = "Unique identifier of the transaction", example = "1001")
    private Long id;

    @Schema(description = "Securities account number", example = "SEC-2025-0001")
    private String securitiesAccountNumber;

    @Schema(description = "Force number associated with the member", example = "F12345")
    private String forceNumber;

    @Schema(description = "Loan number linked to the transaction", example = "LN-98765")
    private String loanNumber;

    @Schema(description = "Currency identifier", example = "1")
    private Long currencyId;

    @Schema(description = "Amount paid towards securities", example = "1500.00")
    private Double securitiesAmountPaid;

    @Schema(description = "Total securities amount", example = "5000.00")
    private Double securitiesAmount;

    @Schema(description = "Remaining balance after transaction", example = "3500.00")
    private Double balance;

    @Schema(description = "Date and time when the transaction occurred", example = "2025-05-22T14:30:00")
    private LocalDateTime transactionDate;

    @Schema(description = "Flag indicating if the securities are fully paid", example = "true")
    private Boolean isPaid;

    @Schema(description = "Current status of the securities")
    private SecuritiesStatus securitiesStatus;

    @Schema(description = "Category of the securities")
    private SecuritiesCategory securitiesCategory;

    @Schema(description = "Audit information for the transaction")
    private Audit audit;

}

