package zw.co.afrosoft.zdf.subscription.dto;

import zw.co.afrosoft.zdf.utils.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;



@Schema(description = "Response details for a single record in a bulk upload operation")
public record UploadResponse(

        @Schema(description = "Force number of the member", example = "ZN123456")
        String forceNumber,

        @Schema(description = "Amount involved in the upload record", example = "1000.00")
        BigDecimal amount,

        @Schema(description = "Status of the upload operation for this record")
        Status status,

        @Schema(description = "Reason for failure if the upload was unsuccessful", example = "Insufficient funds")
        String reason
) {}

