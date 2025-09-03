package zw.co.afrosoft.zdf.audit;

import lombok.Getter;

@Getter
public enum UserAction {

    INTEREST_APPLIED("Interest applied to %d loans, %d projects, %d subscriptions by %s at %s", true),
    INTEREST_NEXT_EXECUTION_MONTH_RETRIEVED("Interest next execution month retrieved", true),

    // Loan actions
    LOAN_CREATED("Loan created with loan number %s for member %s, amount: %s, duration: %d months", true),
    LOAN_UPDATED("Loan updated with loan number %s, new amount: %s, new duration: %d months", true),
    LOAN_CLOSED("Loan closed with loan number %s. Final status: %s", true),

    // Member actions
    MEMBER_REGISTERED("Member registered with force number %s - %s %s", true),
    MEMBER_ACTIVATED("Member activated with force number %s", true),
    MEMBER_UPDATED("Member updated with force number %s", true),

    // Payment actions
    PAYMENT_MADE("Payment made: %s - Amount: %s for member %s, transaction type: %s", true),
    PAYMENT_REVERSAL("Payment reversal made: %s - Amount: %s for member %s, transaction type: %s", true),
    PAYMENT_BULK_UPLOAD("Bulk payment upload processed: %d records for %s on %s", true),

    // Project actions
    PROJECT_CREATED("Project created with serial number %s - %s", true),
    PROJECT_UPDATED("Project updated with serial number %s - %s", true),
    PROJECT_STATUS_UPDATED("Project status updated for serial number %s to %s", true),

    // Project Beneficiary actions
    PROJECT_BENEFICIARY_ADDED("Beneficiary with force number %s added to project %s", true),
    PROJECT_BENEFICIARY_UPDATED("Beneficiary with force number %s updated in project %s", true),
    PROJECT_BENEFICIARY_STATUS_UPDATED("Status updated for beneficiary with force number %s to %s", true),
    PROJECT_BENEFICIARY_BULK_UPLOAD("Bulk beneficiary upload processed for project %s: %d records", true),
    PROJECT_BENEFICIARY_REMOVED("Beneficiary with force number %s removed from project %s", true),

    // Securities actions
    SECURITY_PAYMENT_MADE("Security payment made for %s - Status: %s", true),

    // Subscription actions
    SUBSCRIPTION_CURRENCY_ADDED("Currency %s added to subscription account %s", true),
    SUBSCRIPTION_BULK_UPDATE("Bulk subscription update processed: %d members updated", true);

    private final String description;
    private final boolean isLoans;

    UserAction(String description, boolean isLoans) {
        this.description = description;
        this.isLoans = isLoans;
    }

    public String formatMessage(Object... args) {
        return String.format(description, args);
    }

    public static boolean isLoansAction(UserAction action) {
        return action.isLoans;
    }
}
