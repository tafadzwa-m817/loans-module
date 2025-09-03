package zw.co.afrosoft.zdf.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface MemberStatusRepository extends JpaRepository<MemberStatusLogs, Long>,
        JpaSpecificationExecutor<MemberStatusLogs> {
}
