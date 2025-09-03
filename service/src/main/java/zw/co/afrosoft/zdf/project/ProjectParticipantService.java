package zw.co.afrosoft.zdf.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import zw.co.afrosoft.zdf.project.dto.ParticipantBulkUploadResponse;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantStatusUpdateRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantUpdateRequest;



public interface ProjectParticipantService {

    /**
     * Adds a new project participant.
     *
     * @param request the participant request data
     * @return the added ProjectParticipant
     */
    ProjectParticipant addParticipant(ProjectParticipantRequest request);

    /**
     * Retrieves a project participant by their ID.
     *
     * @param id the participant ID
     * @return the ProjectParticipant if found
     */
    ProjectParticipant getProjectParticipant(Long id);

    /**
     * Updates an existing project participant.
     *
     * @param id the participant ID
     * @param request the update request data
     * @return the updated ProjectParticipant
     */
    ProjectParticipant updateParticipant(Long id, ProjectParticipantUpdateRequest request);

    /**
     * Retrieves a paginated list of project participants filtered by parameters.
     *
     * @param fullName full name filter (optional)
     * @param standNumber stand number filter (optional)
     * @param serialNumber serial number filter (optional)
     * @param forceNumber force number filter (optional)
     * @param projectId project ID filter (optional)
     * @param status participant status filter (optional)
     * @param pageable pagination info
     * @return paginated list of ProjectParticipant
     */
    Page<ProjectParticipant> getAllProjectParticipants(
            String fullName,
            String standNumber,
            String serialNumber,
            String forceNumber,
            Long projectId,
            ProjectParticipantStatus status,
            Pageable pageable
    );

    /**
     * Updates the status of a project participant.
     *
     * @param participantUpdateRequest status update request data
     * @return the updated ProjectParticipant
     */
    ProjectParticipant updateStatus(ProjectParticipantStatusUpdateRequest participantUpdateRequest);

    /**
     * Retrieves paginated status logs for a project participant.
     *
     * @param projectParticipationId the project participation ID
     * @param status status filter (optional)
     * @param pageable pagination info
     * @return paginated list of ProjectParticipantStatusLog
     */
    Page<ProjectParticipantStatusLog> getAllStatusUpdates(
            Long projectParticipationId,
            ProjectParticipantStatus status,
            Pageable pageable
    );

    /**
     * Uploads participants via an Excel file for bulk processing.
     *
     * @param file Excel file containing participant data
     * @param projectId project ID
     * @param currencyId currency ID
     * @return response containing upload results
     */
    ParticipantBulkUploadResponse uploadParticipantsExcel(MultipartFile file, Long projectId, Long currencyId);

    ProjectParticipant removeProjectParticipant(Long projectParticipantId, Long projectId);
}

