package zw.co.afrosoft.zdf.project.statuslog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zw.co.afrosoft.zdf.project.ProjectStatusLogs;


public interface ProjectStatusLogRepository extends JpaRepository<ProjectStatusLogs, Long> ,
        JpaSpecificationExecutor<ProjectStatusLogs> {
}
