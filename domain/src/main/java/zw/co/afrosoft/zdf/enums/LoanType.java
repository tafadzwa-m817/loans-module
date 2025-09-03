package zw.co.afrosoft.zdf.enums;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Type of loan")
public enum LoanType {

    @Schema(description = "Standard loan")
    LOAN,

    @Schema(description = "Project-based loan")
    PROJECT
}

