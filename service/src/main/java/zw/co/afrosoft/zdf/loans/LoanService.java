package zw.co.afrosoft.zdf.loans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.dto.LoanRequestDto;
import zw.co.afrosoft.zdf.dto.MemberLoanDetailsRequest;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;



public interface LoanService {

    /**
     * Method attempts to create a loan using LoanRequestDto
     * @param loanRequestDto
     */
    Loan createLoan(LoanRequestDto loanRequestDto);

    Loan addMemberDetails(String loanNumber, MemberLoanDetailsRequest memberLoanDetailsRequest);

    /**
     * Method attempts to approve Loan
     *
     * @param loanNumber
     */
    void approveLoan(String loanNumber);

    /**
     * Method attempts to fetch Loan by ID
     * @param id
     * @return
     */
    Loan findLoanById(Long id);

    /**
     * Method attempts to close a loan with a given loanNumber
     * @param loanNumber
     */
    Loan closeLoan(String loanNumber);

    /**
     * Method attempts to update loan using UpdateLoanRequestDto
     * @param id
     * @param loanRequestDto
     * @return
     */
    Loan updateLoan( Long id, LoanRequestDto loanRequestDto);


    /**
     * Method attempts to update the loan status
     * @param loanUpdateStatusRequest
     * @return
     */
    Loan updateLoanStatus(LoanUpdateStatusRequest loanUpdateStatusRequest);

    Page<LoanStatusLogs> showLoanStatusLogs(Long loanId, LoanStatus loanStatus, Pageable pageable);

    /**
     * Method attempts bulk upload of loans
     *
     * @param file
     */
    void bulkUploadLoans(MultipartFile file);

    /**
     * Method attempts to retrieve loans using filters
     * @param forceNumber
     * @param loanNumber
     * @param firstName
     * @param lastName
     * @param loanStatus
     * @param pageable
     * @return
     */
    Page<Loan> getLoans(String forceNumber, String loanNumber,
                        String firstName, String lastName,
                        LoanStatus loanStatus, LoanType loanType, Pageable pageable);
}
