package zw.co.afrosoft.zdf.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import zw.co.afrosoft.zdf.util.Audit;

import java.util.List;


@Builder
@Schema(description = "Response returned after bulk uploading payments")
public record ParticipantBulkUploadResponse(

        @Schema(description = "List of individual upload responses (success/failure details)")
        List<ParticipantUploadResponse> uploads,

        @Schema(description = "Number of successfully uploaded payment records", example = "120")
        Long succeededUploads,

        @Schema(description = "Number of failed upload records", example = "5")
        Long failedUploads,

        @Schema(description = "The type of bulk upload action performed", example = "PAYMENT_UPLOAD")
        String action,

        @Schema(description = "Audit information such as timestamps and user actions")
        Audit audit
) {}
