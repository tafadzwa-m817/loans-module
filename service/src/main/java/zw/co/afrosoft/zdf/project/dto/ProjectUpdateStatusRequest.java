package zw.co.afrosoft.zdf.project.dto;

import lombok.Builder;
import lombok.Data;
import zw.co.afrosoft.zdf.enums.ProjectStatus;import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Builder
@Schema(name = "ProjectUpdateStatusRequest", description = "Request to update the status of a project")
public class ProjectUpdateStatusRequest {

        @Schema(description = "ID of the project to update", example = "123")
        private Long projectId;

        @Schema(description = "New status for the project", example = "ACTIVE")
        private ProjectStatus status;

        @Schema(description = "Optional comment regarding the status update", example = "Project is now active")
        private String comment;
}

