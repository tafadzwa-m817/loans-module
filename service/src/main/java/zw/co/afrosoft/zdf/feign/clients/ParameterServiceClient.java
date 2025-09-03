package zw.co.afrosoft.zdf.feign.clients;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import zw.co.afrosoft.zdf.dto.PageResponse;
import zw.co.afrosoft.zdf.feign.*;
import zw.co.afrosoft.zdf.feign.dto.Interest;
import zw.co.afrosoft.zdf.feign.dto.RankResponseDto;
import zw.co.afrosoft.zdf.feign.dto.UnitResponseDto;
import zw.co.afrosoft.zdf.project.dto.ProjectSubCategory;

import java.util.List;


@FeignClient(configuration = ParametersFeignClientConfig.class)
public interface ParameterServiceClient {

    @RequestLine("GET /project-sub-category/{id}")
    @Headers("Content-Type: application/json")
    ProjectSubCategory getProjectSubCategoryById(@Param("id") Long id);

    @RequestLine("GET /ranks/{id}")
    @Headers("Content-Type: application/json")
    Rank getRankById(@Param("id") Long id);

    @RequestLine("GET /unit/{id}")
    @Headers("Content-Type: application/json")
    ResponseEntity<Unit> getUnitById(@Param("id") Long id);

    @RequestLine("GET /interest/active")
    @Headers("Content-Type: application/json")
    Interest getActiveInterest(@RequestParam("category") String category);

    @RequestLine("GET /transaction-type/{id}")
    @Headers("Content-Type: application/json")
    TransactionType getById(@Param("id") Long id);


    @RequestLine("GET /transaction-type")
    @Headers("Content-Type: application/json")
    PageResponse<TransactionType> getAllTransactionTypes();

    @RequestLine("GET /interest")
    @Headers("Content-Type: application/json")
    PageResponse<Interest> getAll();

    @RequestLine("GET /unit")
    @Headers("Content-Type: application/json")
    PageResponse<UnitResponseDto> getAllUnits();

    @RequestLine("GET /ranks")
    @Headers("Content-Type: application/json")
    PageResponse<RankResponseDto> getAllRanks();

    @RequestLine("GET /currencies/active")
    @Headers("Content-Type: application/json")
    List<Currency> getAllCurrency();
}
