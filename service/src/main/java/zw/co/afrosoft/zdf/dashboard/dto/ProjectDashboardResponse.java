package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/9/25
 */

@Builder
@Schema(description = "Response representing project dashboard summary grouped by status")
public record ProjectDashboardResponse(

        @Schema(description = "The status of the projects", example = "ACTIVE")
        ProjectStatus status,

        @Schema(description = "List of totals per currency or group")
        List<Totals> totals

) {
}

