package zw.co.afrosoft.zdf.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zw.co.afrosoft.zdf.dashboard.dto.*;
import zw.co.afrosoft.zdf.utils.constants.Constants;

import java.util.List;


@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/dashboard")
@Tag(name = "REST APIs for Loans Module Dashboard ", description = Constants.DASHBOARD_CONTROLLER_DESCRIPTION)
@CrossOrigin
@SecurityRequirement(name = "authorization")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("total-projects")
    @Operation(
            summary = "Get Total Project per Status and Value per currency REST API ",
            description = "Retrieves All projects categorizing them by status and value per currency")
    public ResponseEntity<List<ProjectDashboardResponse>> getTotalProjects(){
        return ResponseEntity.ok(dashboardService.getProjectDashboard());
    }

    @GetMapping("total-loans")
    @Operation(
            summary = "Get Total Loans per Status and Value per currency REST API ",
            description = "Retrieves All Loans categorizing them by status and value per currency")
    public ResponseEntity<List<LoansDashboardResponse>> getTotalLoans(){
        return ResponseEntity.ok(dashboardService.getLoanDashboard());
    }

    @GetMapping("total-subscriptions")
    @Operation(
            summary = "Get Total Subscriptions Value per currency REST API ",
            description = "Retrieves All Subscriptions categorizing them by value per currency")
    public ResponseEntity<List<Totals>> getTotalSubscriptions(){
        return ResponseEntity.ok(dashboardService.getSubscriptionStats());
    }

    @GetMapping("total-securities")
    @Operation(
            summary = "Get Total Securities per Status and Value per currency REST API ",
            description = "Retrieves All Securities categorizing them by status and value per currency")
    public ResponseEntity<List<SecurityDetails>> getTotalSecurities(){
        return ResponseEntity.ok(dashboardService.getSecurityDashboard());
    }

    @GetMapping("loan/comparison")
    @Operation(
            summary = "Get Total active/closed Loans REST API ",
            description = "Retrieves Active Total comparison between the total number of active loans and the number of " +
                    "loans that have not received any payments in the last 60 days")
    public ResponseEntity<TotalLoans> getTotalActiveLoans(){
        return ResponseEntity.ok(dashboardService.getLoanTotals());
    }

    @GetMapping("subscription/comparison")
    @Operation(
            summary = "Get Total active/closed Subscription REST API ",
            description = "Retrieves Active Total comparison between the total number of active Subscriptions and the number of " +
                    "Subscriptions that have not received any payments in the last 60 days")
    public ResponseEntity<TotalSubscriptions> getTotalActiveSubscriptions(){
        return ResponseEntity.ok(dashboardService.getSubscriptionTotals());
    }

}
