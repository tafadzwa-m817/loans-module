package zw.co.afrosoft.zdf.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afrosoft.zdf.enums.Category;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.project.dto.ProjectRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectUpdateStatusRequest;
import zw.co.afrosoft.zdf.utils.constants.Constants;

import java.util.Arrays;
import java.util.List;



@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/project")
@Tag(name = "Project", description = Constants.PROJECT_CONTROLLER_DESCRIPTION)
@CrossOrigin
@SecurityRequirement(name = "authorization")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("categories-sqm")
    @Operation(description = "Returns all categories per square metre")
    public List<Category> getAllCategoriesPerSqm() {
        return Arrays.asList(Category.values());
    }

    @PostMapping()
    @Operation(description = "Creates a new project")
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request)
    {
        return ResponseEntity.ok(projectService.create(request));
    }

    @GetMapping("{id}")
    @Operation(description = "Returns a project by the given id")
    public ResponseEntity<Project> getProject(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProject(id));
    }
    @PutMapping("{id}")
    @Operation(description = "Updates project by the given id")
    public ResponseEntity<Project> updateProject(@PathVariable Long id,
                                                 @RequestBody ProjectRequest request){
        return ResponseEntity.ok(projectService.update(id, request));
    }
    @PatchMapping("update-status/")
    @Operation(description = "Updates status of the project by the given id")
    public ResponseEntity<Project> updateProjectStatus(@RequestBody ProjectUpdateStatusRequest updateStatusRequest){
        return ResponseEntity.ok(projectService.updateStatus(updateStatusRequest));
    }

    @Operation(summary = "Get all status updates for a specific project", description = "Retrieve paginated project status updates based on budget ID and status")
    @GetMapping("/status-updates")
    public Page<ProjectStatusLogs> getAllStatusUpdates(
            @RequestParam @Nullable Long projectId,
            @RequestParam @Nullable ProjectStatus status,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION)
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        return projectService.getAllStatusUpdates(projectId, status, pageable);
    }


    @GetMapping()
    @Operation(description = "Retrieve a paginated list of all projects")
    public ResponseEntity<Page<Project>> getAllProjects(
            @Parameter(description = Constants.SEARCH_PARAM_DESCRIPTION)
            @RequestParam @Nullable String projectName,
            @RequestParam @Nullable Long provinceId,
            @RequestParam @Nullable Long townId,
            @RequestParam @Nullable Category categoryPerSqm,
            @RequestParam @Nullable String farm,
            @RequestParam @Nullable String developer,
            @RequestParam @Nullable ProjectStatus status,
            @RequestParam @Nullable Long currencyId,
            @RequestParam @Nullable String serialNumber,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION)
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        return ResponseEntity.ok(projectService.getAllProjects(serialNumber,categoryPerSqm,provinceId, townId,farm,developer,status,
                projectName,currencyId, pageable));
    }
}
