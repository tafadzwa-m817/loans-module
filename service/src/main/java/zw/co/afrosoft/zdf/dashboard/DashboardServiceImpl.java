package zw.co.afrosoft.zdf.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.dashboard.dto.*;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.*;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.project.Project;
import zw.co.afrosoft.zdf.project.ProjectRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesRepository;
import zw.co.afrosoft.zdf.subscription.AccountStatus;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;
import zw.co.afrosoft.zdf.transaction.TransactionRepository;
import zw.co.afrosoft.zdf.transaction.Transactions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/9/25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final LoanRepository loanRepository;
    private final SecuritiesRepository securitiesRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;

    @Override
    public List<ProjectDashboardResponse> getProjectDashboard() {
        log.info("Retrieving project data to display on dashboard");

        List<Project> allProjects = projectRepository.findAll().stream()
                .filter(project -> !project.isDeleted())
                .toList();

        log.info("Grouping projects by status and retrieving count");
        Map<ProjectStatus, Long> statusCounts = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getStatus, Collectors.counting()));

        log.info("Grouping projects by status and currency then sum value");
        Map<ProjectStatus, Map<Long, BigDecimal>> grouped = allProjects.stream()
                .collect(Collectors.groupingBy(
                        Project::getStatus,
                        Collectors.groupingBy(
                                Project::getCurrencyId,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Project::getProjectCostPrice,
                                        BigDecimal::add
                                )
                        )
                ));

        log.info("Building response for project dashboard");
        return grouped.entrySet().stream()
                .map(entry -> {
                    ProjectStatus status = entry.getKey();
                    Map<Long, BigDecimal> currencyMap = entry.getValue();
                    Long totalProjects = statusCounts.getOrDefault(status, 0L);

                    List<Totals> totals = currencyMap.entrySet().stream()
                            .map(e -> new Totals(totalProjects, e.getKey(), e.getValue()))
                            .collect(Collectors.toList());

                    return new ProjectDashboardResponse(status, totals);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LoansDashboardResponse> getLoanDashboard() {

        List<Loan> loans = loanRepository.findAll();

        // Filter by LoanType.LOAN before grouping
        Map<Long, List<Loan>> groupedByCurrency = loans.stream()
                .filter(loan -> loan.getLoanType() == LoanType.LOAN)
                .collect(Collectors.groupingBy(Loan::getCurrencyId));

        List<LoansDashboardResponse> responseList = new ArrayList<>();

        for (Map.Entry<Long, List<Loan>> entry : groupedByCurrency.entrySet()) {
            Long currency = entry.getKey();
            List<Loan> currencyLoans = entry.getValue();

            double activeTotal = currencyLoans.stream()
                    .filter(loan -> loan.getLoanStatus() == LoanStatus.OPEN)
                    .map(Loan::getLoanAdvance)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            double defaultedTotal = currencyLoans.stream()
                    .filter(Loan::isDefaulted)
                    .map(Loan::getLoanAdvance)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            responseList.add(new LoansDashboardResponse(currency, activeTotal, defaultedTotal));
        }

        return responseList;
    }

    @Override
    public List<SecurityDetails> getSecurityDashboard() {

        List<Securities> securitiesList = securitiesRepository.findAll();

        // Group by status
        Map<SecuritiesStatus, List<Securities>> groupedByStatus = securitiesList.stream()
                .collect(Collectors.groupingBy(Securities::getSecuritiesStatus));

        return groupedByStatus.entrySet().stream()
                .map(statusEntry -> {
                    SecuritiesStatus status = statusEntry.getKey();
                    List<Securities> securitiesInStatus = statusEntry.getValue();

                    // Group by currencyId and calculate totals
                    Map<Long, List<Securities>> groupedByCurrency = securitiesInStatus.stream()
                            .collect(Collectors.groupingBy(Securities::getCurrencyId));

                    List<Totals> totals = groupedByCurrency.entrySet().stream()
                            .map(currencyEntry -> {
                                Long currencyId = currencyEntry.getKey();
                                List<Securities> securitiesForCurrency = currencyEntry.getValue();

                                BigDecimal totalValue = securitiesForCurrency.stream()
                                        .map(sec -> BigDecimal.valueOf(
                                                sec.getSecuritiesAmount() != null ? sec.getSecuritiesAmount() : 0.0))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                return new Totals((long) securitiesForCurrency.size(), currencyId, totalValue);
                            })
                            .toList();

                    return new SecurityDetails(status, totals);
                })
                .toList();
    }

    @Override
    public TotalLoans getLoanTotals() {
        var sixtyDaysAgo = lastPaymentActivityDate(60);

        log.info("Retrieving active Loans");
        var activeLoans = loanRepository.findByLoanStatus(LoanStatus.OPEN)
                .stream().filter(loan -> loan.getLoanType() == LoanType.LOAN).toList();

        log.info("Retrieved {} active Loans",activeLoans.size());
        var totalActive = (long) activeLoans.size();

        log.info("Retrieving unpaid Loans");
        var unpaidLoanCount = activeLoans.stream()
                .filter(loan -> {
                    List<Transactions> transactions = transactionRepository.
                            findRecentPaymentsByReferenceIdAndPaymentType(loan.getId(), PaymentType.LOAN_REPAYMENT);
                    return transactions.isEmpty() ||
                            transactions.get(0).getTransactionDate().isBefore(sixtyDaysAgo);
                })
                .count();

        return TotalLoans.builder()
                .total_active_loans(totalActive)
                .loans_unpaid_60_days(unpaidLoanCount)
                .build();
    }

    @Override
    public TotalSubscriptions getSubscriptionTotals() {
        var sixtyDaysAgo = lastPaymentActivityDate(60);

        log.info("Retrieving active Subscriptions");
        var activeSubs = subscriptionsAccountRepository.findAllByAccountStatus(AccountStatus.ACTIVE);

        log.info("Retrieved {} active Subscriptions",activeSubs.size());
        var totalActiveSubs = (long) activeSubs.size();

        log.info("Retrieving unpaid Subscriptions");
        var unpaidSubsCount = activeSubs.stream()
                .filter(subs -> {
                    List<Transactions> transactions = transactionRepository.
                            findRecentPaymentsByReferenceIdAndPaymentType(subs.getId(), PaymentType.SUBSCRIPTION);
                    return transactions.isEmpty() ||
                            transactions.get(0).getTransactionDate().isBefore(sixtyDaysAgo);
                })
                .count();

        return TotalSubscriptions.builder()
                .total_active_subscriptions(totalActiveSubs)
                .subscriptions_unpaid_60_days(unpaidSubsCount)
                .build();
    }

    @Override
    public List<Totals> getSubscriptionStats() {
        List<Transactions> transactionsList = transactionRepository.findAllByPaymentType(PaymentType.SUBSCRIPTION);
        return transactionsList.stream()
                .collect(Collectors.groupingBy(Transactions::getCurrencyId))
                .entrySet().stream()
                .map(entry -> {
                    Long currencyId = entry.getKey();
                    List<Transactions> currencyTransactions = entry.getValue();

                    BigDecimal totalValue = currencyTransactions.stream()
                            .map(Transactions::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new Totals(
                            (long) currencyTransactions.size(), // total number of transactions
                            currencyId,// currency
                            totalValue// total value
                    );
                })
                .toList();
    }

    private LocalDate lastPaymentActivityDate(int numberOfDays){
        log.info("Number of days : {} , without payment activity ",numberOfDays);
        return LocalDate.now().minusDays(numberOfDays);
    }
}
