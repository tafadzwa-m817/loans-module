package zw.co.afrosoft.zdf.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.alert.AlertService;
import zw.co.afrosoft.zdf.claim.Claim;
import zw.co.afrosoft.zdf.claim.ClaimRepository;
import zw.co.afrosoft.zdf.claim.ClaimStatus;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.enums.PaymentType;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;
import zw.co.afrosoft.zdf.exceptions.*;
import zw.co.afrosoft.zdf.feign.clients.GLServiceClient;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.feign.dto.ErrorResponse;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.member.Member;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesRepository;
import zw.co.afrosoft.zdf.security.User;
import zw.co.afrosoft.zdf.subscription.AccountStatus;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;
import zw.co.afrosoft.zdf.subscription.dto.PaymentBulkUploadResponse;
import zw.co.afrosoft.zdf.subscription.dto.ProcessTransTypeRequest;
import zw.co.afrosoft.zdf.subscription.dto.UploadResponse;
import zw.co.afrosoft.zdf.transaction.TransactionRepository;
import zw.co.afrosoft.zdf.transaction.Transactions;
import zw.co.afrosoft.zdf.utils.enums.Status;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static zw.co.afrosoft.zdf.claim.ClaimStatus.APPROVED;
import static zw.co.afrosoft.zdf.enums.LoanStatus.OPEN;
import static zw.co.afrosoft.zdf.enums.LoanType.LOAN;
import static zw.co.afrosoft.zdf.enums.PaymentType.SECURITY_REPAYMENT;
import static zw.co.afrosoft.zdf.enums.SecuritiesStatus.OVERDUE;
import static zw.co.afrosoft.zdf.enums.SecuritiesStatus.PAID;
import static zw.co.afrosoft.zdf.payment.ReversalAction.SECURITIES;
import static zw.co.afrosoft.zdf.utils.enums.InterestCategory.SUBSCRIPTION;



