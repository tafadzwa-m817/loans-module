package zw.co.afrosoft.zdf.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.afrosoft.zdf.enums.Category;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.project.dto.ProjectRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectUpdateStatusRequest;



public interface ProjectService {

    /**
     * Creates a new project from the given request.
     *
     * @param request the project creation request data
     * @return the created Project
     */
    Project create(ProjectRequest request);

    /**
     * Retrieves a project by its ID.
     *
     * @param id the project ID
     * @return the Project if found
     */
    Project getProject(Long id);

    /**
     * Updates an existing project identified by ID with the given request data.
     *
     * @param id      the project ID
     * @param request the project update request data
     * @return the updated Project
     */
    Project update(Long id, ProjectRequest request);

    /**
     * Updates the status of a project.
     *
     * @param updateStatusRequest the request containing status update info
     * @return the updated Project
     */
    Project updateStatus(ProjectUpdateStatusRequest updateStatusRequest);

    /**
     * Retrieves a paginated list of status updates for a given project.
     *
     * @param projectId the project ID
     * @param status    the status filter (optional)
     * @param pageable  pagination information
     * @return paginated list of ProjectStatusLogs
     */
    Page<ProjectStatusLogs> getAllStatusUpdates(Long projectId, ProjectStatus status, Pageable pageable);

    /**
     * Retrieves a paginated list of projects filtered by the given parameters.
     *
     * @param serialNumber    project serial number filter (optional)
     * @param categoryPerSqm  category per sqm filter (optional)
     * @param provinceId      province filter (optional)
     * @param townId          town filter (optional)
     * @param farm            farm filter (optional)
     * @param developer       developer filter (optional)
     * @param status          project status filter (optional)
     * @param projectName     project name filter (optional)
     * @param currencyId      currency filter (optional)
     * @param pageable        pagination information
     * @return paginated list of Projects
     */
    Page<Project> getAllProjects(
            String serialNumber,
            Category categoryPerSqm,
            Long provinceId,
            Long townId,
            String farm,
            String developer,
            ProjectStatus status,
            String projectName,
            Long currencyId,
            Pageable pageable
    );
}

