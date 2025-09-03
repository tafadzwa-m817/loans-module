package zw.co.afrosoft.zdf.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.feign.dto.Interest;
import zw.co.afrosoft.zdf.utils.enums.InterestCategory;

/**
 * Author Terrance Nyamfukudza
 * Date: 3/13/25
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentService {


    private final ParameterServiceClient parameterServiceClient;


    public Interest retrieveInterestRate(InterestCategory category) {

        try {
            log.info("Retrieving interest rate for category: {}", category.name());
            return parameterServiceClient.getActiveInterest(category.name());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
