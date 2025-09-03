package zw.co.afrosoft.zdf.notification.constants;

public interface MessageConstants {
    String CREATE_INVESTMENT_NOTIFICATION =
            "Your investment on {investment-type}, has been successfully created. \n We will keep you updated on its performance.";

    String PROPERTY_RENTAL_INVESTMENT_MATURITY =
                    "We are delighted to inform you that your property rental investment has successfully matured. Below is the performance summary for your investment:\n\n" +
                    "{performanceDetails}\n\n" +
                    "Your total returns amount to: ${returns}.\n\n" +
                    "Thank you for trusting us with your investment. We look forward to continuing to serve your financial growth needs.\n\n" +
                    "Best regards,\n" +
                    "The ZDF Investments Team";

    String PROPERTY_INVESTMENT_MATURITY =
           "Your property investment has matured. StockMarketInvestment details: {investmentDetails}. Total returns: ${returns}.";

    String ASSIGN_PROPERTY_TO_TENANT =
                    "You have been successfully assigned to a property with serial number {serial-number}, located at {propertyAddress}, in {location}.\n" +
                    "Your monthly rent is ${monthlyRent}. We look forward to a great experience together.";

    String UPDATE_PROPERTY_RENTAL_TERMS = "Your property rental terms have been updated.\n\n" +
            "New Monthly Rent effective next month: ${newMonthlyRent}\n" +
            "Property Serial Number: {serial-number}\n" +
            "Property Address: {propertyAddress}\n" +
            "Location: {location}.\n\n" +
            "Please review the updated terms and contact us if you have any questions.";

    String CREATED_TENANT_ACCOUNT_NOTIFICATION = """
    Your tenant account has been successfully created.\n\n
    Tenant Number: {tenantNumber}\n
    Email: {email}\n
    Phone: {phoneNumber}.\n
    """;

    String TENANT_EVICTION_NOTICE =
                    "We regret to inform you that your tenancy agreement for the property with serial number {serial-number}, located at {propertyAddress}, {location}, has been terminated. " +
                    "This notice is effective from {evictionDate}.\n\n" +
                    "Please ensure that you vacate the property by the specified date. Failure to comply may result in further action.\n\n" +
                    "If you have any questions or require clarification, please contact us immediately or visit our offices.\n\n" +
                    "We appreciate your cooperation in this matter.\n\n" +
                    "Best regards,\n" +
                    "The ZDF Investments Team";


    String UPDATED_TENANT_DETAILS_NOTIFICATION =
                    "Your tenant details have been updated:\n\n" +
                    "{updatedDetails}" +
                    "Tenant Number: {tenantNumber}.";

    String PROPERTY_RENTAL_PAYMENT =
                    "This is a confirmation that your rental payment has been successfully processed for the property with serial number {serial-number}.\n\n" +
                    "Property Address: {propertyAddress},\n" +
                    "Location: {location},\n" +
                    "Amount Paid: ${paymentAmount},\n" +
                    "Current Balance: ${balance}.\n" +
                    "Thank you for ensuring timely payments. If you have any queries, please contact our support team.";

    String INVESTMENT_CONTRIBUTION_UPDATE =
                    "We are pleased to confirm that your recent investment contribution of ${paymentAmount} has been successfully processed.\n\n" +
                    "Here is a summary of your updated investment details:\n" +
                    "- Current Year Deposit: ${currentYearDeposit}\n" +
                    "- Yearly Income Generated: ${yearlyIncomeGenerated}\n" +
                    "- Total Income Generated: ${totalIncomeGenerated}\n\n" +
                    "Thank you for your continued trust in our investment programs. Your contributions are integral to the success of our ventures.\n\n" +
                    "If you have any questions or require assistance, please contact our support team.\n\n" +
                    "Best regards,\n" +
                    "The ZDF Investments Team";

    String TERMINATE_PROPERTY_INVESTMENT =
            "Your Property StockMarketInvestment with serial number {serial-number} has been terminated. " +
                    "Here are the final details:\n\n" +
                    "Investor Name: {investorName}\n" +
                    "StockMarketInvestment Amount: ${investmentAmount}\n" +
                    "Yearly Income: ${yearlyIncome}\n\n" +
                    "Total Income: ${totalIncome}\n\n" +
                    "Thank you for your investment!";


    String TERMINATE_PROPERTY_RENT_INVESTMENT =
            "Your Property Rental StockMarketInvestment with serial number {serial-number} has been terminated. " +
                    "Here are the final details:\n\n" +
                    "Investor Name: {investorName}\n" +
                    "Amount Invested: ${amountInvested}\n" +
                    "Current Returns: ${currentReturns}\n\n" +
                    "Thank you for your investment!";
}
