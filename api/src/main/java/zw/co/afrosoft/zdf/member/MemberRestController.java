package zw.co.afrosoft.zdf.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.logs.MemberStatusLogs;
import zw.co.afrosoft.zdf.member.dto.MemberBulkUploadResponse;
import zw.co.afrosoft.zdf.member.request.MemberFilterRequest;
import zw.co.afrosoft.zdf.member.request.MemberStatusUpdateRequest;
import zw.co.afrosoft.zdf.utils.constants.Constants;
import zw.co.afrosoft.zdf.utils.enums.BulkMembership;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toMemberResponseDto;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toMemberResponseDtoList;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/member")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Zdf Membership", description = "REST APIs to CREATE, UPDATE, FETCH and VIEW Member details")
public class MemberRestController {

    private final MemberService memberService;


    @Operation(
            summary = "Register Member REST API",
            description = "REST API to register a member"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Member registered successfully!"
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
    @PostMapping
    ResponseEntity<MemberResponseDto> registerMember(@Valid @RequestBody MemberRegistrationRequest request){
        return ResponseEntity.status(CREATED)
                .body(toMemberResponseDto(memberService.registerMember(request)));
    }

    @Operation(
            summary = "Activate Member REST API",
            description = "REST API to activate member"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Member activated successfully!"
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
    @PutMapping("/activation")
    public ResponseEntity<MemberResponseDto> memberValidation(@RequestParam Long id){
        return ResponseEntity.status(OK)
                .body(toMemberResponseDto(memberService.activateMember(id)));
    }

    @Operation(
            summary = "Fetch All Members REST API ",
            description = "REST API to fetch all members or filter to get single record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
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
    @GetMapping
    public ResponseEntity<Page<MemberResponseDto>> getMembers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String forceNumber,
            @RequestParam(required = false) String membershipNumber,
            @RequestParam(required = false) MemberStatus memberStatus,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());

        MemberFilterRequest request = new MemberFilterRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setForceNumber(forceNumber);
        request.setMembershipNumber(membershipNumber);
        request.setMemberStatus(memberStatus);

        Page<Member> memberPage = memberService.getMembers(request, pageable);

        return ResponseEntity.ok(
                new PageImpl<>(
                        toMemberResponseDtoList(memberPage.getContent()),
                        memberPage.getPageable(),
                        memberPage.getTotalElements()
                )
        );
    }


    @Operation(
            summary = "Update Member REST API ",
            description = "REST API to update member with the given ID")
    @PutMapping("{id}")
    public ResponseEntity<MemberResponseDto> updateMember(
            @Parameter(required = true)
            @PathVariable Long id,
           @Valid @RequestBody MemberRegistrationRequest request) {
        return ResponseEntity.ok(memberService.update(id, request));
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get Member REST API ",
            description = "Retrieves a member by the given id")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id){
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @Operation(
            summary = "Update Member Status REST API ",
            description = "REST API to update member status with the given ID")
    @PutMapping("status")
    public ResponseEntity<MemberResponseDto> updateMemberStatus(
            @Valid @RequestBody MemberStatusUpdateRequest request) {
        return ResponseEntity.ok(memberService.updateStatus(request));
    }

    @Operation(
            summary = "Get all status updates for a specific member",
            description = "Retrieve paginated member status updates based on member ID and status")
    @GetMapping("/status-updates")
    public Page<MemberStatusLogs> getAllStatusUpdates(
            @RequestParam @Nullable Long memberId,
            @RequestParam @Nullable MemberStatus status,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION)
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        return memberService.getAllStatusUpdates(memberId, status, pageable);
    }

    @GetMapping("member-statuses")
    @Operation(description = "Returns a List of member statuses")
    public List<MemberStatus> getAllPaymentTypes() {
        return Arrays.asList(MemberStatus.values());
    }


    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload Membership Excel File REST API ",
            description = "Upload Excel file to update or create member details")

    public ResponseEntity<MemberBulkUploadResponse> uploadMembershipExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam("membership-option") BulkMembership option) {

        return ResponseEntity.ok(memberService.uploadMembershipExcel(file,option));
    }
}
