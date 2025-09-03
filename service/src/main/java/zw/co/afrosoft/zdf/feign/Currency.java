package zw.co.afrosoft.zdf.feign;

import lombok.Data;

import java.math.BigDecimal;



@Data
public class Currency {

    private Long id;
    private String currencyName;
    private BigDecimal rate;
    private Boolean isActive;
    private Boolean isBaseCurrency;
}

