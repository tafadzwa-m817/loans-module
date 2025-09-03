package zw.co.afrosoft.zdf.generalledger;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountingEntryRequest {


    @NotBlank(message = "Reference id is required")
    @Size(max = 100, message = "Reference id must have at most 100 characters")
    private String referenceId;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;

    @NotNull(message = "Entries are required")
    private List<AccountingEntry> entries;

    private String currency;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountingEntry {

        @NotNull(message = "Debit account name is required")
        private String debitAccountName;

        @NotNull(message = "Credit account name is required")
        private String creditAccountName;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
        private BigDecimal amount;
    }
}