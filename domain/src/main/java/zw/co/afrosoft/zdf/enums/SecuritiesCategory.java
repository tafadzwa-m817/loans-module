package zw.co.afrosoft.zdf.enums;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "Category of the securities")
public enum SecuritiesCategory {

    @Schema(description = "Securities related to Loans")
    LOAN,

    @Schema(description = "Securities related to Projects")
    PROJECT
}

