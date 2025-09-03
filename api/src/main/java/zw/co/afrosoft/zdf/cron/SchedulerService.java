package zw.co.afrosoft.zdf.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zw.co.afrosoft.zdf.alert.AlertService;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.interest.InterestApplicationService;
import zw.co.afrosoft.zdf.interest.InterestTrackingRepository;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanService;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.member.MemberService;
import zw.co.afrosoft.zdf.subscription.SubscriptionService;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static zw.co.afrosoft.zdf.enums.LoanStatus.*;
import static zw.co.afrosoft.zdf.member.MemberStatus.IN_ACTIVE;


@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final LoanRepository loanRepository;
    private final LoanService loanService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AlertService alertService;

//    @Scheduled(cron = "0 */10 * * * *") // Runs every 10 minutes
    public void checkApprovedLoans(){
        log.info("Checking for approved loans");
        loanRepository.findAll().forEach(loan -> {
            if (loan.getLoanStatus().equals(APPROVED)){
                loan.setLoanStatus(OPEN);
                loanRepository.save(loan);
            }
        });
    }
    // Runs every 3hours
    @Scheduled(cron = "0 0 */3 * * *")
    public void checkPaidLoans(){
        log.info("Closing all paid-up loans");
        loanRepository.findAll().stream()
                .filter(loan -> loan.getLoanStatus().equals(PAID))
                .forEach(loan -> {
                    loan.setLoanStatus(CLOSED);
                    loanRepository.save(loan);
                });
    }

    // Runs daily at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void approveLoans() {
        loanRepository.findAll()
                .forEach(loan -> {
                    if (loanRepository.existsByLoanNumberAndLoanStatus(loan.getLoanNumber(), PENDING)){
                        log.info("Approving loan {}", loan.getLoanNumber());
                        loanService.approveLoan(loan.getLoanNumber());
                    }
                });
    }

    // Runs at midnight on the 28th of every month
    @Scheduled(cron = "0 0 0 28 * ?")
    public void checkForLoanDefaults() {
        List<Loan> activeLoans = loanRepository.findByLoanStatus(OPEN);

        LocalDateTime today = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(today);

        List<Loan> defaultedLoans = new ArrayList<>();
        activeLoans.forEach(loan -> {
            // Determine if a payment was made this month
            boolean paymentMade = loan.getAudit() != null &&
                    loan.getAudit().getModifiedDate() != null &&
                    YearMonth.from(loan.getAudit().getModifiedDate()).equals(currentMonth);

            // If payment not made, flag as defaulted
            if (!paymentMade && loan.getBalance() > 0) {
                loan.setDefaulted(true);
                loan.setLoanStatus(LoanStatus.DEFAULTED);
                defaultedLoans.add(loan); // Collect defaulted loans for alert
            }

        });

        if (!defaultedLoans.isEmpty()) {
            // Send alerts before saving
            alertService.sendLoanDefaultAlert(defaultedLoans);
            loanRepository.saveAll(defaultedLoans);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void activateMembership(){
        log.info("Activating Membership");
        memberRepository.findAll().stream()
                .filter(member -> member.getMemberStatus().equals(IN_ACTIVE))
                .forEach(member -> memberService.activateMember(member.getId()));
    }
}
