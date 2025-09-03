package zw.co.afrosoft.zdf.dto;



import zw.co.afrosoft.zdf.util.Audit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;



@Schema(name = "AccountResponseDto", description = "Response DTO containing account information")
public record AccountResponseDto(

        @Schema(description = "Unique identifier of the account", example = "1001")
        Long id,

        @Schema(description = "Name of the account", example = "Development Fund")
        String name,

        @Schema(description = "System-generated or user-defined account code", example = "DF-2025")
        String code,

        @Schema(description = "Account category", example = "Project")
        String category,

        @Schema(description = "Current status of the account", example = "ACTIVE")
        String status,

        @Schema(description = "Currency name or code", example = "USD")
        String currency,

        @Schema(description = "Current balance of the account", example = "12500.75")
        BigDecimal balance,

        @Schema(description = "Type of account", example = "ASSET")
        String accountType,

        @Schema(description = "Financial statement type", example = "BALANCE_SHEET")
        String financialStatementType,

        @Schema(description = "Normal balance type", example = "DEBIT")
        String normalBalance,

        @Schema(description = "Audit metadata for the record")
        Audit audit

) {
}