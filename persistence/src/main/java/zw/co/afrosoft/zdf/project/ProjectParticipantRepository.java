package zw.co.afrosoft.zdf.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant,Long>,
        JpaSpecificationExecutor<ProjectParticipant> {
    @Query(value = "SELECT MAX(serial_number) FROM project_participant", nativeQuery = true)
    Optional<String> findMaxSerialNumber();

    Boolean existsByMember_ForceNumberAndProject(String forceNumber, Project project);

    ProjectParticipant findByIdAndProject(Long id, Project project);
}
