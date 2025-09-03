package zw.co.afrosoft.zdf.feign;

import lombok.Data;
import zw.co.afrosoft.zdf.utils.enums.InterestCategory;



@Data
public class Interest {
    private Long id;
    private String name;
    private Double interestPercentage;
    private Boolean isActive;
    private Boolean isDeleted;
    private InterestCategory interestCategory;

}
