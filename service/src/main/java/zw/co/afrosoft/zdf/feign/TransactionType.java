package zw.co.afrosoft.zdf.feign;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(name = "TransactionType", description = "Schema representing a type of transaction")
public class TransactionType {

    @Schema(description = "Unique identifier for the transaction type", example = "101")
    private Long id;

    @Schema(description = "Name of the transaction type", example = "Loan Repayment")
    private String name;

    @Schema(description = "Detailed description of the transaction type", example = "Monthly loan repayment by member")
    private String description;

    @Schema(description = "Transaction code", example = "TXN_LOAN_REPAY")
    private String code;

    private Boolean subscriptionEffect;
    private String subsAccountEffect;
    private Boolean loansEffect;
    private String loansAccountEffect;
    private Boolean projectEffect;
    private String projectAccountEffect;
    private Boolean securitiesEffect;
    private String securitiesAccountEffect;
    @Schema(description = "Category of the transaction")
    private TransactionCategory transactionCategory;
}

