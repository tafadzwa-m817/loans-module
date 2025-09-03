package zw.co.afrosoft.zdf.project.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.clients.AuthServerService;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.member.MemberStatus;
import zw.co.afrosoft.zdf.project.*;
import zw.co.afrosoft.zdf.project.dto.*;
import zw.co.afrosoft.zdf.project.statuslog.ParticipantStatusUpdateLogsSpecification;
import zw.co.afrosoft.zdf.project.statuslog.ProjectParticipantStatusLogRepository;
import zw.co.afrosoft.zdf.utils.enums.Status;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectParticipantServiceImpl implements ProjectParticipantService {

    @Value("${serial.number.project.prefix}")
    private String projectPrefix;
    @Value("${serial.number.project.default}")
    private String projectDefaultSerialNumber;

    private final AuthServerService authServerService;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository participantRepository;
    private final ProjectParticipantStatusLogRepository participantStatusLogRepository;

    private static final String PARTICIPANT_UPLOAD = "PROJECT_PARTICIPANT_UPLOAD";

    @Override
    public ProjectParticipant addParticipant(ProjectParticipantRequest request) {
        log.info("Retrieving member by force number {}",request.forceNumber());

        var member = memberRepository.findMemberByForceNumber(request.forceNumber())
                .orElseThrow(()-> new RecordNotFoundException(
                        format("Invalid forceNUmber %s ",request.forceNumber())));

        var project = projectRepository.findById(request.projectId())
                .orElseThrow(()-> new RecordNotFoundException(
                        format("Invalid projectId %s",request.projectId())));

        if(project.getStatus().equals(ProjectStatus.INACTIVE) ||
                project.getStatus().equals(ProjectStatus.WAITING_FOR_APPROVAL))
            throw new RecordNotFoundException(format("Project %s is not active",project.getProjectName()));

        log.info("Checking if participant already exists in project {}",project.getProjectName());
        var projectParticipant = participantRepository.existsByMember_ForceNumberAndProject(request.forceNumber(),
                project);


        if(projectParticipant){
            log.error("Participant with force number {} already exists in project {}",request.forceNumber(),project.getProjectName());
            throw new RecordNotFoundException(format("Participant with force number %s already exists in project %s"
                    ,request.forceNumber(),project.getProjectName()));
        }
        log.info("Validating and updating requested square metres against available.. in project {}",project.getProjectName());
        // Validate and update square meters
        validateAndUpdateSquareMeters(project, request.squareMeters());

        log.info("Creating new project participant for project {}",project.getProjectName());
        var newProjectParticipant = ProjectParticipant.builder()
                .costPrice(request.costPrice())
                .interestCharged(request.interestCharged())
                .serialNumber(generateSerialNumber())
                .squareMeters(request.squareMeters())
                .currencyId(request.currencyId())
                .audit(new Audit())
                .standNumber(request.standNumber())
                .fundCostPrice(request.fundCostPrice())
                .member(member)
                .status(ProjectParticipantStatus.APPROVED)
                .project(project)
                .build();

        var savedParticipant = participantRepository.save(newProjectParticipant);
        log.info("Saved project participant with id {}",savedParticipant.getId());
        // Update project's allocated area
        var currentAllocated = project.getAllocatedAreaInSqm() != null ? project.getAllocatedAreaInSqm() : 0;
        project.setAllocatedAreaInSqm(currentAllocated + request.squareMeters());
        projectRepository.save(project);
        log.info("Updated allocated area in project {} to {}",project.getProjectName(),project.getAllocatedAreaInSqm());
        return savedParticipant;
    }

    @Override
    public ProjectParticipant getProjectParticipant(Long id) {
        log.info("Retrieving project participant with id : {}",id);

        return participantRepository.findById(id)
                .orElseThrow(()->
                        new RecordNotFoundException(format("Invalid id %s",id)));
    }

    @Override
    public ProjectParticipant updateParticipant(Long id, ProjectParticipantUpdateRequest request) {
        log.info("Retrieving project participant with the given id : {}",id);
        var participant = participantRepository.findById(id)
                .orElseThrow(()->
                        new RecordNotFoundException(format("Invalid id %s",id)));

        log.info("Retrieving member details by force number  {}",request.forceNumber());

        var member = memberRepository.findMemberByForceNumber(request.forceNumber())
                .orElseThrow(()-> new RecordNotFoundException(
                        format("Invalid forceNUmber %s ",request.forceNumber())));

        participant.setMember(member);
        participant.setCostPrice(request.costPrice());
        participant.setInterestCharged(request.interestCharged());
        participant.setCurrencyId(request.currencyId());
        participant.setSquareMeters(request.squareMeters());
        participant.setStandNumber(request.standNumber());
        participant.setFundCostPrice(request.fundCostPrice());

        return participantRepository.save(participant);
    }

    @Override
    public Page<ProjectParticipant> getAllProjectParticipants(String fullName, String standNumber, String serialNumber,
                                                              String forceNumber, Long projectId, ProjectParticipantStatus status, Pageable pageable) {
        return participantRepository.findAll(ProjectParticipantSpecification.getProperties(fullName, standNumber, serialNumber,
                forceNumber, projectId, status), pageable);
    }

    @Override
    public ProjectParticipant updateStatus(ProjectParticipantStatusUpdateRequest participantUpdateRequest) {
        var retrievedParticipant = participantRepository.findById(participantUpdateRequest.projectParticipantId()).
                orElseThrow(()-> new RecordNotFoundException(format("Invalid participant ID : %s",
                        participantUpdateRequest.projectParticipantId())));

        log.info("Updating project participant ID : {} with status {}",participantUpdateRequest.projectParticipantId()
                ,participantUpdateRequest.status());

        retrievedParticipant.setStatus(participantUpdateRequest.status());
        retrievedParticipant.setComment(participantUpdateRequest.comment());

        log.info("handling Project status change for project {}",retrievedParticipant);

        handleParticipantStatusUpdate(retrievedParticipant,participantUpdateRequest);
        return participantRepository.save(retrievedParticipant);
    }

    @Override
    public Page<ProjectParticipantStatusLog> getAllStatusUpdates(Long projectParticipationId, ProjectParticipantStatus status, Pageable pageable) {
        var specification = ParticipantStatusUpdateLogsSpecification.getProperties(projectParticipationId, status);

        return participantStatusLogRepository.findAll(specification, pageable);
    }

    @Override
    public ParticipantBulkUploadResponse uploadParticipantsExcel(MultipartFile file, Long projectId, Long currencyId) {
        List<ParticipantUploadResponse> uploadResponses = new ArrayList<>();
        List<ProjectParticipant> participantsToSave = new ArrayList<>();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project with ID " + projectId + " not found"));

        if (project.getStatus() == ProjectStatus.INACTIVE || project.getStatus() == ProjectStatus.WAITING_FOR_APPROVAL) {
            throw new RuntimeException("Project " + project.getProjectName() + " is not active");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String forceNumber = "";
                String standNumber = "";
                double squareMeters = 0.0;
                BigDecimal costPrice = BigDecimal.ZERO;
                double interestCharged = 0.0;
                BigDecimal fundCostPrice = BigDecimal.ZERO;

                try {
                    // Column A: Force Number
                    Cell cell0 = row.getCell(0);
                    if (cell0 != null) {
                        if (cell0.getCellType() == CellType.STRING) {
                            forceNumber = cell0.getStringCellValue().trim();
                        } else if (cell0.getCellType() == CellType.NUMERIC) {
                            forceNumber = String.valueOf((int) cell0.getNumericCellValue());
                        }
                    }
                    if (forceNumber.isEmpty()) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Force number is required"));
                        continue;
                    }

                    // Column B: Stand Number
                    Cell cell1 = row.getCell(1);
                    if (cell1 != null) {
                        if (cell1.getCellType() == CellType.STRING) {
                            standNumber = cell1.getStringCellValue().trim();
                        } else if (cell1.getCellType() == CellType.NUMERIC) {
                            standNumber = String.valueOf((int) cell1.getNumericCellValue());
                        }
                    }
                    if (standNumber.isEmpty()) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Stand number is required"));
                        continue;
                    }

                    // Column C: Square Meters
                    Cell cell2 = row.getCell(2);
                    if (cell2 == null || cell2.getCellType() != CellType.NUMERIC) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Square meters must be numeric"));
                        continue;
                    }
                    squareMeters = cell2.getNumericCellValue();

                    // Column D: Cost Price
                    Cell cell3 = row.getCell(3);
                    if (cell3 == null || cell3.getCellType() != CellType.NUMERIC) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Cost price must be numeric"));
                        continue;
                    }
                    costPrice = BigDecimal.valueOf(cell3.getNumericCellValue());

                    // Column E: Interest Charged
                    Cell cell4 = row.getCell(4);
                    if (cell4 == null || cell4.getCellType() != CellType.NUMERIC) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Interest charged must be numeric"));
                        continue;
                    }
                    interestCharged = cell4.getNumericCellValue();

                    // Column F: Fund Cost Price
                    Cell cell5 = row.getCell(5);
                    if (cell5 == null || cell5.getCellType() != CellType.NUMERIC) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Fund cost price must be numeric"));
                        continue;
                    }
                    fundCostPrice = BigDecimal.valueOf(cell5.getNumericCellValue());

                    // Member validation
                    var member = memberRepository.findMemberByForceNumber(forceNumber);
                    if (member.isEmpty()) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Member with force number " + forceNumber + " not found"));
                        continue;
                    }

                    if (member.get().getMemberStatus() != MemberStatus.ACTIVE) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Member with force number " + forceNumber + " is not active"));
                        continue;
                    }

                    // Duplicate check
                    if (participantRepository.existsByMember_ForceNumberAndProject(forceNumber, project)) {
                        uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                                Status.FAILED, "Participant with force number " + forceNumber + " already exists in project " + project.getProjectName()));
                        continue;
                    }

                    // Validate area
                    validateAndUpdateSquareMeters(project, squareMeters);

                    // Create participant
                    var participant = ProjectParticipant.builder()
                            .costPrice(costPrice)
                            .interestCharged(interestCharged)
                            .serialNumber(generateSerialNumber())
                            .squareMeters(squareMeters)
                            .currencyId(currencyId)
                            .standNumber(standNumber)
                            .fundCostPrice(fundCostPrice)
                            .member(member.get())
                            .status(ProjectParticipantStatus.APPROVED)
                            .project(project)
                            .build();

                    participantsToSave.add(participant);

                    // Adjust project's allocated area
                    var currentAllocated = project.getAllocatedAreaInSqm() != null ? project.getAllocatedAreaInSqm() : 0;
                    project.setAllocatedAreaInSqm(currentAllocated + squareMeters);

                    uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                            Status.SUCCESS, "Project participant added successfully"));

                } catch (Exception e) {
                    log.error("Error processing row {}: {}", row.getRowNum() + 1, e.getMessage());
                    uploadResponses.add(response(row, forceNumber, standNumber, squareMeters, costPrice, interestCharged, fundCostPrice,
                            Status.FAILED, "Unexpected error in row: " + e.getMessage()));
                }
            }

            // Save all participants in bulk
            if (!participantsToSave.isEmpty()) {
                participantRepository.saveAll(participantsToSave);
                projectRepository.save(project);
            }

        } catch (Exception e) {
            log.error("Error reading Excel file", e);
            throw new RuntimeException("Error reading Excel file", e);
        }

        return ParticipantBulkUploadResponse.builder()
                .audit(new zw.co.afrosoft.zdf.util.Audit())
                .uploads(uploadResponses)
                .succeededUploads(uploadResponses.stream().filter(r -> r.status() == Status.SUCCESS).count())
                .failedUploads(uploadResponses.stream().filter(r -> r.status() == Status.FAILED).count())
                .action(PARTICIPANT_UPLOAD)
                .build();
    }

    private ParticipantUploadResponse response(Row row, String forceNumber, String standNumber, double sqm,
                                               BigDecimal cost, double interest, BigDecimal fund, Status status, String message) {
        return new ParticipantUploadResponse(forceNumber, standNumber, sqm, cost, interest, fund, status,
                "At row " + (row.getRowNum() + 1) + ": " + message);
    }

    @Override
    public ProjectParticipant removeProjectParticipant(Long projectParticipantId, Long projectId) {

        var participant = participantRepository.findById(projectParticipantId);
        if (participant.isEmpty()) {
            throw new RecordNotFoundException("Project participant with id " + projectParticipantId + " not found");
        }

        var project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new RecordNotFoundException("Project with id " + projectId + " not found");
        }

        var projectParticipant = participantRepository.findByIdAndProject(projectParticipantId, project.get());
        if (Objects.isNull(projectParticipant)) {
            throw new RecordNotFoundException("Project participant with id " + projectParticipantId + " not found in project " + projectId);
        }

        log.info("Deleting project participant with id {} from project with id {}", projectParticipantId, projectId);
        participantRepository.delete(projectParticipant);

        return projectParticipant;
    }


    private void handleParticipantStatusUpdate(ProjectParticipant participant, ProjectParticipantStatusUpdateRequest
            updateStatusRequest) {

        log.info("Saving status update log for Project with id : {}", participant.getId());
        participantStatusLogRepository.save(ProjectParticipantStatusLog.builder()
                .comment(updateStatusRequest.comment())
                .status(updateStatusRequest.status())
                .projectParticipant(participant)
                .build());
    }

    private void validateAndUpdateSquareMeters(Project project, Double requestedSquareMeters) {
        // Initialize allocatedAreaInSqm if null
        var currentAllocated = project.getAllocatedAreaInSqm() != null ? project.getAllocatedAreaInSqm() : 0;
        project.setAllocatedAreaInSqm(currentAllocated);

        // Calculate remaining area
        double remainingArea = project.getTotalAreaInSqm() - project.getAllocatedAreaInSqm();

        // Validate if enough area is available
        if (requestedSquareMeters > remainingArea) {
            log.error("Requested area exceeds available area in project {}", project.getProjectName());
            throw new RecordNotFoundException(
                format("Requested area %f sqm exceeds available area %f sqm in project %s",
                    requestedSquareMeters, remainingArea, project.getProjectName()));
        }
        log.info("Updating allocated area in project {} to {}", project.getProjectName(), currentAllocated + requestedSquareMeters);
    }

    private String generateSerialNumber(){
        String maxSerialNumber = participantRepository.findMaxSerialNumber()
                .orElse(projectDefaultSerialNumber);
        log.info("Current max serial number: {}", maxSerialNumber);

        int currentMax = Integer.parseInt(maxSerialNumber.split("/")[2]);
        return String.format("%s/%06d", projectPrefix, currentMax + 1);
    }
}
