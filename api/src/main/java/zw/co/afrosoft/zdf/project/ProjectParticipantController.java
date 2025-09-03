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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import zw.co.afrosoft.zdf.project.dto.ParticipantBulkUploadResponse;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantStatusUpdateRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectParticipantUpdateRequest;
import zw.co.afrosoft.zdf.utils.constants.Constants;


@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/project-participant")
@Tag(name = "Project Participant", description = Constants.PROJECT_PARTICIPANT_CONTROLLER_DESCRIPTION)
@CrossOrigin
@SecurityRequirement(name = "authorization")
public class ProjectParticipantController {

    private final ProjectParticipantService service;

    @PostMapping()
    @Operation(description = "Adds a new participant in a project")
    public ResponseEntity<ProjectParticipant> addProjectParticipant(@RequestBody ProjectParticipantRequest request)
    {
        return ResponseEntity.ok(service.addParticipant(request));
    }

    @GetMapping("{id}")
    @Operation(description = "Returns a project participant by the given id")
    public ResponseEntity<ProjectParticipant> getProjectParticipant(@PathVariable Long id){
        return ResponseEntity.ok(service.getProjectParticipant(id));
    }
    @PutMapping("{id}")
    @Operation(description = "Updates project participant by the given id")
    public ResponseEntity<ProjectParticipant> updateProjectParticipant(@PathVariable Long id,
                                                 @RequestBody ProjectParticipantUpdateRequest request){
        return ResponseEntity.ok(service.updateParticipant(id, request));
    }

    @Operation(description = "Update a project participant status")
    @PutMapping("update-status")
    public ResponseEntity<ProjectParticipant> updateParticipantStatus(
            @RequestBody ProjectParticipantStatusUpdateRequest participantUpdateRequest) {
        return ResponseEntity.ok(service.updateStatus(participantUpdateRequest));
    }

    @Operation(summary = "Get all status updates for a specific participant",
            description = "Retrieve paginated participant status updates based on participant ID and status")
    @GetMapping("/status-updates")
    public Page<ProjectParticipantStatusLog> getAllStatusUpdates(
            @RequestParam @Nullable Long projectParticipantId,
            @RequestParam @Nullable ProjectParticipantStatus status,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION)
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        return service.getAllStatusUpdates(projectParticipantId, status, pageable);
    }

    @GetMapping()
    @Operation(description = "Retrieve a paginated list of all project participants")
    public ResponseEntity<Page<ProjectParticipant>> getAllProjectParticipant(
            @Parameter(description = Constants.SEARCH_PARAM_DESCRIPTION)
            @RequestParam @Nullable String serialNumber,
            @RequestParam @Nullable ProjectParticipantStatus status,
            @RequestParam @Nullable Long projectId,
            @RequestParam @Nullable String standNumber,
            @RequestParam @Nullable String fullName,
            @RequestParam @Nullable String forceNumber,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION)
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        return ResponseEntity.ok(service.getAllProjectParticipants(fullName, standNumber, serialNumber, forceNumber, projectId, status, pageable));
    }

    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload Participants Excel File REST API ",
            description = "Upload Excel file for Project Participants")
    public ResponseEntity<ParticipantBulkUploadResponse> uploadParticipantsExcel(
            @RequestPart("file") MultipartFile file ,
            @RequestParam("projectId") Long projectId,
            @RequestParam("currencyId") Long currencyId) {
        return ResponseEntity.ok(service.uploadParticipantsExcel(file,
                projectId,currencyId));
    }

    @DeleteMapping("{id}/project/{projectId}")
    @Operation( summary = "Remove Participant REST API ",
            description = "Remove a participant from a project")
    public ResponseEntity<ProjectParticipant> removeProjectParticipant(@PathVariable Long id,
                                                           @PathVariable Long projectId) {
         var participant = service.removeProjectParticipant(id, projectId);
        return ResponseEntity.ok(participant);
    }
}
