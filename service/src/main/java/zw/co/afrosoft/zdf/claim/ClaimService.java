package zw.co.afrosoft.zdf.claim;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * created by  Romeo Jerenyama
 * created on  5/5/2025 at 17:54
 */

/**
 * Service interface for processing and retrieving claims.
 */
public interface ClaimService {

    /**
     * Processes a claim for a member based on the provided claimant details, force number, claim type, and retirement date.
     *
     * @param request        the claimant details request containing claim data
     * @param forceNumber    the force number identifying the member
     * @param claimType      the type of claim to be processed
     * @param retirementDate the retirement date of the member (if applicable)
     * @return the processed Claim entity
     */
    Claim processClaim(ClaimantDetailsRequest request, String forceNumber, ClaimType claimType, LocalDate retirementDate);

    /**
     * Retrieves a claim by its unique identifier.
     *
     * @param id the ID of the claim
     * @return the Claim entity matching the provided ID
     */
    Claim getMemberClaim(Long id);

    /**
     * Retrieves a pageable list of claims filtered by force number, claim status, and claim type.
     *
     * @param forceNumber the force number of the member (optional filter)
     * @param claimStatus the status of the claim (optional filter)
     * @param claimType   the type of claim (optional filter)
     * @param pageable    the pagination information
     * @return a pageable list of claims matching the filters
     */
    Page<Claim> retrieveClaims(String forceNumber, ClaimStatus claimStatus, ClaimType claimType, Pageable pageable);

    /**
     * Retrieves a list of claim counts grouped by their status.
     *
     * @return a list of ClaimResponse objects summarizing claim counts by status
     */
    List<ClaimResponse> getClaimCountsGroupedByStatus();
}

