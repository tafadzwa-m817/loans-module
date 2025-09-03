package zw.co.afrosoft.zdf.securities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.afrosoft.zdf.dto.AppResponseDto;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;

import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toAppResponseDtoList;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/securities")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Zdf Loans and Projects Securities", description = "REST APIs for Loans and Projects Securities")
public class SecuritiesRestController {

    private final SecuritiesService securityService;

    @Operation(
            summary = "Fetch All Loans Securities REST API",
            description = "REST API to fetch all Loans securities"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<AppResponseDto>> getAllSecurities(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String loanNumber,
            @RequestParam(required = false) String forceNumber,
            @RequestParam(required = false) Long currencyId,
            @RequestParam(required = false) SecuritiesStatus securitiesStatus) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Securities> securitiesPage = securityService.getAllSecurityTransactions(
                loanNumber, forceNumber, currencyId, securitiesStatus, pageable);

        var dtoList = toAppResponseDtoList(securitiesPage.getContent());
        Page<AppResponseDto> dtoPage = new PageImpl<>(dtoList, pageable, securitiesPage.getTotalElements());

        return ResponseEntity.ok(dtoPage);
    }
}
