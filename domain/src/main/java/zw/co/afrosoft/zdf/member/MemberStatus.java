package zw.co.afrosoft.zdf.member;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Enumeration of possible member statuses")
public enum MemberStatus {
    @Schema(description = "Active member")
    ACTIVE,

    @Schema(description = "Active member who is retired")
    ACTIVE_RETIRED,

    @Schema(description = "Inactive member who is retired")
    INACTIVE_RETIRED,

    @Schema(description = "Inactive member who was discharged")
    INACTIVE_DISCHARGED,

    @Schema(description = "Inactive member (alternative status)")
    IN_ACTIVE,

    @Schema(description = "Inactive member due to death")
    INACTIVE_DEATH
}
