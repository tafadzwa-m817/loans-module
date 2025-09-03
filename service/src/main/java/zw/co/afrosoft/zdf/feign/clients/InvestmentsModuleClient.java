package zw.co.afrosoft.zdf.feign.clients;

import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.feign.configs.InvestmentsModuleClientConfig;

@FeignClient(configuration = InvestmentsModuleClientConfig.class)
public interface InvestmentsModuleClient {

    @RequestLine("POST /audit-trail")
    @Headers("Content-Type: application/json")
    void createAuditTrail(@RequestBody AuditTrailRequest request);
}
