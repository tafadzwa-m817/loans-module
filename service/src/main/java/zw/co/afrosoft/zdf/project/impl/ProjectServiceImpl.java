package zw.co.afrosoft.zdf.project.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.dto.AccountCreateRequest;
import zw.co.afrosoft.zdf.dto.AccountResponseDto;
import zw.co.afrosoft.zdf.dto.AccountStatusUpdateRequest;
import zw.co.afrosoft.zdf.dto.PageResponse;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.Category;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.clients.GLServiceClient;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.project.*;
import zw.co.afrosoft.zdf.project.dto.ProjectRequest;
import zw.co.afrosoft.zdf.project.dto.ProjectSubCategory;
import zw.co.afrosoft.zdf.project.dto.ProjectUpdateStatusRequest;
import zw.co.afrosoft.zdf.project.statuslog.ProjectStatusLogRepository;
import zw.co.afrosoft.zdf.project.statuslog.ProjectStatusLogsSpecification;
import zw.co.afrosoft.zdf.utils.enums.GLAccountCategory;
import zw.co.afrosoft.zdf.utils.enums.GLStatus;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectStatusLogRepository projectStatusLogRepository;
    private final ParameterServiceClient parameterServiceClient;
    private final GLServiceClient glServiceClient;

    @Value("${serial.number.main-project.prefix}")
    private String prefix;

    @Override
    public Project create(ProjectRequest projectRequest) {
        requireNonNull(projectRequest, "Project request cannot be null");

        var newProject = Project.builder()
                .serialNumber(generateSerialNumber())
                .projectName(projectRequest.projectName())
                .provinceId(projectRequest.provinceId())
                .townId(projectRequest.townId())
                .farm(projectRequest.farm())
                .developer(projectRequest.developer())
                .numberOfStands(projectRequest.numberOfStands())
                .dateOfPurchase(projectRequest.dateOfPurchase())
                .audit(new Audit())
                .isDeleted(Boolean.FALSE)
                .status(ProjectStatus.WAITING_FOR_APPROVAL)
                .categoryPerSqm(projectRequest.categoryPerSqm())
                .costPricePerSqm(projectRequest.costPricePerSqm())
                .totalAreaInSqm(projectRequest.totalAreaInSqm())
                .projectCostPrice(projectRequest.projectCostPrice())
                .currencyId(projectRequest.currencyId())
                .build();

        log.info("new project to add : {}",newProject);
        return projectRepository.save(newProject);
    }

    @Override
    public Project getProject(Long id) {
        return projectRepository.findById(id).orElseThrow(()->
                new RecordNotFoundException(format("Project id  %s not found",id)));
    }

    @Override
    public Project update(Long id, ProjectRequest request) {

        log.info("retrieving project with id {}",id);
        var project = projectRepository.findById(id).orElseThrow(()->
                new RecordNotFoundException(format("Project id  %s not found",id)));

        log.info("retrieved project {}",project);
        project.setProjectName(request.projectName());
        project.setTownId(request.townId());
        project.setFarm(request.farm());
        project.setDateOfPurchase(request.dateOfPurchase());
        project.setDeveloper(request.developer());
        project.setNumberOfStands(request.numberOfStands());
        project.setProvinceId(request.provinceId());
        project.setCategoryPerSqm(request.categoryPerSqm());
        project.setCostPricePerSqm(request.costPricePerSqm());
        project.setTotalAreaInSqm(request.totalAreaInSqm());
        project.setProjectCostPrice(request.projectCostPrice());
        project.setCurrencyId(request.currencyId());

        return projectRepository.save(project);
    }

    @Override
    public Project updateStatus(ProjectUpdateStatusRequest updateStatusRequest) {
        log.info("Updating status of project id: {} with comment: {}",updateStatusRequest.getProjectId()
                ,updateStatusRequest.getComment());

        var retrievedProject = projectRepository.findById(updateStatusRequest.getProjectId()).orElseThrow(()->
                new RecordNotFoundException(format("Invalid Project id : %s", updateStatusRequest.getProjectId())));

        if(retrievedProject.getStatus().equals(updateStatusRequest.getStatus()))
            throw new RecordNotFoundException(format("Project id : %s already in %s status",
                    updateStatusRequest.getProjectId(),updateStatusRequest.getStatus()));


        if(retrievedProject.getStatus().equals(ProjectStatus.INACTIVE) && updateStatusRequest.getStatus().equals(ProjectStatus.ACTIVE)){
            retrievedProject.setStatus(ProjectStatus.ACTIVE);
            updateStatusRequest.setStatus(ProjectStatus.ACTIVATED);
        }
        retrievedProject.setStatus(updateStatusRequest.getStatus().equals(ProjectStatus.ACTIVATED)?ProjectStatus.ACTIVE:ProjectStatus.INACTIVE);
        retrievedProject.setComment(updateStatusRequest.getComment());

        log.info("handling Project status change for project {}",retrievedProject);


        createAndUpdateGeneralLedger(retrievedProject,updateStatusRequest);

        handleProjectStatusUpdate(retrievedProject,updateStatusRequest);
        return projectRepository.save(retrievedProject);
    }

    @Override
    public Page<ProjectStatusLogs> getAllStatusUpdates(Long projectId, ProjectStatus status, Pageable pageable) {
        Specification<ProjectStatusLogs> spec = ProjectStatusLogsSpecification.getProperties(projectId, status);
        return projectStatusLogRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Project> getAllProjects(String serialNumber, Category categoryPerSqm, Long provinceId, Long townId, String farm,
                                        String developer, ProjectStatus status, String projectName, Long currencyId,
                                        Pageable pageable) {
        Specification<Project> spec = ProjectSpecification.getProperties(
                categoryPerSqm, provinceId,townId,farm,developer, status,projectName,currencyId,serialNumber);

        return projectRepository.findAll(spec, pageable);
    }

    private void createAndUpdateGeneralLedger(Project project, ProjectUpdateStatusRequest updateStatusRequest){
        switch (updateStatusRequest.getStatus()){
            case ACTIVE -> createGeneralLedger(project);
            case INACTIVE,ACTIVATED -> updateGeneralLedger(project,updateStatusRequest);
            default -> {
            }

        }
    }

    private void createGeneralLedger(Project project){

        var projectIncomeAccount = buildGeneralLedgerAccountRequest(project,GLAccountCategory.PROJECTS_INCOME);
        log.info("Created request : {} to create a new Income GL Account from project id {}",projectIncomeAccount
                ,project.getId());
        var projectExpenseAccount = buildGeneralLedgerAccountRequest(project,GLAccountCategory.PROJECTS_EXPENSES);

        log.info("Created request : {} to create a new Expense GL Account from project id {}",projectExpenseAccount
                ,project.getId());
        try {
            var projectIncomeResponse = glServiceClient.createNewGLAccount(projectIncomeAccount);
            log.info("created General Ledger Account : {} of type {}",projectIncomeResponse,
                    projectIncomeAccount.category());

            var projectExpenseResponse = glServiceClient.createNewGLAccount(projectExpenseAccount);
            log.info("created General Ledger Account : {} of type {}",projectExpenseResponse,
                    projectExpenseAccount.category());

        } catch (Exception e) {
            log.error("Error creating General Ledger Account via feign: {}", e.getMessage());
            throw new RuntimeException("Error creating General Ledger Account via feign: " +e.getMessage());
        }
    }
    private void updateGeneralLedger(Project project, ProjectUpdateStatusRequest updateStatusRequest){
        log.info("Retrieving a GL Account by name {}",project.getProjectName());

        PageResponse<AccountResponseDto> response;
        if(!project.getStatus().equals(ProjectStatus.WAITING_FOR_APPROVAL)){
            try {
                response = glServiceClient.getGLAccountByNameAndCurrency(project.getCurrencyId(),project.getProjectName());
                log.info("Retrieved GL Account details : {}",response.getContent());
                if(!response.getContent().isEmpty()) {

                    response.getContent().forEach(account -> {
                        log.info("Updating GL Account details: {}", account);

                        var newStatus = updateStatusRequest.getStatus();
                        var updateRequest = AccountStatusUpdateRequest.builder()
                                .accountId(account.id()) // Ensure `id()` is correct
                                .comment(updateStatusRequest.getComment())
                                .status(newStatus.name().equals("ACTIVATED")?GLStatus.ACTIVE.name(): newStatus.name())
                                .build();

                        log.info("Updated GL Account details: {}", updateRequest);
                        glServiceClient.updateGLAccountStatus(updateRequest);
                    });
                }

            } catch (Exception e) {
                log.error("Error retrieving GL Account details via feign: {}", e.getMessage());
                throw new RuntimeException("Error retrieving GL account details via feign: " +e.getMessage());
            }
        }


    }

    private AccountCreateRequest buildGeneralLedgerAccountRequest(Project project , GLAccountCategory category){
       String accountName = category.equals(GLAccountCategory.PROJECTS_INCOME)?
               project.getProjectName().concat(" (INCOME)"):
               project.getProjectName().concat(" (EXPENSE)");
        return AccountCreateRequest.builder()
                .name(accountName)
                .category(category.name())
                .currencyId(project.getCurrencyId())
                .build();
    }

    private void handleProjectStatusUpdate(Project project, ProjectUpdateStatusRequest updateStatusRequest) {

        log.info("Saving status update log for Project with id : {}", project.getId());
        projectStatusLogRepository.save(ProjectStatusLogs.builder()
                .comment(updateStatusRequest.getComment())
                .status(updateStatusRequest.getStatus())
                .project(project).
                audit(new Audit())
                .build());
    }

    // Retrieve sub category details from system parameter service
    private ProjectSubCategory retrieveCategoryDetails(Long categoryId) {
        try {
            return parameterServiceClient.getProjectSubCategoryById(categoryId);
        } catch (Exception e) {
            log.error("Error retrieving account details via feign: {}", e.getMessage());
            throw new RuntimeException("Error retrieving account details via feign: " +e.getMessage());
        }
    }
    private String generateSerialNumber(){
        long nextNumber = 1;
        Optional<Project> project = projectRepository.findTopByOrderByIdDesc();
        if(project.isPresent()){
            String lastSerialNumber = project.get().getSerialNumber();
            String numericPart = lastSerialNumber.substring(8, 14);
            nextNumber = Integer.parseInt(numericPart) + 1;
        }
        String formattedNumber = format("%06d", nextNumber);
        return prefix + formattedNumber;
    }

}
