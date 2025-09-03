package zw.co.afrosoft.zdf.project.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;



@Builder
@Schema(name = "ProjectRequest", description = "Request payload for creating or updating a project")
public record ProjectRequest(

        @Schema(description = "Name of the project", example = "Green Valley Project")
        String projectName,

        @Schema(description = "ID of the province", example = "10")
        Long provinceId,

        @Schema(description = "ID of the town", example = "20")
        Long townId,

        @Schema(description = "Farm name or identifier", example = "Sunny Farm")
        String farm,

        @Schema(description = "Developer name", example = "ABC Developers")
        String developer,

        @Schema(description = "Number of stands in the project", example = "50")
        Integer numberOfStands,

        @Schema(description = "Date of purchase", example = "2023-09-15", type = "string", format = "date")
        LocalDate dateOfPurchase,

        @Schema(description = "Total project cost price", example = "5000000.00")
        BigDecimal projectCostPrice,

        @Schema(description = "Total area in square meters", example = "10000.5")
        Double totalAreaInSqm,

        @Schema(description = "Cost price per square meter", example = "500.00")
        BigDecimal costPricePerSqm,

        @Schema(description = "Category per square meter", example = "RESIDENTIAL")
        Category categoryPerSqm,

        @Schema(description = "Currency ID", example = "1")
        Long currencyId
) {}

