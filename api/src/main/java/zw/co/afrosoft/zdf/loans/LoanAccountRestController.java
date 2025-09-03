package zw.co.afrosoft.zdf.loans;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zw.co.afrosoft.zdf.dto.LoanAccountDto;

import static org.springframework.http.HttpStatus.OK;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toLoanAccountDtoList;



@Tag(
        name = "REST APIs for Zdf Loan Accounts",
        description = "REST APIs to fetch Loan Account details"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/loan-accounts")
@SecurityRequirement(name = "authorization")
public class LoanAccountRestController {

    private final LoanAccountService loanAccountService;

    @GetMapping
    public ResponseEntity<Page<LoanAccountDto>> getLoanAccounts(@RequestParam(value = "forceNumber", required = false) String forceNumber,
                                                                @RequestParam(value = "membershipNumber", required = false) String membershipNumber,
                                                                @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                @RequestParam(defaultValue = "10") Integer pageSize
                                                                ) {
        var pageable = PageRequest.of(pageNumber, pageSize);
        var pagedDto = loanAccountService.getLoanAccounts(forceNumber,membershipNumber,pageable);
        return ResponseEntity.status(OK)
                .body(new PageImpl<>(toLoanAccountDtoList(pagedDto.getContent()),
                        pageable, pagedDto.getTotalElements()));
    }
}
