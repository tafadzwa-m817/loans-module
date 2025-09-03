package zw.co.afrosoft.zdf.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afrosoft.zdf.entity.Payments;



public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    Page<Payments> getAllByForceNumberAndLoanNumber(String forceNumber, String loanNumber,
                                                    Pageable pageable);
}
