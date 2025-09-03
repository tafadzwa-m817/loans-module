package zw.co.afrosoft.zdf.loans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanNumber(String loanNumber);
    boolean existsByLoanNumber(String loanNumber);
    boolean existsByLoanNumberAndLoanStatus(String loanNumber, LoanStatus loanStatus);
    Optional<Loan> findTopByOrderByIdDesc();
    List<Loan> findByLoanStatus(LoanStatus loanStatus);
    List<Loan> findAllByForceNumberAndLoanStatus(String forceNumber, LoanStatus loanStatus);
    boolean existsByForceNumberAndLoanStatusAndLoanType(String forceNumber, LoanStatus loanStatus,
                                                        LoanType loanType);
    List<Loan> findByLoanStatusAndDueDateBefore(LoanStatus loanStatus, LocalDateTime dueDate);
    Optional<Loan> findByForceNumberAndLoanStatus(String forceNumber, LoanStatus loanStatus);
    Optional<Loan> findByForceNumberAndLoanStatusAndLoanType(String forceNumber, LoanStatus loanStatus,LoanType loanType);
    List<Loan> findAllByLoanStatusAndLoanType(LoanStatus loanStatus, LoanType loanType);

    @Query("SELECT l FROM Loan l " +
            "WHERE (:forceNumber IS NULL OR l.forceNumber = :forceNumber) " +
            "AND (:loanNumber IS NULL OR l.loanNumber = :loanNumber)" +
            "AND (:firstName IS NULL OR l.personalDetails.firstName = :firstName)" +
            "AND (:lastName IS NULL OR l.personalDetails.lastName = :lastName)" +
            "AND (:loanStatus IS NULL OR l.loanStatus = :loanStatus)" +
            "AND (:loanType) IS NULL OR l.loanType = :loanType")
    Page<Loan> findAllByOrderByIdDesc(@Param("forceNumber") String forceNumber,
                                      @Param("loanNumber") String loanNumber,
                                      @Param("firstName") String firstName,
                                      @Param("lastName") String lastName,
                                      @Param("loanStatus") LoanStatus loanStatus,
                                      @Param("loanType") LoanType loanType,
                                      Pageable pageable);
}
