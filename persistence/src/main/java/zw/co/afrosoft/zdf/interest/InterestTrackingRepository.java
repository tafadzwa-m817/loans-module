package zw.co.afrosoft.zdf.interest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface InterestTrackingRepository extends JpaRepository<InterestTracking, Integer> {

    @Query("SELECT i FROM InterestTracking i ORDER BY i.lastExecutionDate DESC LIMIT 1")
    Optional<InterestTracking> findLastExecutionDate();

   Optional <InterestTracking> findTopByOrderByLastExecutionDateDesc();
}
