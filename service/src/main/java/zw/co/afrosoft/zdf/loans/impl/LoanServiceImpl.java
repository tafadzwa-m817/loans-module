package zw.co.afrosoft.zdf.loans.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.alert.AlertServiceImpl;
import zw.co.afrosoft.zdf.dto.MemberLoanDetailsRequest;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.exceptions.*;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.loans.*;
import zw.co.afrosoft.zdf.dto.LoanRequestDto;
import zw.co.afrosoft.zdf.member.Member;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.member.PersonalDetails;
import zw.co.afrosoft.zdf.member.ServiceType;
import zw.co.afrosoft.zdf.notification.NotificationServiceImpl;
import zw.co.afrosoft.zdf.payments.PaymentsRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesService;
import zw.co.afrosoft.zdf.util.LoanUtil;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.requireNonNull;
import static zw.co.afrosoft.zdf.enums.LoanStatus.*;
import static zw.co.afrosoft.zdf.loans.AccountType.valueOf;
import static zw.co.afrosoft.zdf.member.MemberStatus.ACTIVE;



@SuppressWarnings("ALL")
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final PaymentsRepository paymentsRepository;
    private final MemberRepository memberRepository;
    private final NotificationServiceImpl notificationService;
    private final SecuritiesService securitiesService;
    private final MessageSource messageSource;
    private final ParameterServiceClient parameterServiceClient;
    private final AlertServiceImpl alertService;
    private final LoanAccountRepository loanAccountRepository;
    private final LoanStatusLogsRepository loanStatusLogsRepository;

    private static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Loan createLoan(LoanRequestDto loanRequestDto) {
                requireNonNull(loanRequestDto, "Loan request DTO cannot be null");

                log.info("Validating Loan Request with Loan type, {}, and Duration: {}",loanRequestDto.getLoanType()
                        , loanRequestDto.getDuration());
        validateDuration(loanRequestDto);

        var member = memberRepository.findMemberByForceNumber(loanRequestDto.getForceNumber());
        member.ifPresent(foundMember -> {
            if (!Objects.equals(foundMember.getMemberStatus(),ACTIVE))
                throw new MemberInactiveException(
                        format("Member with force number, %s, is not active",
                                foundMember.getForceNumber()));

            if (loanRepository.existsByForceNumberAndLoanStatusAndLoanType(foundMember.getForceNumber(),
                    OPEN, loanRequestDto.getLoanType())){
                throw new OutStandingLoanException(
                        format("Member with force number, %s, already has an outstanding loan",
                                foundMember.getForceNumber())
                );
            }
        });

        log.info("Creating loan for member with forceNumber:: {}", loanRequestDto.getForceNumber());
        return loanRepository.save(createNewLoan(member.get(), loanRequestDto));

    }

    @Override
    public Loan addMemberDetails(String loanNumber, MemberLoanDetailsRequest memberLoanDetailsRequest) {
        var loan = loanRepository.findByLoanNumber(loanNumber);
        loan.ifPresent(foundLoan -> {
            foundLoan.setPersonalDetails(
                    PersonalDetails.builder()
                            .firstName(memberLoanDetailsRequest.getFirstName())
                            .lastName(memberLoanDetailsRequest.getLastName())
                            .nationalId(memberLoanDetailsRequest.getNationalId())
                            .maritalStatus(memberLoanDetailsRequest.getMaritalStatus())
                            .dOB(memberLoanDetailsRequest.getDOB())
                            .gender(memberLoanDetailsRequest.getGender())
                            .address(memberLoanDetailsRequest.getAddress())
                            .build()
            );
            loanRepository.save(foundLoan);
        });

        return loan.get();
    }

    @Override
    public void approveLoan(String loanNumber) {
        requireNonNull(loanNumber, "Loan number cannot be null");
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow( () -> new RecordNotFoundException(
                        format("Loan with loan number %s, does not exist", loanNumber)
                ));
        if (loan.getLoanStatus().equals(CLOSED))
        log.info("Approving loan with loanNumber, %s, for member with forceNumber, %s",
                loanNumber, loan.getForceNumber());
        loan.setLoanStatus(OPEN);
        log.info("Adding loan security transaction to with loanNumber, %s, for member with forceNumber, %s"
                ,loanNumber, loan.getForceNumber());
        Double securityAmount = 0.0d;
        var duration = loan.getDuration();
        switch (loan.getLoanType()){
            case LOAN -> securityAmount = securitiesService.calculateLoanSecurity(loan.getLoanAdvance(), loan.getDuration());
            case PROJECT -> securityAmount = securitiesService.calculateProjectSecurity(loan.getLoanAdvance(),loan.getDuration());
        }

        loanRepository.save(loan);
        if (!loanAccountRepository.existsByForceNumberAndAccountType(loan.getForceNumber(),
                valueOf(loan.getLoanType().name()))){
            loanAccountCreation(loan.getLoanType(), loan);
        }
        securitiesService.addLoanSecurityTransaction(loan.getId(), securityAmount);
    }

    private void loanAccountCreation(LoanType loanType, Loan loan){
        memberRepository.findMemberByForceNumber(loan.getForceNumber())
                .ifPresent(member -> {
                    log.info("Generating loan account for member with forceNumber:: {}",
                            member.getForceNumber());
                    String loanAccountNumber = generateLoanAccountNumber(loanType,
                            member.getServiceType());
                            loanAccountRepository.save(LoanAccount.builder()
                                    .loanAccountNumber(loanAccountNumber)
                                    .forceNumber(member.getForceNumber())
                                    .membershipNumber(member.getMembershipNumber())
                                    .accountType(valueOf(loanType.name()))
                                    .loans(List.of(loan))
                                    .dateCreated(now())
                                    .audit(new Audit())
                                    .build());
                        });

    }
    private String generateLoanAccountNumber(LoanType loanType, ServiceType service) {
        AtomicInteger nextNumber = new AtomicInteger(1);
        StringBuilder suffix = new StringBuilder();
        String prefix;
        if(service == ServiceType.AIR_FORCE_OF_ZIMBABWE)
            prefix = "AFZ";
        else
            prefix = "ZNA";
        loanAccountRepository.findTopByAccountTypeOrderByIdDesc(valueOf(loanType.name()))
                .ifPresent(loanAccount ->{
                    String loanAccountNumber = loanAccount.getLoanAccountNumber();
                    String numberPart = loanAccountNumber.substring(3, 11);
                    nextNumber.set(Integer.parseInt(numberPart) + 1);
                } );
        switch (loanType) {
            case LOAN -> suffix.append("L");
            case PROJECT -> suffix.append("P");
        }
        String formattedNumber = format("%08d", nextNumber.get());
        return  prefix + formattedNumber + suffix;
    }
    @Override
    public Loan findLoanById(Long id) {
        return loanRepository.findById(id).orElseThrow(
                () -> new RecordNotFoundException(
                        format("Loan with id %d does not exist", id)
                ));
    }

    @Override
    public Loan closeLoan(String loanNumber) {
        requireNonNull(loanNumber, "Loan number cannot be null");
        if (!loanRepository.existsByLoanNumber(loanNumber)){
            throw new RecordNotFoundException(format("Loan number, %s, not found", loanNumber));
        }
        Optional<Loan> loan = loanRepository.findByLoanNumber(loanNumber);
        loan.ifPresent(loanFound -> {
            if (!Objects.equals(loanFound.getLoanStatus(), PAID) &&
            loanFound.getBalance() != 0 ){
                throw new OutStandingLoanException(
                        format("Member still has an outstanding balance of %s", loanFound.getBalance())
                );
            }
            loanFound.setLoanStatus(CLOSED);
            loanFound.setDateClosed(now());

            loanRepository.save(loanFound);
        });
        return loan.get();
    }

    @Override
    public Loan updateLoan(Long id, LoanRequestDto loanRequestDto) {
        requireNonNull(id, "Id cannot be null");
        requireNonNull(loanRequestDto, "Update loan request dto cannot be null");
        Loan loan = loanRepository.findById(id).orElseThrow(
                () -> new RecordNotFoundException(
                        format("Loan, %s, not found", id)));
        if (Objects.equals(loan.getLoanStatus(), OPEN))
            throw new LoanAlreadyOpenException(format("Loan with id, %d already open, " +
                    "cannot update loan details for an open loan", id));
        log.info("Updating loan {}", loan);
        var loanNumber = loan.getLoanNumber();
        loan.setDuration(loanRequestDto.getDuration());
        loan.setForceNumber(loanRequestDto.getForceNumber());
        loan.setDueDate(now().plusMonths(loanRequestDto.getDuration()));
        loan.setLoanAdvance(loanRequestDto.getPrincipalAmount());
        loan.setCurrencyId(loan.getCurrencyId());
        loan.setBalance(loanRequestDto.getPrincipalAmount());
        return loanRepository.save(loan);
    }

    @Override
    public Loan updateLoanStatus(LoanUpdateStatusRequest loanUpdateStatusRequest) {
        requireNonNull(loanUpdateStatusRequest,"LoanUpdateStatusRequest cannot be null");
        Loan loan = loanRepository.findById(loanUpdateStatusRequest.loanId()).orElseThrow(() ->
                new RecordNotFoundException(format("Loan, %s, not found", loanUpdateStatusRequest.loanId())));
        if (Objects.equals(loanUpdateStatusRequest.loanStatus(), OPEN) && loan.getLoanStatus() != OPEN){
            approveLoan(loan.getLoanNumber());
            return loan;
        }else {
            loan.setLoanStatus(loanUpdateStatusRequest.loanStatus());
            loan.setComment(loanUpdateStatusRequest.comment());
            log.info("Updating loan status {}", loan);
            var updatedLoan = loanRepository.save(loan);
            handleLoanStatusUpdate(updatedLoan, loanUpdateStatusRequest);
            return updatedLoan;
        }
    }

