package zw.co.afrosoft.zdf.subscription;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Status of a member's account")
public enum AccountStatus {

    @Schema(description = "Account is active and in good standing")
    ACTIVE,

    @Schema(description = "Account is inactive but not closed")
    INACTIVE,

    @Schema(description = "Account has been formally closed")
    CLOSED,

    @Schema(description = "Account has been permanently deleted from the system")
    DELETED
}

