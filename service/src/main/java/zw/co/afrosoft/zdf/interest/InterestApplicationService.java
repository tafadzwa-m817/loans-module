package zw.co.afrosoft.zdf.interest;

import zw.co.afrosoft.zdf.utils.enums.InterestCategory;

import java.time.YearMonth;



public interface InterestApplicationService {
    InterestResponse applyInterest();
    YearMonth getNextExecutionMonth();
}
