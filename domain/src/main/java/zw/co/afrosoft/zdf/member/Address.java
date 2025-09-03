package zw.co.afrosoft.zdf.member;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;



@Data
@Embeddable
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Address {
    private String addressLine_1;
    private String addressLine_2;
    private String addressLine_3;
    private String addressLine_4;
}
