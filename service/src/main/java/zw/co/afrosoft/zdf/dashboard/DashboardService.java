package zw.co.afrosoft.zdf.dashboard;

import zw.co.afrosoft.zdf.dashboard.dto.*;

import java.util.List;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/9/25
 */

/**
 * Service interface for providing summarized dashboard data related to
 * projects, loans, securities, and subscriptions.
 */
public interface DashboardService {

    /**
     * Retrieves dashboard data summarizing projects grouped by status.
     *
     * @return list of project dashboard responses
     */
    List<ProjectDashboardResponse> getProjectDashboard();

    /**
     * Retrieves dashboard data summarizing loan balances by currency.
     *
     * @return list of loan dashboard responses
     */
    List<LoansDashboardResponse> getLoanDashboard();

    /**
     * Retrieves security details grouped by status and currency.
     *
     * @return list of security details
     */
    List<SecurityDetails> getSecurityDashboard();

    /**
     * Retrieves total loan statistics including active and overdue loans.
     *
     * @return total loans summary
     */
    TotalLoans getLoanTotals();

    /**
     * Retrieves total subscription statistics including active and overdue subscriptions.
     *
     * @return total subscriptions summary
     */
    TotalSubscriptions getSubscriptionTotals();

    /**
     * Retrieves aggregated subscription values grouped by currency.
     *
     * @return list of subscription totals per currency
     */
    List<Totals> getSubscriptionStats();
}

