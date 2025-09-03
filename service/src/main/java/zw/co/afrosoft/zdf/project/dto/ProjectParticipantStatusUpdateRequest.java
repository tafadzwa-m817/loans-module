package zw.co.afrosoft.zdf.project.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import io.swagger.v3.oas.annotations.media.Schema;



@Builder
@Schema(name = "ProjectParticipantStatusUpdateRequest", description = "Request to update the status of a project participant")
public record ProjectParticipantStatusUpdateRequest(

        @Schema(description = "ID of the project participant", example = "1001")
        Long projectParticipantId,

        @Schema(description = "New status of the project participant", example = "ACTIVE")
        ProjectParticipantStatus status,

        @Schema(description = "Optional comment regarding the status update", example = "Approved by management")
        String comment
) {}

