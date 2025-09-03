package zw.co.afrosoft.zdf.project.dto;

import zw.co.afrosoft.zdf.entity.Audit;
import io.swagger.v3.oas.annotations.media.Schema;



@Schema(name = "ProjectSubCategory", description = "Represents a subcategory of a project")
public record ProjectSubCategory(

        @Schema(description = "Unique identifier of the project subcategory", example = "101")
        Long id,

        @Schema(description = "Name of the project subcategory", example = "Residential")
        String name,

        @Schema(description = "Flag indicating if the subcategory is active", example = "true")
        Boolean isActive,

        @Schema(description = "Flag indicating if the subcategory is deleted", example = "false")
        Boolean isDeleted,

        @Schema(description = "Reason for deletion or inactivation, if any", example = "Deprecated category")
        String reason,

        @Schema(description = "Audit information related to the subcategory")
        Audit audit
) {}

