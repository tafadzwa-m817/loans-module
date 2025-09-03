package zw.co.afrosoft.zdf.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import zw.co.afrosoft.zdf.utils.enums.Status;

import java.math.BigDecimal;


@Schema(description = "Response details for a single project participant record in a bulk upload operation")
public record ParticipantUploadResponse(

        @Schema(description = "Force number of the member", example = "ZN123456")
        String forceNumber,

        @Schema(description = "Stand number of the member", example = "67456")
        String standNumber,

        @Schema(description = "Square metres allocated to the member", example = "500")
        Double squareMeters,

        @Schema(description = "Cost price", example = "1000.00")
        BigDecimal costPrice,

        @Schema(description = "Interest charged in the upload record", example = "1000.00")
        Double interestCharged,// interest charges per 5 years/60 months with option to change

        @Schema(description = "Fund cost price involved in the upload record", example = "1000.00")
        BigDecimal fundCostPrice,

        @Schema(description = "Status of the upload operation for this record")
        Status status,

        @Schema(description = "Reason for failure if the upload was unsuccessful", example = "Insufficient funds")
        String reason
        ) {
}
