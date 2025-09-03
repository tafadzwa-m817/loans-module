package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;

import java.util.List;

@Slf4j
@Aspect
@Service
@RequiredArgsConstructor
public class SubscriptionTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.subscription.SubscriptionService.addCurrency(..))")
    public void subscriptionCurrencyAdd() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.subscription.SubscriptionService.updateMemberDetailsFromFile(..))")
    public void subscriptionBulkUpdate() {}

    @AfterReturning(value = "subscriptionCurrencyAdd()", returning = "response")
    public void afterSubscriptionCurrencyAdd(SubscriptionsAccount response) {
        log.info("Tracking subscription currency addition: {} for account {}", response.getCurrencyId(), response.getAccountNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.SUBSCRIPTION_CURRENCY_ADDED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.SUBSCRIPTION_CURRENCY_ADDED))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.SUBSCRIPTION_CURRENCY_ADDED.formatMessage(
                                response.getCurrencyId(),
                                response.getAccountNumber()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "subscriptionBulkUpdate()", returning = "response")
    public void afterSubscriptionBulkUpdate(List<SubscriptionsAccount> response) {
        if (!response.isEmpty()) {
            var firstResponse = response.get(0);
            log.info("Tracking bulk subscription update: {} members", response.size());
            auditCommons.createAudit(
                    AuditTrailRequest.builder()
                            .userAction(UserAction.SUBSCRIPTION_BULK_UPDATE.name())
                            .isLoans(UserAction.isLoansAction(UserAction.SUBSCRIPTION_BULK_UPDATE))
                            .username(firstResponse.getAudit().getCreatedBy())
                            .ipAddress(firstResponse.getAudit().getIpAddress())
                            .description(UserAction.SUBSCRIPTION_BULK_UPDATE.formatMessage(
                                    response.size()
                            ))
                            .build()
            );
        }
    }
}
