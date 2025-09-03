package zw.co.afrosoft.zdf.reports;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afrosoft.zdf.claim.ClaimStatus;
import zw.co.afrosoft.zdf.claim.ClaimType;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.report.ReportService;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/report")
@CrossOrigin
@Tag(name = "Reports", description = "Contains retrieval operations for reports")
@SecurityRequirement(name = "authorization")
@Slf4j
public class ReportsController {

    private final ReportService reportService;
    String reportName;

    @GetMapping("/projects")
    @Operation(description = "Creates a report of all projects with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> projectReport(@RequestParam Long currencyId,
                                                                 @RequestParam @Nullable ProjectStatus status,
                                                                 Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        Map<String, Object> params = new HashMap<>();

        params.put("currency", currencyId.toString());
        params.put("status", status!=null?status.name():null);
        params.put("runby", username);

        String fileName = String.format("%s Project_report", currencyId);
        reportName = "Projects_Report";

        byte[] bytes = reportService.generateReport(reportName, params);

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }



    @GetMapping("/word/projects")
    @Operation(description = "Creates a word report of all projects with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> projectReportToWord(@RequestParam Long currencyId,
                                                  @RequestParam @Nullable ProjectStatus status,
                                                  Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        Map<String, Object> params = new HashMap<>();

        params.put("currency", currencyId.toString());
        params.put("status", status!=null?status.name():null);
        params.put("runby", username);

        String fileName = String.format("%s Projects_Report", currencyId);
        reportName = "Projects_Report";
        byte[] bytes = reportService.generateToWord(reportName, params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/projects")
    @Operation(description = "Creates an excel report of all projects with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> projectReportToExcel(@RequestParam Long currencyId,
                                                   @RequestParam @Nullable ProjectStatus status,
                                                   Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        Map<String, Object> params = new HashMap<>();

        params.put("currency", currencyId.toString());
        params.put("status", status!=null?status.name():null);
        params.put("runby", username);

        String fileName = String.format("%s Projects_Report", currencyId);
        reportName = "Projects_Report";
        byte[] bytes = reportService.generateToExcel(reportName, params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/project-beneficiary")
    @Operation(description = "Creates a report of all beneficiaries of a project with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> projectBeneficiaryReport(@RequestParam Long projectId,
                                                              Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = String.format("%s Project_Beneficiary_report", projectId);
        reportName = "project_participant";

        byte[] bytes = reportService.generateReport(reportName, buildReportParams(
                projectId,username
        ));

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }



    @GetMapping("/word/project-beneficiary")
    @Operation(description = "Creates a word report of all beneficiaries of a project with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> projectBeneficiaryReportToWord(@RequestParam Long projectId,
                                               Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();

        String fileName = String.format("%s Projects_Beneficiary_Report", projectId);
        reportName = "project_participant";
        byte[] bytes = reportService.generateToWord(reportName, buildReportParams(
                projectId,username
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/project-beneficiary")
    @Operation(description = "Creates an excel report of all beneficiaries of a project with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> projectBeneficiaryReportToExcel(@RequestParam Long projectId,
                                                Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = String.format("%s Projects_Beneficiary_Report", projectId);
        reportName = "project_participant";
        byte[] bytes = reportService.generateToExcel(reportName, buildReportParams(
                projectId,username
        ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildReportParams(Long projectId,String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId.toString());
        params.put("runby", username);
        return params;
    }

    @GetMapping("/loan-listing")
    @Operation(description = "Creates a loan listing report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> loanListingReport(@RequestParam Long currencyId,
                                                                  @RequestParam LoanType loanType,
                                                                         Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = String.format("%s Loan_Listing_Report", currencyId);
        reportName = "loan_listing_report";

        byte[] bytes = reportService.generateReport(reportName, buildLoanListingReportParams(
                currencyId,username,loanType.toString()
        ));

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }

    @GetMapping("/word/loan-listing")
    @Operation(description = "Creates a word loan listing report with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> loanListingReportToWord(@RequestParam Long currencyId,
                                                   @RequestParam LoanType loanType,
                                                          Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();

        String fileName = String.format("%s Loan_Listing_Report", currencyId);
        reportName = "loan_listing_report";
        byte[] bytes = reportService.generateToWord(reportName, buildLoanListingReportParams(
                currencyId,username,loanType.toString()
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/loan-listing")
    @Operation(description = "Creates an excel loan listing report with the given parameters")
    public @ResponseBody
    ResponseEntity<byte[]> loanListingReportToExcel(@RequestParam Long currencyId,
                                                           @RequestParam LoanType loanType,
                                                           Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = String.format("%s Loan_Listing_Report", currencyId);
        reportName = "loan_listing_report";
        byte[] bytes = reportService.generateToExcel(reportName, buildLoanListingReportParams(
                currencyId,username,loanType.toString()
        ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildLoanListingReportParams(Long currencyId,String username,String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currencyId.toString());
        params.put("category", category);
        params.put("runby", username);
        return params;
    }

    @GetMapping("/sub-account-statement")
    @Operation(description = "Creates a subscription account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> subAccountStatement(@RequestParam Long currencyId,
                                                                  @RequestParam String forceNumber,
                                                                  @RequestParam @Nullable String fromDate,
                                                                  Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Subscription_Account_Statement", currencyId);
        reportName = "subs_account_statement";

        byte[] bytes = reportService.generateReport(reportName, buildSubAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate)
        );

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }


    @GetMapping("/word/sub-account-statement")
    @Operation(description = "Creates a word subscription account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> subAccountStatementToWord(@RequestParam Long currencyId,
                                                                  @RequestParam String forceNumber,
                                                                  @RequestParam @Nullable String fromDate,
                                                                  Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Subscription_Account_Statement", currencyId);
        reportName = "subs_account_statement";

        byte[] bytes = reportService.generateToWord(reportName, buildSubAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/sub-account-statement")
    @Operation(description = "Creates an excel subscription account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> subAccountStatementToExcel(@RequestParam Long currencyId,
                                                                          @RequestParam String forceNumber,
                                                                          @RequestParam @Nullable String fromDate,
                                                                          Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Subscription_Account_Statement", currencyId);
        reportName = "subs_account_statement";

        byte[] bytes = reportService.generateToExcel(reportName, buildSubAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildSubAccountStatementParams(Long currencyId,String username,String forceNumber
            ,String formattedStartDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currencyId.toString());
        params.put("forceNumber", forceNumber);
        params.put("startDate", formattedStartDate);
        params.put("runby", username);
        return params;
    }

    @GetMapping("/loans-account-statement")
    @Operation(description = "Creates a loans account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> loanAccountStatement(@RequestParam Long currencyId,
                                                                    @RequestParam String forceNumber,
                                                                    @RequestParam LoanType loanType,
                                                                    @RequestParam @Nullable String fromDate,
                                                                    Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Loans_Account_Statement", currencyId);
        reportName = "loans_account_statement";

        byte[] bytes = reportService.generateReport(reportName, buildLoansAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate,loanType)
        );

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }


    @GetMapping("/word/loans-account-statement")
    @Operation(description = "Creates a word Loans account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> loanAccountStatementToWord(@RequestParam Long currencyId,
                                                                          @RequestParam String forceNumber,
                                                                          @RequestParam @Nullable String fromDate,
                                                                          @RequestParam LoanType loanType,
                                                                          Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Loans_Account_Statement", currencyId);
        reportName = "loans_account_statement";

        byte[] bytes = reportService.generateToWord(reportName, buildLoansAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate,loanType)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/loans-account-statement")
    @Operation(description = "Creates an excel Loans account statement report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> subAccountStatementToExcel(@RequestParam Long currencyId,
                                                                           @RequestParam String forceNumber,
                                                                           @RequestParam @Nullable String fromDate,
                                                                           @RequestParam LoanType loanType,
                                                                           Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String formattedStartDate = fromDate != null && !fromDate.isEmpty() ?
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

        String fileName = String.format("%s Loans_Account_Statement", currencyId);
        reportName = "loans_account_statement";

        byte[] bytes = reportService.generateToExcel(reportName, buildLoansAccountStatementParams(
                currencyId,username,forceNumber,formattedStartDate,loanType)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildLoansAccountStatementParams(Long currencyId,String username,String forceNumber
            ,String formattedStartDate, LoanType loanType) {
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currencyId.toString());
        params.put("forceNumber", forceNumber);
        params.put("startDate", formattedStartDate);
        params.put("runby", username);
        params.put("loanType",loanType.toString());
        return params;
    }

    @GetMapping("/claims")
    @Operation(description = "Creates a claims report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsReport(@RequestParam @Nullable ClaimType claimType,
                                                             @RequestParam @Nullable ClaimStatus claimStatus,
                                                             Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String newClaimStatus = claimStatus != null ? claimStatus.name() : null;
        String newClaimType = claimType != null ? claimType.name() : null;

        String fileName = "Claims_Report";
        reportName = "claims_report";

        byte[] bytes = reportService.generateReport(reportName, buildClaimsReportParams(
                newClaimStatus,username,newClaimType)
        );

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }


    @GetMapping("/word/claims")
    @Operation(description = "Creates a word claims report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsReportToWord(@RequestParam @Nullable ClaimType claimType,
                                                                   @RequestParam @Nullable ClaimStatus claimStatus,
                                                                   Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String newClaimStatus = claimStatus != null ? claimStatus.name() : null;
        String newClaimType = claimType != null ? claimType.name() : null;
        String fileName = "Claims_Report";
        reportName = "claims_report";

        byte[] bytes = reportService.generateToWord(reportName, buildClaimsReportParams(
                newClaimStatus,username,newClaimType)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/claims")
    @Operation(description = "Creates an excel claims report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsToExcel(@RequestParam @Nullable ClaimType claimType,
                                                              @RequestParam @Nullable ClaimStatus claimStatus,
                                                              Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String newClaimStatus = claimStatus != null ? claimStatus.name() : null;
        String newClaimType = claimType != null ? claimType.name() : null;
        String fileName = "Claims_Report";
        reportName = "claims_report";

        byte[] bytes = reportService.generateToExcel(reportName, buildClaimsReportParams(
                newClaimStatus,username,newClaimType)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildClaimsReportParams(String claimStatus,String username,String claimType) {
        Map<String, Object> params = new HashMap<>();
        params.put("claim_status", claimStatus);
        params.put("claim_type", claimType);
        params.put("runby", username);
        return params;
    }

    @GetMapping("/claim-card")
    @Operation(description = "Creates an individual claim report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsCardReport(@RequestParam Long claimId,
                                                             Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = "Individual_Claim_Report";
        reportName = "claim_card";

        byte[] bytes = reportService.generateReport(reportName, buildClaimsCardParams(
                claimId,username)
        );

        return ResponseEntity.ok().header("Content-Type", "application/pdf; charset=UTF-8")
                .header("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"")
                .body(bytes);
    }


    @GetMapping("/word/claim-card")
    @Operation(description = "Creates a word individual claim report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsCardReportToWord(@RequestParam Long claimId,
                                                                   Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = "Individual_Claim_Report";
        reportName = "claim_card";

        byte[] bytes = reportService.generateToWord(reportName, buildClaimsCardParams(
                claimId,username)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".docx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/claim-card")
    @Operation(description = "Creates an excel individual claim report with the given parameters")
    public @ResponseBody ResponseEntity<byte[]> claimsCardToExcel(@RequestParam Long claimId,
                                                              Principal principal) throws IOException, JRException, SQLException {
        String username = principal.getName();
        String fileName = "Individual_Claim_Report";
        reportName = "claim_card";

        byte[] bytes = reportService.generateToExcel(reportName, buildClaimsCardParams(
                claimId,username)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName + ".xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private Map<String, Object> buildClaimsCardParams(Long claimId,String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("claim_id", claimId.toString());
        params.put("runby", username);
        return params;
    }


}
