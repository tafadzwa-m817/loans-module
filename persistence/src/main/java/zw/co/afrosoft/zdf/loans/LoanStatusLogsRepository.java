package zw.co.afrosoft.zdf.loans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface LoanStatusLogsRepository extends JpaRepository<LoanStatusLogs, Long>,
        JpaSpecificationExecutor<LoanStatusLogs> {
}
