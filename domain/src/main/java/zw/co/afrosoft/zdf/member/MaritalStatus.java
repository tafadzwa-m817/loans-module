package zw.co.afrosoft.zdf.member;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Marital status of the member")
public enum MaritalStatus {
    @Schema(description = "Single marital status")
    SINGLE,

    @Schema(description = "Married marital status")
    MARRIED,

    @Schema(description = "Divorced marital status")
    DIVORCED
}
