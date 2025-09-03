package zw.co.afrosoft.zdf.feign.clients;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import zw.co.afrosoft.zdf.dto.AccountCreateRequest;
import zw.co.afrosoft.zdf.dto.AccountResponseDto;
import zw.co.afrosoft.zdf.dto.AccountStatusUpdateRequest;
import zw.co.afrosoft.zdf.dto.PageResponse;
import zw.co.afrosoft.zdf.feign.configs.GLServiceFeignClientConfig;
import zw.co.afrosoft.zdf.generalledger.AccountingEntryRequest;
import zw.co.afrosoft.zdf.subscription.dto.ProcessTransTypeRequest;

import java.util.List;

@FeignClient(configuration = GLServiceFeignClientConfig.class)
public interface GLServiceClient {

    @RequestLine("POST /gl-account-entries")
    @Headers("Content-Type: application/json")
    void postLedgerEntries(@RequestBody List<AccountingEntryRequest> accountingEntryRequests);


    @RequestLine("POST /transactions/transaction-type")
    @Headers("Content-Type: application/json")
    void postTransactionEntries(@RequestBody List<ProcessTransTypeRequest> transactionRequests);

    @RequestLine("POST /gl-accounts")
    @Headers("Content-Type: application/json")
    Object createNewGLAccount(@RequestBody AccountCreateRequest request);

    @RequestLine("GET /gl-accounts?currencyId={currencyId}&accountName={accountName}")
    @Headers("Content-Type: application/json")
    PageResponse<AccountResponseDto> getGLAccountByNameAndCurrency(@Param("currencyId") Long currencyId,
                                                                   @Param("accountName") String accountName);

    @RequestLine("PUT /gl-accounts/update-status")
    @Headers("Content-Type: application/json")
    void updateGLAccountStatus(@RequestBody AccountStatusUpdateRequest updateRequest);
}