//    private void applySecurities(Loan loan, Double securityAmount) {
//        switch (loan.getLoanType()){
//            case LOAN -> securityAmount = securitiesService.calculateLoanSecurity(loan.getPrincipalAmount());
//            case PROJECT -> securityAmount = securitiesService.calculateProjectSecurity(loan.getPrincipalAmount());
//        }
//
//        loanRepository.save(loan);
//        if (!loanAccountRepository.existsByForceNumber(loan.getForceNumber())) {
//            loanAccountCreation(loan.getLoanType(), loan);
//        }
//        securitiesService.addLoanSecurityTransaction(loan.getId(), securityAmount);
//    }

    private void handleLoanStatusUpdate(Loan loan, LoanUpdateStatusRequest updateStatusRequest) {

        log.info("Saving status update log for Loan with id : {}", loan.getId());
        loanStatusLogsRepository.save(LoanStatusLogs.builder()
                .comment(updateStatusRequest.comment())
                .loanStatus(updateStatusRequest.loanStatus())
                .loan(loan).
                audit(new Audit())
                .build());
    }
    @Override
    public Page<LoanStatusLogs> showLoanStatusLogs(Long loanId, LoanStatus loanStatus, Pageable pageable) {
        Specification<LoanStatusLogs> spec = LoanLogsSpecification.getProperties(loanId, loanStatus);
        return loanStatusLogsRepository.findAll(spec, pageable);
    }

    @Override
    public void bulkUploadLoans(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            if (file.getOriginalFilename().endsWith(".csv")) {
                new LoanUtil(loanRepository).processCSVFile(inputStream);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                new LoanUtil(loanRepository).processExcelFile(inputStream);
            } else {
                throw new RuntimeException("Unsupported file format");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }

    @Override
    public Page<Loan> getLoans(String forceNumber, String loanNumber, String firstName,
                               String lastName, LoanStatus loanStatus,LoanType loanType,Pageable pageable) {
        return loanRepository.findAllByOrderByIdDesc(forceNumber, loanNumber, firstName,
                lastName, loanStatus,loanType, pageable);
    }


    private Loan createNewLoan(Member member, LoanRequestDto loanRequestDto) {
        PersonalDetails personalDetails = member.getPersonalDetails();
        Loan loan = Loan.builder()
                .forceNumber(loanRequestDto.getForceNumber())
                .loanStatus(PENDING)
                .loanAdvance(loanRequestDto.getPrincipalAmount())
                .duration(loanRequestDto.getDuration())
                .balance(loanRequestDto.getPrincipalAmount())
                .amountPaid(0.0D)
                .dueDate(now().plusDays(loanRequestDto.getDuration()))
                .loanNumber(generateLoanNumber(loanRequestDto.getLoanType()))
                .rankId(member.getRankID())
                .currencyId(loanRequestDto.getCurrencyId())
                .personalDetails(personalDetails)
                .loanType(loanRequestDto.getLoanType())
                .unitId(member.getUnitID())
                .audit(new Audit())
                .build();
        return loan;
    }

    private String generateLoanNumber(LoanType loanType) {
        String prefix = switch (loanType) {
            case LOAN -> "LN";
            case PROJECT -> "PT";
        };

        int nextNumber = loanRepository.findTopByOrderByIdDesc()
                .map(loan -> {
                    String loanNumber = loan.getLoanNumber();
                    try {
                        // Extract numeric part assuming it starts at index 2 (e.g., LN00001 → 00001)
                        return Integer.parseInt(loanNumber.substring(2)) + 1;
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        log.warn("Failed to parse loan number: {}", loanNumber, e);
                        return 1; // fallback
                    }
                })
                .orElse(1);

        return prefix + String.format("%05d", nextNumber);
    }
//    private void notifyMember(String message, String subject, Member member){
//        log.info("Sending notification to Member with forceNumber: {}",
//                member.getForceNumber());
//
//        List<Recipient> recipients = notificationService.getRecipients(
//                List.of(RecipientRequest.builder()
//                        .fullName(member.getPersonalDetails().getFirstName())
//                        .email(member.getPersonalDetails().getEmail())
//                        .phoneNumber(member.getPersonalDetails().getPhoneNumber())
//                        .build())
//        );
//
//        notificationService.sendNotification(
//                message, recipients,
//                subject, true, true, false);
//
//        log.info("Notification sent successfully to Member with forceNumber: {}",
//                member.getForceNumber());
//
//    }
    private void validateDuration(LoanRequestDto request){

        if (request.getDuration() <= 0) {
            throw new InvalidDurationException("Duration must be greater than 0");
        }

        switch (request.getLoanType()) {
            case LOAN -> {
                if (request.getDuration() > 18) {
                    throw new InvalidDurationException("Duration for LOAN type must not exceed 18 months");
                }
            }
            case PROJECT -> {
                if (request.getDuration() > 60) {
                    throw new InvalidDurationException("Duration for PROJECT type must not exceed 60 months");
                }
            }
        }
    }
}
