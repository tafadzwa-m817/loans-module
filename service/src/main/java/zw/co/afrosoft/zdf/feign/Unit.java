package zw.co.afrosoft.zdf.feign;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(name = "Unit", description = "Schema representing a military or organizational unit")
public class Unit {

    @Schema(description = "Name of the unit", example = "1 Infantry Battalion")
    private String name;

    @Schema(description = "Code representing the unit", example = "1IB")
    private String code;

    @Schema(description = "Higher-level formation this unit belongs to", example = "ZNA Formation")
    private String formation;
}
