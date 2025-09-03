package zw.co.afrosoft.zdf.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.utils.enums.InterestCategory;


public record Interest(
        Long id,
        String name,
        Double interestPercentage,
        Boolean isActive,
        Boolean isDeleted,
        InterestCategory interestCategory,
        String reason,
        @JsonIgnore
        Audit audit
) {
}