@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService{

    private final MemberRepository memberRepository;
    private final SubscriptionsAccountRepository subscriptionsRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final SecuritiesRepository securityTransactionRepository;
    private final ParameterServiceClient parameterServiceClient;
    private final GLServiceClient glServiceClient;
    private final AlertService alertService;
    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public Transactions makePayment(PaymentType paymentType, PaymentRequest paymentRequest,
                                    Long transactionTypeId, LocalDate date) {

        log.info("Making payment for payment type {}, and for forceNumber {}",
                paymentType, paymentRequest.getForceNumber());
        Member foundMember = findMemberOrThrow(paymentRequest.getForceNumber());

        return switch (paymentType) {
            case SUBSCRIPTION -> paySubscription(paymentType, paymentRequest, transactionTypeId, date, foundMember);
            case LOAN_REPAYMENT ,PROJECT_REPAYMENT-> payLoan(paymentType, paymentRequest, transactionTypeId, date, foundMember);
            case SECURITY_REPAYMENT -> paySecurities(paymentType, paymentRequest, transactionTypeId, date, foundMember);
            case CLAIM_PAYMENT -> payClaim(paymentType, paymentRequest, transactionTypeId, date, foundMember);
            default -> throw new InvalidParameterException("Invalid payment type");
        };
    }
    @Override
    public Transactions paymentReversal(PaymentType paymentType,
                                        PaymentReversalRequest request,
                                        Long transactionTypeId,
                                        LocalDate date,
                                        ReversalAction action) {
        log.info("Reversing payment for {}: ", action.name());
        Member member = findMemberOrThrow(request.getForceNumber());

        return switch (action) {
            case LOAN, PROJECT -> reverseLoanOrProjectPayment(paymentType, request, transactionTypeId, date, member, action);
            case SUBSCRIPTION -> reverseSubscriptionPayment(paymentType, request, transactionTypeId, date, member, action);
            case SECURITIES -> reverseSecuritiesPayment(paymentType, request, transactionTypeId, date, member, action);
        };
    }

    private Member findMemberOrThrow(String forceNumber) {
        log.info("Retrieving member for {}", forceNumber);
        return memberRepository.findMemberByForceNumber(forceNumber)
                .orElseThrow(() -> new RecordNotFoundException(
                        format("Member with force number %s not found", forceNumber)
                ));
    }
    private  Transactions paySecurities(PaymentType paymentType, PaymentRequest paymentRequest,
                                       Long transactionTypeId, LocalDate date, Member foundMember) {

        if (!Objects.equals(paymentType, SECURITY_REPAYMENT)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, SECURITY_REPAYMENT)
            );
        }
        log.info("Paying securities for forceNumber: {} ", paymentRequest.getForceNumber());
        var transactionType = parameterServiceClient.getById(transactionTypeId);
        if (!transactionType.getSecuritiesEffect() && !Objects.equals(transactionType.getSecuritiesAccountEffect(), "DR")){
            throw new IncorrectTransactionTypeException(
                    format("Incorrect transactionType:: %s", transactionType.getCode())
            );
        }

        log.info("Retrieving Loan Number with reference id {}", paymentRequest.getReferenceId());
        var loan = loanRepository.findById(paymentRequest.getReferenceId()).orElseThrow(
                () -> new RecordNotFoundException(
                        format("Loan with reference id %s not found", paymentRequest.getReferenceId())
                )
        );

        Securities securities = securityTransactionRepository.findByForceNumberAndCurrencyIdAndLoanNumber(foundMember.getForceNumber(),
                        paymentRequest.getCurrencyId(), loan.getLoanNumber())
                .orElseThrow(() -> new RecordNotFoundException(
                        format("No securities found for force number: %s, with currency ID: %s", foundMember.getForceNumber(),
                                paymentRequest.getCurrencyId())
                ));


        if(!Objects.equals(securities.getCurrencyId(), paymentRequest.getCurrencyId())){
            throw new CurrencyMismatchException(
                    format("Securities currency does not match currency id %s", paymentRequest.getCurrencyId())
            );
        }
        double balance = securities.getSecuritiesAmount() - securities.getSecuritiesAmountPaid();

        if (balance == 0){
            throw new LoanFullyPaidException(format("Securities with force number %s has no balance", securities.getForceNumber()));
        }

        double paymentAmount = paymentRequest.getAmount();
        double updatedAmountPaid = securities.getSecuritiesAmountPaid() + paymentAmount;
        double originalAmount = securities.getSecuritiesAmount();

        securities.setSecuritiesAmountPaid(updatedAmountPaid);
        securities.setBalance(originalAmount - updatedAmountPaid);
        checkIfSecuritiesIsPaidOrOverPaid(securities);
        if (updatedAmountPaid >= originalAmount) {
            securities.setIsPaid(true);
            securities.setSecuritiesStatus(PAID);
            securityTransactionRepository.save(securities);
            alertService.sendSecuritiesPaidUpAlert(List.of(securities));
        }
        else {
            securities.setIsPaid(false);
            securities.setSecuritiesStatus(OVERDUE);
            securityTransactionRepository.save(securities);
        }

        return newTransaction(paymentType, foundMember, transactionTypeId, date,
                valueOf(paymentAmount), paymentRequest.getCurrencyId(), securities.getId()
        );
    }

    private Transactions payLoan(PaymentType paymentType, PaymentRequest paymentRequest,
                                 Long transactionTypeId, LocalDate date, Member foundMember) {

        var transactionType = parameterServiceClient.getById(transactionTypeId);

        switch (paymentType) {
            case LOAN_REPAYMENT -> {
                if (!transactionType.getLoansEffect() ||
                        !Objects.equals(transactionType.getLoansAccountEffect(), "DR")) {
                    throw new IncorrectTransactionTypeException(
                            format("Transaction type %s is not valid for paying a loan", transactionType.getCode())

                    );
                }
            }
            case PROJECT_REPAYMENT -> {
                if (!transactionType.getProjectEffect() ||
                        !Objects.equals(transactionType.getProjectAccountEffect(), "DR")) {
                    throw new IncorrectTransactionTypeException(
                            format("Transaction type %s is not valid for paying a project", transactionType.getCode())

                    );
                }
            }
            default -> throw new InvalidPaymentTypeException(
                    format("Unsupported Payment Type: %s", paymentType.name())
            );
        }

        LoanType loanType = switch (paymentType) {
            case LOAN_REPAYMENT -> LOAN;
            case PROJECT_REPAYMENT -> LoanType.PROJECT;
            default -> throw new InvalidPaymentTypeException(
                    format("Invalid Payment type: %s", paymentType.name()));
        };

        Loan foundLoan = loanRepository.findByForceNumberAndLoanStatusAndLoanType(
                        foundMember.getForceNumber(), OPEN, loanType)
                .filter(loan -> loan.getCurrencyId().equals(paymentRequest.getCurrencyId()))
                .orElseThrow(() -> new RecordNotFoundException(
                        format("No open %s found for force number %s", loanType, foundMember.getForceNumber())));

        if(!Objects.equals(foundLoan.getCurrencyId(), paymentRequest.getCurrencyId())){
            throw new CurrencyMismatchException(
                    format("Loan/Project currency does not match currency id %s", paymentRequest.getCurrencyId())
            );
        }
        double paymentAmount = paymentRequest.getAmount();
        foundLoan.setAmountPaid(foundLoan.getAmountPaid() + paymentAmount);
        foundLoan.setBalance(foundLoan.getBalance() - paymentAmount);
        foundLoan.setDefaulted(false);

        checkIfLoanIsPaidOrOverPaid(foundLoan);
        loanRepository.save(foundLoan);

        log.info("Processed {} payment for loan {}", loanType, foundLoan.getLoanNumber());

        return newTransaction(paymentType, foundMember, transactionTypeId, date,
                valueOf(paymentAmount), paymentRequest.getCurrencyId(), foundLoan.getId()
        );
    }

    private static void checkIfSecuritiesIsPaidOrOverPaid(Securities foundSecurities){
        log.info("Check for securities overdraft");
        if (foundSecurities.getBalance() > 0) return;

        foundSecurities.setBalance(0.0);

        if (foundSecurities.getSecuritiesAmountPaid() > foundSecurities.getSecuritiesAmount()) {
            foundSecurities.setOverdraftAmount(foundSecurities.getSecuritiesAmountPaid() - foundSecurities.getSecuritiesAmount());
            foundSecurities.setSecuritiesStatus(SecuritiesStatus.OVER_PAID);
        } else {
            foundSecurities.setSecuritiesStatus(PAID);
        }

    }
    private static void checkIfLoanIsPaidOrOverPaid(Loan foundLoan) {
        if (foundLoan.getBalance() > 0) return;

        foundLoan.setBalance(0.0);

        if (foundLoan.getAmountPaid() > foundLoan.getLoanAdvance()) {
            foundLoan.setOverPaidAmount(foundLoan.getAmountPaid() - foundLoan.getLoanAdvance());
            foundLoan.setLoanStatus(LoanStatus.OVER_PAID);
        } else {
            foundLoan.setLoanStatus(LoanStatus.PAID);
        }
    }

    private Transactions paySubscription(PaymentType paymentType, PaymentRequest paymentRequest,
                                         Long transactionTypeId, LocalDate date, Member foundMember) {

        var transactionType = parameterServiceClient.getById(transactionTypeId);

        if (!Objects.equals(paymentType, PaymentType.SUBSCRIPTION)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, PaymentType.SUBSCRIPTION)
            );
        }
        if (!transactionType.getSubscriptionEffect() ||
                !Objects.equals(transactionType.getSubsAccountEffect(), "DR")) {
            throw new IncorrectTransactionTypeException(
                    format("Transaction type %s is not valid for paying a subscription", transactionType.getCode())
            );
        }

        var interest = parameterServiceClient.getAll().getContent().stream()
                .filter(i -> i.interestCategory() == SUBSCRIPTION)
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("No interest config with category SUBSCRIPTION found"));

        var subscription = subscriptionsRepository.findByForceNumberAndCurrencyId(
                foundMember.getForceNumber(), paymentRequest.getCurrencyId()
        ).orElseThrow(() -> new RecordNotFoundException(
                format("Subscription account with force number %s not found", foundMember.getForceNumber()))
        );

        if (Objects.equals(subscription.getAccountStatus(), AccountStatus.CLOSED)){
            throw new AccountClosedException(
                    "Subscription account with force number " + foundMember.getForceNumber() + " closed"
            );
        }

        if(!Objects.equals(subscription.getCurrencyId(), paymentRequest.getCurrencyId())){
            throw new CurrencyMismatchException(
                    format("Subscription account currency does not match currency id %s", paymentRequest.getCurrencyId())
            );
        }
        BigDecimal paymentAmount = valueOf(paymentRequest.getAmount());
        BigDecimal currentBalance = subscription.getCurrentBalance();

        subscription.setCurrencyId(paymentRequest.getCurrencyId());
        subscription.setBalanceBForward(subscription.getBalanceBForward().add(currentBalance));
        subscription.setCurrentBalance(currentBalance.add(paymentAmount));
        subscription.setInterestToDate(subscription.getInterestToDate() + interest.interestPercentage());

        log.info("Processing subscription payment for account ID: {}", subscription.getId());
        subscriptionsRepository.save(subscription);

        return newTransaction(paymentType, foundMember, transactionTypeId, date,
                paymentAmount, paymentRequest.getCurrencyId(), subscription.getId()
        );
    }

    private Transactions payClaim(PaymentType paymentType, PaymentRequest paymentRequest,
                                  Long transactionTypeId, LocalDate date, Member foundMember) {

        log.info("Paying claim for member: {}", foundMember.getForceNumber());

        if (!Objects.equals(paymentType, PaymentType.CLAIM_PAYMENT)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, PaymentType.CLAIM_PAYMENT)
            );
        }
        Claim claim = claimRepository.findByForceNumberAndClaimStatus(foundMember.getForceNumber(), APPROVED)
                .orElseThrow(() -> new RecordNotFoundException("Approved claim not found for force number " +
                        foundMember.getForceNumber()));

        double paymentAmount = Math.round(paymentRequest.getAmount() * 100.0) / 100.0;
        double claimAmount = Math.round(claim.getClaimAmount() * 100.0) / 100.0;

        if (paymentAmount != claimAmount) {
            throw new ClaimPaymentMismatchException(
                    format("Claim amount mismatch: payment amount (%.2f) does not equal claim amount (%.2f)",
                            paymentAmount, claimAmount)
            );
        }

        claim.setClaimStatus(ClaimStatus.CLAIMED);
        claim.setClaimDate(date);
        claim.setCurrencyId(paymentRequest.getCurrencyId());
        claimRepository.save(claim);

        return newTransaction(
                paymentType,
                foundMember,
                transactionTypeId,
                date,
                valueOf(paymentRequest.getAmount()),
                paymentRequest.getCurrencyId(),
                claim.getId()
        );
    }
    @Override
    @Transactional
    public PaymentBulkUploadResponse uploadPaymentExcel(MultipartFile file,
                                                        Long transactionTypeId,
                                                        LocalDate transactionDate,
                                                        PaymentType paymentType,
                                                        Long currencyId) {
        return switch (paymentType) {
            case SUBSCRIPTION, LOAN_REPAYMENT,PROJECT_REPAYMENT -> handlePayment(file, transactionTypeId, transactionDate, paymentType, currencyId);
            case LOAN_INTEREST,PROJECT_INTEREST,SUBSCRIPTION_INTEREST,CLAIM_PAYMENT,REVERSAL-> null;
            case SECURITY_REPAYMENT -> handleSecurityPayment(file, transactionTypeId, transactionDate, paymentType);

        };
    }

    private Transactions reverseLoanOrProjectPayment(PaymentType paymentType, PaymentReversalRequest paymentReversalRequest,
                                                     Long transactionTypeId, LocalDate date, Member member, ReversalAction reversalAction) {

        var transactionType = parameterServiceClient.getById(transactionTypeId);

        if (!Objects.equals(paymentType, PaymentType.REVERSAL)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, PaymentType.REVERSAL)
            );
        }

        switch (reversalAction) {
            case LOAN -> {
                if (!transactionType.getLoansEffect() ||
                        !Objects.equals(transactionType.getLoansAccountEffect(), "CR")) {
                    throw new IncorrectTransactionTypeException(
                            format("Transaction type %s is not valid for reversing loan payment", transactionType.getCode())
                    );
                }

            }
            case PROJECT -> {
                if (!transactionType.getProjectEffect() ||
                        !Objects.equals(transactionType.getProjectAccountEffect(), "CR")) {
                    throw new IncorrectTransactionTypeException(
                            format("Transaction type %s is not valid for reversing project payment", transactionType.getCode())
                    );
                }
            }
            default -> throw new InvalidPaymentTypeException(
                    format("Unsupported payment type: %s", paymentType.name())
            );
        }

        Transactions foundTransaction = transactionRepository.findById(paymentReversalRequest.getTransactionId())
                .orElseThrow(() -> new RecordNotFoundException(
                        format("Transaction id %s not found", paymentReversalRequest.getTransactionId())
                ));

        if (!Objects.equals(paymentReversalRequest.getCurrencyId(), foundTransaction.getCurrencyId())) {
            throw new CurrencyMismatchException(
                    format("Reversal currency ID: %s does not match currency ID: %s.", paymentReversalRequest.getCurrencyId(),
                            foundTransaction.getCurrencyId())
            );
        }
        var loanOpt = loanRepository.findById(foundTransaction.getReferenceId());
        if (loanOpt.isEmpty()) {
            throw new RecordNotFoundException(
                    format("Loan/Project not found for reference ID: %s", foundTransaction.getReferenceId())
            );
        }

        Loan loan = loanOpt.get();
        double amountToReverse = foundTransaction.getAmount().doubleValue();
        double latestAmountPaid = loan.getAmountPaid() - amountToReverse;
        double newBalance = loan.getBalance() + amountToReverse;

        loan.setAmountPaid(latestAmountPaid);
        loan.setBalance(newBalance);

        if (loan.getBalance() > 0 && LoanStatus.CLOSED.equals(loan.getLoanStatus())) {
            loan.setLoanStatus(LoanStatus.OPEN);
        }

        Loan savedLoan = loanRepository.save(loan);

        var transactions =  newTransaction(
                paymentType,
                member,
                transactionTypeId,
                date,
                BigDecimal.valueOf(paymentReversalRequest.getReversalAmount()),
                paymentReversalRequest.getCurrencyId(),
                savedLoan.getId()
        );
        transactions.setReversedTransactionId(paymentReversalRequest.getTransactionId());
        foundTransaction.setIsReversed(true);
        transactionRepository.save(foundTransaction);
        return transactionRepository.save(transactions);
    }
    private Transactions reverseSubscriptionPayment(PaymentType paymentType, PaymentReversalRequest paymentReversalRequest,
                                                     Long transactionTypeId, LocalDate date, Member member, ReversalAction reversalAction) {

        var transactionType = parameterServiceClient.getById(transactionTypeId);

        if (!Objects.equals(paymentType, PaymentType.REVERSAL)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, PaymentType.REVERSAL)
            );
        }
        if (!transactionType.getSubscriptionEffect() ||
                !Objects.equals(transactionType.getSubsAccountEffect(), "CR")) {
            throw new IncorrectTransactionTypeException(
                    format("Transaction type %s is not valid for reversing a subscription payment",
                            transactionType.getCode())
            );
        }
        if (!Objects.equals(reversalAction, ReversalAction.SUBSCRIPTION)){
            throw new InvalidReversalAction(
                    format("Invalid reversal transaction, provided %s, but required option is, %s ",
                            reversalAction, ReversalAction.SUBSCRIPTION)
            );
        }

        var transactionOpt = transactionRepository.findById(paymentReversalRequest.getTransactionId());
        if (transactionOpt.isEmpty()) {
            throw new RecordNotFoundException("Transaction not found for ID: " + paymentReversalRequest.getTransactionId());
        }

        var foundTransaction = transactionOpt.get();
        if (!Objects.equals(paymentReversalRequest.getCurrencyId(), foundTransaction.getCurrencyId())) {
            throw new CurrencyMismatchException(
                    format("Reversal currency ID: %s does not match currency ID: %s.", paymentReversalRequest.getCurrencyId(),
                            foundTransaction.getCurrencyId())
            );
        }

        var subscriptionOpt = subscriptionsRepository.findById(foundTransaction.getReferenceId());
        if (subscriptionOpt.isEmpty()) {
            throw new RecordNotFoundException(
                    format("Subscription not found for reference ID: %s", foundTransaction.getReferenceId())
            );
        }

        SubscriptionsAccount subscriptionsAccount = subscriptionOpt.get();
        double amountToReverse = foundTransaction.getAmount().doubleValue();
        double currentSubBalance = subscriptionsAccount.getCurrentBalance().doubleValue();
        double newBalance = currentSubBalance - amountToReverse;

        subscriptionsAccount.setCurrentBalance(BigDecimal.valueOf(newBalance));

        SubscriptionsAccount savedSubscriptionAccount = subscriptionsRepository.save(subscriptionsAccount);

        var transactions = newTransaction(
                paymentType,
                member,
                transactionTypeId,
                date,
                BigDecimal.valueOf(paymentReversalRequest.getReversalAmount()),
                paymentReversalRequest.getCurrencyId(),
                savedSubscriptionAccount.getId()
        );
        transactions.setReversedTransactionId(paymentReversalRequest.getTransactionId());
        foundTransaction.setIsReversed(true);
        transactionRepository.save(foundTransaction);
        return transactionRepository.save(transactions);
    }

    private Transactions reverseSecuritiesPayment(PaymentType paymentType, PaymentReversalRequest paymentReversalRequest,
                                                     Long transactionTypeId, LocalDate date, Member member, ReversalAction reversalAction) {

        var transactionType = parameterServiceClient.getById(transactionTypeId);

        if (!Objects.equals(paymentType, PaymentType.REVERSAL)) {
            throw new IncorrectPaymentTypeException(
                    format("Incorrect payment type: '%s'. Expected payment type: '%s'.", paymentType, PaymentType.REVERSAL)
            );
        }
        if (!transactionType.getSecuritiesEffect() ||
                !Objects.equals(transactionType.getSecuritiesAccountEffect(), "CR")) {
            throw new IncorrectTransactionTypeException(
                    format("Transaction type %s is not valid for reversing a securities payment",
                            transactionType.getCode())
            );
        }
        if (!Objects.equals(reversalAction, SECURITIES)){
            throw new InvalidReversalAction(
                    format("Invalid reversal transaction, provided %s, but required option is, %s ",
                            reversalAction, SECURITIES)
            );
        }
        var transactionOpt = transactionRepository.findById(paymentReversalRequest.getTransactionId());
        if (transactionOpt.isEmpty()) {
            throw new RecordNotFoundException(
                    format("Transaction not found for ID: %s", paymentReversalRequest.getTransactionId())
            );
        }

        var foundTransaction = transactionOpt.get();
        if (!Objects.equals(paymentReversalRequest.getCurrencyId(), foundTransaction.getCurrencyId())) {
            throw new CurrencyMismatchException(
                    format("Reversal currency ID: %s does not match currency ID: %s.", paymentReversalRequest.getCurrencyId(),
                            foundTransaction.getCurrencyId())
            );
        }
        var securitiesOpt = securityTransactionRepository.findById(foundTransaction.getReferenceId());
        if (securitiesOpt.isEmpty()) {
            throw new RecordNotFoundException(
                    format("Loan not found for reference ID: %s", foundTransaction.getReferenceId())
            );
        }

        Securities securities = getSecurities(securitiesOpt, foundTransaction);

        Securities savedSecurities = securityTransactionRepository.save(securities);

        Transactions transactions = newTransaction(
                paymentType,
                member,
                transactionTypeId,
                date,
                BigDecimal.valueOf(paymentReversalRequest.getReversalAmount()),
                paymentReversalRequest.getCurrencyId(),
                savedSecurities.getId()
        );
        transactions.setReversedTransactionId(paymentReversalRequest.getTransactionId());
        foundTransaction.setIsReversed(true);
        transactionRepository.save(foundTransaction);
        return transactionRepository.save(transactions);
    }

    private static Securities getSecurities(Optional<Securities> securitiesOpt, Transactions foundTransaction) {
        Securities securities = securitiesOpt.get();
        double amountToReverse = foundTransaction.getAmount().doubleValue();
        double latestAmountPaid = securities.getSecuritiesAmountPaid() - amountToReverse;
        double newBalance = securities.getBalance() + amountToReverse;

        securities.setBalance(newBalance);
        securities.setSecuritiesAmountPaid(latestAmountPaid);


        if (securities.getBalance() > 0 && securities.getIsPaid()) {
            securities.setIsPaid(false);
        }
        return securities;
    }

    private PaymentBulkUploadResponse handleSecurityPayment(MultipartFile file,
                                                            Long transactionTypeId,
                                                            LocalDate transactionDate,
                                                            PaymentType paymentType) {
        // TODO: Implement security payment processing logic
        return null;
    }

    private Transactions  newTransaction(PaymentType paymentType, Member member, Long transId,
                                         LocalDate date, BigDecimal amount, Long currencyId, Long referenceId) {
        var transType = parameterServiceClient.getById(transId);
        if(transType == null){
            throw new RecordNotFoundException("Failed to retrieve transaction type");
        }
        var transaction = Transactions.builder()
                .forceNumber(member.getForceNumber())
                .transactionCode(transType.getCode())
                .transactionDate(date)
                .datePosted(LocalDateTime.now())
                .audit(new Audit())
                .amount(amount)
                .referenceId(referenceId)
                .currencyId(currencyId)
                .paymentType(paymentType)
                .build();

        transaction.setTransactor(SecurityContextHolder.getContext().getAuthentication().getName());

        var savedTransaction = transactionRepository.save(transaction);

        log.info("Updating account balances and posting transactions");
        postTransactions(List.of(savedTransaction));
       return savedTransaction;
    }

    private void transactionUpdate(List<Transactions> transactions ){

        switch (transactions.get(0).getPaymentType()) {
            case SUBSCRIPTION -> {

                log.info("Updating subscription accounts current balance");
                updateSubscriptionAccountBalances(transactions);
            }
            case LOAN_REPAYMENT -> {
                log.info("Updating Loan accounts current balance");
                updateLoanAccountBalances(transactions, LOAN);
            }
            case PROJECT_REPAYMENT -> {
                log.info("Updating Project accounts current balance");
                updateLoanAccountBalances(transactions,LoanType.PROJECT);
            }
            case SECURITY_REPAYMENT ->
                log.info("Skipping account balance update for {}", transactions.get(0).getPaymentType());

        }

        log.info("Posting transactions in Batch");
        postTransactions(transactions);

    }

    private void  updateSubscriptionAccountBalances(List<Transactions> transactions) {

        List<SubscriptionsAccount> updatedAccounts = new ArrayList<>();
        List<Transactions> updatedTransactions = new ArrayList<>();

        transactions.forEach(transaction -> {

            log.info("Checking if exists an account with given force number : {}",
                    transaction.getForceNumber());

            var account = subscriptionsRepository.findByForceNumberAndCurrencyId(transaction.getForceNumber()
                    ,transaction.getCurrencyId());

            account.ifPresent(retrievedAccount->{
                log.info("Updating account current balance from : {} to  : {}",retrievedAccount.getCurrentBalance(),
                        transaction.getAmount().add(retrievedAccount.getCurrentBalance()));

                retrievedAccount.setCurrentBalance(transaction.getAmount().add(retrievedAccount.getCurrentBalance()));

                updatedAccounts.add(retrievedAccount);
                log.info("updating subscription transaction by setting a reference id");
                transaction.setReferenceId(account.get().getId());
                updatedTransactions.add(transaction);

                log.info("saving updated subscription transaction : {}",transaction);

                log.info("saving updated subscription transaction : {}",transaction);
            });

        });
        log.info("Saving all Updated subscription transactions");
        transactionRepository.saveAll(updatedTransactions);

        log.info("Saving all Updated Subscription account");
        subscriptionsRepository.saveAll(updatedAccounts);
    }

    private void  updateLoanAccountBalances(List<Transactions> transactions,LoanType loanType) {
        List<Loan> updatedLoanAccounts = new ArrayList<>();
        List<Transactions> updatedTransactions = new ArrayList<>();

        transactions.forEach(transaction -> {

            log.info("Checking if exists an loan account with given force number : {}",
                    transaction.getForceNumber());
            var loan = loanRepository.findByForceNumberAndLoanStatusAndLoanType(
                    transaction.getForceNumber(), OPEN, loanType);

            loan.ifPresent(retrievedLoan -> {
                var newBalance = retrievedLoan.getLoanAdvance() - retrievedLoan.getAmountPaid();
                retrievedLoan.setAmountPaid(retrievedLoan.getAmountPaid() + transaction.getAmount().doubleValue());
                retrievedLoan.setBalance(newBalance);
                retrievedLoan.setDefaulted(false);
                checkIfLoanIsPaidOrOverPaid(retrievedLoan);
                log.info("Processing loan payments for loan {}", retrievedLoan.getLoanNumber());
                updatedLoanAccounts.add(retrievedLoan);

                log.info("updating loan transaction by setting a reference id");
                transaction.setReferenceId(loan.get().getId());
                updatedTransactions.add(transaction);

                log.info("saving updated loan transaction : {}",transaction);

            });
        });

        log.info("Saving all Updated Loan transactions");
        transactionRepository.saveAll(updatedTransactions);

        log.info("Saving all Updated Loan account");
        loanRepository.saveAll(updatedLoanAccounts);
    }

    private void postTransactions (List<Transactions> transactions){

        log.info("Posting transaction to General Ledger");
        try {

            List<ProcessTransTypeRequest> requests = transactions.stream()
                    .map(transaction -> new ProcessTransTypeRequest(
                            transaction.getTransactionCode(),
                            transaction.getAmount(),
                            transaction.getForceNumber(),
                            format("%s payment for %s",transaction.getPaymentType(),
                                    transaction.getForceNumber()
                            )))
                    .toList();

            glServiceClient.postTransactionEntries(requests);

        } catch (FeignException.BadRequest e) {
            log.info("error {}", e.contentUTF8());
            String errorResponse = e.contentUTF8();
            ErrorResponse response = objectMapper(errorResponse);
            throw new PostTransactionException(response.error());
        }
        catch (FeignException e) {
            log.error("Error retrieving account details via feign: {}", e.getMessage());
            throw new RuntimeException("Error posting transactions via Feign: " + e.getMessage(), e);
        }
    }

    private PaymentBulkUploadResponse handlePayment(MultipartFile file, Long transactionTypeId
            , LocalDate date, PaymentType paymentType, Long currencyId){

        List<UploadResponse> uploadResponses = new ArrayList<>();
        List<Transactions> transactions = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                String forceNumber = row.getCell(0).getStringCellValue();
                BigDecimal amount = valueOf(row.getCell(1).getNumericCellValue());

                var member = memberRepository.findMemberByForceNumber(forceNumber);
                if (member.isEmpty()) {
                    uploadResponses.add(new UploadResponse(forceNumber, amount, Status.FAILED,
                            format("Member with force number %s not found", forceNumber)));
                    continue;
                }

                switch (paymentType) {
                    case LOAN_REPAYMENT -> {
                        var loan = loanRepository.findByForceNumberAndLoanStatusAndLoanType(member.get().getForceNumber(), OPEN, LOAN);
                        if (loan.isEmpty()) {
                            uploadResponses.add(new UploadResponse(forceNumber, amount, Status.FAILED,
                                    format("Member with force number %s does not have an Open Loan", forceNumber)));
                            continue;
                        }
                    }
                    case SUBSCRIPTION -> {
                        var account = subscriptionsRepository.findByForceNumberAndCurrencyId(member.get().getForceNumber(),
                                currencyId);
                        if (account.isEmpty()) {
                            uploadResponses.add(new UploadResponse(forceNumber, amount, Status.FAILED,
                                    format("Member with force number %s does not have Subscription", forceNumber)));
                            continue;
                        }

                    }
                    case PROJECT_REPAYMENT -> {
                        var loan = loanRepository.findByForceNumberAndLoanStatusAndLoanType(
                                member.get().getForceNumber(), OPEN, LoanType.PROJECT);
                        if (loan.isEmpty()) {
                            uploadResponses.add(new UploadResponse(forceNumber, amount, Status.FAILED,
                                    format("Member with force number %s does not have an Open Project", forceNumber)));
                            continue;
                        }
                    }
                }

                log.info("Retrieved member : {}  with forceNumber: {}", member, forceNumber);

                var transType = parameterServiceClient.getById(transactionTypeId);
                assert transType != null;
                var transaction = Transactions.builder()
                        .forceNumber(member.get().getForceNumber())
                        .transactionCode(transType.getCode())
                        .transactionDate(date)
                        .datePosted(LocalDateTime.now())
                        .transactor(getAuthenticatedUser())
                        .audit(new Audit())
                        .amount(amount)
                        .paymentType(paymentType)
                        .currencyId(currencyId)
                        .build();

                log.info("Saving transaction for Member with forceNumber: {}", forceNumber);
                transactions.add(transaction);
                uploadResponses.add(new UploadResponse(forceNumber, amount,
                        Status.SUCCESS, format("%s transaction saved successfully",transaction.getPaymentType())));
            }

            if(!transactions.isEmpty()){

                log.info("Saving transactions to database :{}", transactions);
                var savedTransactions = transactionRepository.saveAll(transactions);
                log.info("{} transactions saved to database", savedTransactions.size());

                log.info("Updating accounts current balance an posting transactions");
                transactionUpdate(savedTransactions);
            }


        } catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return PaymentBulkUploadResponse.builder()
                .audit(new zw.co.afrosoft.zdf.util.Audit())
                .uploads(uploadResponses)
                .succeededUploads(uploadResponses.stream().filter(response -> response.status()
                        == Status.SUCCESS).count())
                .failedUploads(uploadResponses.stream().filter(response -> response.status()
                        == Status.FAILED).count())
                .build();
    }

    public String getAuthenticatedUser() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.nonNull(user))
            return user.getUsername();
        return "anonymous";
    }

    private ErrorResponse objectMapper(String errorResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse response;
        try {
            response = objectMapper.readValue(errorResponse, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
