package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/28/25
 */

@Builder
@Schema(description = "Details of securities grouped by status and associated totals")
public record SecurityDetails(

        @Schema(description = "The status of the security", example = "ACTIVE")
        SecuritiesStatus status,

        @Schema(description = "List of totals per currency or grouping")
        List<Totals> totals

) {
}

