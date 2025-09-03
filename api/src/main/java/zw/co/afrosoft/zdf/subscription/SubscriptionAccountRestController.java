package zw.co.afrosoft.zdf.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.utils.constants.Constants;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toSubscriptionsAccountDto;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toSubscriptionsAccountDtoList;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/subscription-accounts")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Zdf Subscriptions", description = "REST APIs to CREATE, UPDATE, FETCH and VIEW Member subscriptions")

public class SubscriptionAccountRestController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Adding Currency To Subscription Account",
            description = "REST API to add currency subscription account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Member details updated successfully!"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PatchMapping
    private ResponseEntity<SubscriptionsAccountDto> addCurrency(@RequestParam Long id,
                                                                @RequestParam Long currencyId){
        return ResponseEntity.status(OK)
                .body(toSubscriptionsAccountDto(subscriptionService.addCurrency(id, currencyId)));
    }

    @Operation(
            summary = "Update Bulk Member Details For A Given Subscription Account REST API",
            description = "REST API to update bulk member details for subscription account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Members details updated successfully!"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PatchMapping("/upload")
    private ResponseEntity<List<SubscriptionsAccountDto>> updateMemberDetailsFromFile(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("forceNumbers") List<String> forceNumbers) {
        return ResponseEntity.status(OK).body(
                toSubscriptionsAccountDtoList(
                        subscriptionService.updateMemberDetailsFromFile(file, forceNumbers)
                ));
    }
    @Operation(
            summary = "Retrieve Subscriptions Account REST API",
            description = "REST API to retrieve subscription member"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400", description = "Subscription Account not found",
                    content = @Content(schema = @Schema(implementation = RecordNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<SubscriptionsAccountDto>> getAllSubscriptionsAccounts(@RequestParam(required = false) String forceNumber,
                                                                                     @RequestParam(required = false) String accountNumber,
                                                                                     @RequestParam(required = false) Long currencyId,
                                                                                     @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                                     @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
                                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        var allSubscriptions = subscriptionService.getAllSubscriptions(forceNumber, accountNumber, currencyId, pageable);
        return ResponseEntity.status(OK)
                .body(new PageImpl<>(toSubscriptionsAccountDtoList(allSubscriptions.getContent()),
                        allSubscriptions.getPageable(), allSubscriptions.getTotalElements()));
    }

    @Operation(
            summary = "Fetch Subscription Account REST API",
            description = "Fetch Subscription Account details by loan ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400", description = "Subscription Account not found",
                    content = @Content(schema = @Schema(implementation = RecordNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })

    @GetMapping("{id}")
    public ResponseEntity<SubscriptionsAccountDto> getSubscription(@PathVariable() Long id){
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

}
