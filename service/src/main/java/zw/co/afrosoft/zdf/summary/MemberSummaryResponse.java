package zw.co.afrosoft.zdf.summary;

import lombok.Builder;
import lombok.Data;
import zw.co.afrosoft.zdf.member.ServiceType;



@Data
@Builder
public class MemberSummaryResponse {
    private String fullName;
    private String idNumber;
    private ServiceType serviceType;
    private double usdLoanBalance;
    private double localLoanBalance;
    private double usdProjectBalance;
    private double localProjectBalance;
    private double usdSubsBalance;
    private double localSubsBalance;
    private double usdSecBalance;
    private double localSecBalance;
}
