package zw.co.afrosoft.zdf.member;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Type of service branch the member belongs to")
public enum ServiceType {

    @Schema(description = "Zimbabwe National Army")
    ZIMBABWE_NATIONAL_ARMY,

    @Schema(description = "Air Force of Zimbabwe")
    AIR_FORCE_OF_ZIMBABWE
}
