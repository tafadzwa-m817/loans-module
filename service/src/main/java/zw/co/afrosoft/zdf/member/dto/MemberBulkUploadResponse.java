package zw.co.afrosoft.zdf.member.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;



@Builder
@Schema(description = "Response object for member bulk upload operation")
public record MemberBulkUploadResponse(

        @Schema(description = "List of upload responses for each member")
        List<MemberUploadResponse> uploads,

        @Schema(description = "Count of successful uploads", example = "25")
        Long succeededUploads,

        @Schema(description = "Count of failed uploads", example = "5")
        Long failedUploads
) {}

