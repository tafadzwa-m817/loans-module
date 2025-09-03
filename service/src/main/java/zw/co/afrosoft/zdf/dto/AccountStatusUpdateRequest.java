package zw.co.afrosoft.zdf.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;



@Builder
@Schema(name = "AccountStatusUpdateRequest", description = "Request to update the status of an account")
public record AccountStatusUpdateRequest(

        @Schema(description = "Unique identifier of the account to update", example = "123")
        Long accountId,

        @Schema(description = "New status to set for the account", example = "INACTIVE")
        String status,

        @Schema(description = "Optional comment regarding the status update", example = "Account closed due to inactivity")
        String comment

) {
}

