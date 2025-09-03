package zw.co.afrosoft.zdf.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.logs.MemberStatusLogs;
import zw.co.afrosoft.zdf.member.dto.MemberBulkUploadResponse;
import zw.co.afrosoft.zdf.member.request.MemberFilterRequest;
import zw.co.afrosoft.zdf.member.request.MemberStatusUpdateRequest;
import zw.co.afrosoft.zdf.utils.enums.BulkMembership;



/**
 * Service interface for managing members within the system.
 */
public interface MemberService {

    /**
     * Registers a new member with the provided registration details.
     *
     * @param memberRegistrationRequest the registration request containing member information
     * @return the newly created {@link Member}
     */
    Member registerMember(MemberRegistrationRequest memberRegistrationRequest);

    /**
     * Activates a member using their unique ID.
     *
     * @param id the ID of the member to activate
     * @return the updated {@link Member} with active status
     */
    Member activateMember(Long id);

    /**
     * Retrieves a paginated list of members matching the given filters.
     *
     * @param request  the filter criteria
     * @param pageable the pagination information
     * @return a paginated list of {@link Member}
     */
    Page<Member> getMembers(MemberFilterRequest request, Pageable pageable);

    /**
     * Updates an existing member with the specified ID using new registration details.
     *
     * @param id      the ID of the member to update
     * @param request the new member details
     * @return the updated {@link MemberResponseDto}
     */
    MemberResponseDto update(Long id, MemberRegistrationRequest request);

    /**
     * Updates the status of a member.
     *
     * @param request the status update request
     * @return the updated {@link MemberResponseDto}
     */
    MemberResponseDto updateStatus(MemberStatusUpdateRequest request);

    /**
     * Retrieves the member status change logs for a specific member.
     *
     * @param memberId the ID of the member
     * @param status   optional filter by specific {@link MemberStatus}
     * @param pageable the pagination information
     * @return a paginated list of {@link MemberStatusLogs}
     */
    Page<MemberStatusLogs> getAllStatusUpdates(Long memberId, MemberStatus status, Pageable pageable);

    /**
     * Retrieves a member's detailed information by their ID.
     *
     * @param id the ID of the member
     * @return the {@link MemberResponseDto} containing member details
     */
    MemberResponseDto getMemberById(Long id);

    /**
     * Uploads and registers multiple members using a provided Excel file.
     *
     * @param file    the Excel file containing member data
     * @param option  the type of bulk membership processing to apply
     * @return the result of the bulk upload operation
     */
    MemberBulkUploadResponse uploadMembershipExcel(MultipartFile file, BulkMembership option);
}
