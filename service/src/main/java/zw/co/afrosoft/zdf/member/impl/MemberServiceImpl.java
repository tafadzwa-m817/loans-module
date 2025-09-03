package zw.co.afrosoft.zdf.member.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.components.excelhelper.impl.ExcelHelper;
import zw.co.afrosoft.zdf.dto.PageResponse;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.feign.dto.RankResponseDto;
import zw.co.afrosoft.zdf.feign.dto.UnitResponseDto;
import zw.co.afrosoft.zdf.logs.MemberStatusLogs;
import zw.co.afrosoft.zdf.logs.MemberStatusRepository;
import zw.co.afrosoft.zdf.member.*;
import zw.co.afrosoft.zdf.member.dto.MemberBulkUploadResponse;
import zw.co.afrosoft.zdf.member.dto.MemberExcelUploadDTO;
import zw.co.afrosoft.zdf.member.dto.MemberUploadResponse;
import zw.co.afrosoft.zdf.member.request.MemberFilterRequest;
import zw.co.afrosoft.zdf.member.request.MemberStatusUpdateRequest;
import zw.co.afrosoft.zdf.subscription.AccountStatus;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;
import zw.co.afrosoft.zdf.utils.enums.BulkMembership;
import zw.co.afrosoft.zdf.utils.enums.Status;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.*;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toMemberResponseDto;
import static zw.co.afrosoft.zdf.member.MemberStatus.IN_ACTIVE;



@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final SubscriptionsAccountRepository subscriptionsRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final ParameterServiceClient parameterServiceClient;
    private final ExcelHelper excelHelper;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;

    @Override
    public Member registerMember(MemberRegistrationRequest memberRegistrationRequest) {
        requireNonNull(memberRegistrationRequest, "MemberRegistrationRequest cannot be null");

        String forceNumber = memberRegistrationRequest.getForceNumber();
        log.info("Validating if member with force number '{}' already exists", forceNumber);

        boolean memberExists = memberRepository.findMemberByForceNumber(forceNumber).isPresent();
        if (memberExists) {
            throw new IllegalArgumentException(
                    String.format("Member with force number '%s' already exists", forceNumber));
        }

        log.info("Registering new member with force number '{}'", forceNumber);

        Member member = Member.builder()
                .serviceType(memberRegistrationRequest.getServiceType())
                .forceNumber(forceNumber)
                .personalDetails(memberRegistrationRequest.getPersonalDetails())
                .prevForceNumber(memberRegistrationRequest.getPrevForceNumber())
                .membershipNumber(generateMembershipNumber(memberRegistrationRequest.getServiceType()))
                .rankID(memberRegistrationRequest.getRankID())
                .unitID(memberRegistrationRequest.getUnitID())
                .membershipDate(memberRegistrationRequest.getMembershipDate())
                .dateOfAttestation(memberRegistrationRequest.getDateOfAttestation())
                .grossSalary(memberRegistrationRequest.getGrossSalary())
                .netSalary(memberRegistrationRequest.getNetSalary())
                .tax(memberRegistrationRequest.getTax())
                .memberStatus(MemberStatus.IN_ACTIVE)
                .build();

        if (member.getAudit() == null) {
            member.setAudit(new Audit());
        }

        return memberRepository.save(member);
    }

    @Override
    public Member activateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Member with id %d not found", id)));

        log.info("Activating member with ID {} and Force Number {}", member.getId(), member.getForceNumber());

        member.setMemberStatus(MemberStatus.ACTIVE);
        Member activatedMember = memberRepository.save(member);

        createSubscriptionAccount(activatedMember);

        return activatedMember;
    }


    private void createSubscriptionAccount(Member member) {
        var rank = parameterServiceClient.getRankById(member.getRankID());
        var currencies = parameterServiceClient.getAllCurrency();

        if (currencies == null || currencies.isEmpty()) {
            log.warn("No currencies found from system parameters. Cannot create subscription accounts.");
            return;
        }

        currencies.forEach(currency -> {
            Long currencyId = currency.getId();
            String currencyName = currency.getCurrencyName();

            boolean accountExists = subscriptionsRepository
                    .existsByCurrencyIdAndForceNumber(currencyId, member.getForceNumber());

            log.info("SubscriptionAccount exist:{}",accountExists);

            if (!accountExists) {
                String generatedAccountNumber = generateSubscriptionAccountNumber(member.getServiceType(),
                        currencyName);
                SubscriptionsAccount account = SubscriptionsAccount.builder()
                        .accountNumber(generatedAccountNumber)
                        .accountStatus(AccountStatus.ACTIVE)
                        .name(member.getPersonalDetails().getFirstName())
                        .surname(member.getPersonalDetails().getLastName())
                        .forceNumber(member.getForceNumber())
                        .serviceType(member.getServiceType())
                        .rank(rank != null ? rank.getName() : null)
                        .currencyId(currencyId)
                        .startDate(LocalDateTime.now())
                        .membershipDate(member.getDateOfAttestation())
                        .currentBalance(BigDecimal.ZERO)
                        .currentArrears(BigDecimal.ZERO)
                        .balanceBForward(BigDecimal.ZERO)
                        .interestToDate(0.0)
                        .audit(new Audit())
                        .build();

                subscriptionsRepository.save(account);
                log.info("Created subscription account for {} in {}", member.getForceNumber(), currencyName);
            } else {
                log.info("Subscription account already exists for {} in {}", member.getForceNumber(), currencyName);
            }
        });
    }
    private String generateSubscriptionAccountNumber(ServiceType service, String currencyPrefix) {
        String prefix = switch (service) {
            case AIR_FORCE_OF_ZIMBABWE -> "AFZ";
            case ZIMBABWE_NATIONAL_ARMY -> "ZNA";
        };

        int nextNumber = subscriptionsRepository.findTopByOrderByIdDesc()
                .map(SubscriptionsAccount::getAccountNumber)
                .map(accNum -> accNum.replaceAll("\\D+", ""))
                .filter(numStr -> !numStr.isEmpty())
                .map(Integer::parseInt)
                .map(num -> num + 1)
                .orElse(1);

        String formattedNumber = String.format("%08d", nextNumber);
        return prefix + currencyPrefix + "_" + formattedNumber + "S";
    }



    @Override
    public Page<Member> getMembers(MemberFilterRequest request, Pageable pageable) {
        requireNonNull(request, "MemberFilterRequest cannot be null");
        Specification<Member> spec = getSpecification(request);
        return (spec != null) ? memberRepository.findAll(spec, pageable) : memberRepository.findAll(pageable);
    }


    @Override
    public MemberResponseDto update(Long id, MemberRegistrationRequest request) {

        requireNonNull(request, "MemberRegistrationRequest cannot be null");
        
        log.info("Checking if member with id : {} exists",id);
        var member = memberRepository.findById(id).orElseThrow(()->  new RecordNotFoundException(
                format("Member with id %s not found", id)
        ));

        log.info("Updating member with request : {}", request);

        member.setServiceType(request.getServiceType());
        member.setForceNumber(request.getForceNumber());
        member.setPersonalDetails(request.getPersonalDetails());
        member.setPrevForceNumber(request.getPrevForceNumber());
        member.setMembershipDate(request.getMembershipDate());
        member.setRankID(request.getRankID());
        member.setUnitID(request.getUnitID());
        member.setGrossSalary(request.getGrossSalary());
        member.setNetSalary(request.getNetSalary());
        member.setTax(request.getTax());

        var updatedMember = memberRepository.save(member);
        updateSubscriptionAccount(updatedMember);
        log.info("Updated member : {}", updatedMember);

        return toMemberResponseDto(updatedMember);
    }

    private void updateSubscriptionAccount(Member member) {
        subscriptionsRepository.findAll().stream()
                .filter(subscriptionsAccount -> Objects.equals(member.getForceNumber(), subscriptionsAccount.getForceNumber()))
                .forEach(subscriptionsAccount -> {
                    log.info("Updating subscription account : {}", subscriptionsAccount);
                    subscriptionsAccount.setName(member.getPersonalDetails().getFirstName());
                    subscriptionsAccount.setSurname(member.getPersonalDetails().getLastName());
                    subscriptionsAccount.setServiceType(member.getServiceType());
                    subscriptionsRepository.save(subscriptionsAccount);
                });
    }
    @Override
    public MemberResponseDto updateStatus(MemberStatusUpdateRequest request) {

        requireNonNull(request, "MemberStatusUpdateRequest cannot be null");

        log.info("Checking if member id : {} exists",request.memberId());
        var member = memberRepository.findById(request.memberId()).orElseThrow(
                ()->  new RecordNotFoundException(
                format("Member with id %s not found", request.memberId())
        ));

        log.info("Updating member status with request : {}", request);
        member.setMemberStatus(request.memberStatus());
        handleMemberStatusUpdateLog(member,request);

        return toMemberResponseDto(memberRepository.save(member));
    }

    @Override
    public Page<MemberStatusLogs> getAllStatusUpdates(Long memberId, MemberStatus status, Pageable pageable) {
        var spec = getMemberStatusProperties(memberId,status);
        return memberStatusRepository.findAll(spec,pageable);
    }

    @Override
    public MemberResponseDto getMemberById(Long id) {

        requireNonNull(id, "Member id cannot be null");

        log.info("Checking if member with given id : {} exists",id);
        var member = memberRepository.findById(id).orElseThrow(
                ()->  new RecordNotFoundException(
                        format("Member with id %s not found", id)
                ));

        return toMemberResponseDto(member);
    }

    @Override
    public MemberBulkUploadResponse uploadMembershipExcel(MultipartFile file, BulkMembership option) {

        return switch(option){
            case REGISTER -> uploadNewMembershipExcel(file);
            case UPDATE-> updateMembershipExcel(file);
        };
    }
    private MemberBulkUploadResponse uploadNewMembershipExcel(MultipartFile file) {
        log.info("Retrieving all ranks and units");
        var allUnits = getAllUnits();
        var allRanks = getAllRanks();

        log.info("Adding new members from excel file : {}", file.getOriginalFilename());

        List<MemberUploadResponse> memberUploadResponses = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean isHeaderRow = true;

            for (Row row : sheet) {
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue;
                }

                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }
                var memberData = extractMemberDataFromRow(row);

                Optional<String> validationError = validateMemberData(row,memberData, allUnits,allRanks);

                if (validationError.isPresent()) {
                    memberUploadResponses.add(buildMemberResponse(memberData.forceNumber(),memberData.firstName(),
                            memberData.lastName(),memberData.nationalID(),memberData.dob(),memberData.maritalStatus()
                            ,memberData.gender(), memberData.serviceType(),memberData.initials(),memberData.prevForceNumber(),
                            memberData.unit(),memberData.rank(), memberData.dateOfAttestation(),
                            memberData.membershipDate(),memberData.grossSalary(),memberData.netSalary(),memberData.tax()
                            ,memberData.address1(),memberData.address2(),memberData.address3(),memberData.address4(),
                            Status.FAILED, validationError.get(),memberData.phoneNumber()));

                    continue;
                }

                var memberResponse = buildMemberResponse(memberData.forceNumber(),memberData.firstName(),
                        memberData.lastName(),memberData.nationalID(),memberData.dob(),memberData.maritalStatus()
                        ,memberData.gender(), memberData.serviceType(),memberData.initials(),memberData.prevForceNumber(),
                        memberData.unit(),memberData.rank(), memberData.dateOfAttestation(),
                        memberData.membershipDate(),memberData.grossSalary(),memberData.netSalary(),memberData.tax()
                        ,memberData.address1(),memberData.address2(),memberData.address3(),memberData.address4(),
                        Status.SUCCESS,  format("Member with force number %s successfully added"
                                , memberData.forceNumber()),memberData.phoneNumber());

                Long unitId = allUnits.stream().filter(unitResponse ->
                                unitResponse.name().equalsIgnoreCase(memberData.unit()))
                        .findFirst().get().id();
                Long rankId = allRanks.stream().filter(rankResponse ->
                                rankResponse.name().equalsIgnoreCase(memberData.rank()))
                        .findFirst().get().id();

                log.info("Building a new member with response {}",memberResponse);
                var newMember = Member.builder()
                        .serviceType(memberResponse.serviceType())
                        .forceNumber(memberResponse.forceNumber())
                        .prevForceNumber(memberResponse.prevForceNumber())
                        .membershipNumber(generateMembershipNumber(memberResponse.serviceType()))
                        .rankID(rankId)
                        .unitID(unitId)
                        .membershipDate(memberResponse.membershipDate())
                        .dateOfAttestation(memberResponse.dateOfAttestation())
                        .grossSalary(memberResponse.grossSalary())
                        .netSalary(memberResponse.netSalary())
                        .tax(memberResponse.tax())
                        .personalDetails(PersonalDetails.builder()
                                .initials(memberResponse.initials())
                                .dOB(memberResponse.dOB())
                                .maritalStatus(memberResponse.maritalStatus())
                                .lastName(memberResponse.lastName())
                                .firstName(memberResponse.firstName())
                                .nationalId(memberResponse.nationalId())
                                .gender(memberResponse.gender())
                                .phoneNumber(memberResponse.phoneNumber())
                                .address(Address.builder()
                                        .addressLine_1(memberResponse.addressLine_1())
                                        .addressLine_2(memberResponse.addressLine_2())
                                        .addressLine_3(memberResponse.addressLine_3())
                                        .addressLine_4(memberResponse.addressLine_4())
                                        .build())
                                .build())
                        .memberStatus(IN_ACTIVE)
                        .audit(new Audit())
                        .build();

                memberList.add(newMember);
                memberUploadResponses.add(memberResponse);

            }

            log.info("Saving all retrieved members : {}" ,memberList);
            var savedMembers = memberRepository.saveAll(memberList);
            log.info("{} members saved to database", savedMembers.size());
        }
        catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return MemberBulkUploadResponse.builder()
                .uploads(memberUploadResponses)
                .succeededUploads(memberUploadResponses.stream().filter(response -> response.status()
                        == Status.SUCCESS).count())
                .failedUploads(memberUploadResponses.stream().filter(response -> response.status()
                        == Status.FAILED).count())
                .build();


    }

    private MemberBulkUploadResponse updateMembershipExcel(MultipartFile file) {
        log.info("Updating members from excel file : {}", file.getOriginalFilename());

        log.info("Retrieving ranks and units");
        var allUnits = getAllUnits();
        var allRanks = getAllRanks();

        List<MemberUploadResponse> memberUploadResponses = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean isHeaderRow = true;

            for (Row row : sheet) {
                log.info("Processing row : {}", row.getRowNum());
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue;
                }

                log.info("Skipping empty rows");
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                Cell forceNumberCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (forceNumberCell == null || forceNumberCell.getStringCellValue().trim().isEmpty()) {
                    log.warn("Skipping row {}: forceNumber is missing", row.getRowNum() + 1);
                    continue; // Skip rows without a forceNumber
                }

                String forceNumber = forceNumberCell.getStringCellValue().trim();
                var memberData = extractMemberDataFromRow(row);
                Optional<String> validationError = validateMemberDataUpdate(row, allUnits,allRanks);

                if (validationError.isPresent()) {
                    memberUploadResponses.add(buildMemberResponse(memberData.forceNumber(),memberData.firstName(),
                            memberData.lastName(),memberData.nationalID(),memberData.dob(),memberData.maritalStatus()
                            ,memberData.gender(), memberData.serviceType(),memberData.initials(),memberData.prevForceNumber(),
                            memberData.unit(),memberData.rank(), memberData.dateOfAttestation(),
                            memberData.membershipDate(),memberData.grossSalary(),memberData.netSalary(),memberData.tax()
                            ,memberData.address1(),memberData.address2(),memberData.address3(),memberData.address4(),
                            Status.FAILED, validationError.get(),memberData.phoneNumber()));

                    continue;
                }
                log.info("Retrieving member by given force number : {}",forceNumber);
                var existingMember = memberRepository.findMemberByForceNumber(forceNumber).get();

                var updatedMember = memberUpdateOption(existingMember,allUnits,allRanks,row);

                var memberResponse = buildMemberResponse(updatedMember.getForceNumber(),updatedMember.getPersonalDetails().getFirstName(),
                        updatedMember.getPersonalDetails().getLastName(),updatedMember.getPersonalDetails().getNationalId()
                        ,updatedMember.getPersonalDetails().getDOB(),updatedMember.getPersonalDetails().getMaritalStatus()
                        ,updatedMember.getPersonalDetails().getGender(), updatedMember.getServiceType(),
                        updatedMember.getPersonalDetails().getInitials(),updatedMember.getPrevForceNumber(),
                        row.getCell(10).getStringCellValue().trim(),
                        row.getCell(11).getStringCellValue().trim(), updatedMember.getDateOfAttestation(),
                        updatedMember.getMembershipDate(),updatedMember.getGrossSalary(),updatedMember.getNetSalary()
                        ,updatedMember.getTax(),updatedMember.getPersonalDetails().getAddress().getAddressLine_1()
                        ,updatedMember.getPersonalDetails().getAddress().getAddressLine_2(),
                        updatedMember.getPersonalDetails().getAddress().getAddressLine_3(),
                        updatedMember.getPersonalDetails().getAddress().getAddressLine_4(),
                        Status.SUCCESS,  format("Member with force number %s successfully updated"
                                , updatedMember.getForceNumber()),memberData.phoneNumber());

                memberList.add(updatedMember);
                memberUploadResponses.add(memberResponse);

            }

            log.info("Updating all retrieved members : {}" ,memberList);
            var savedMembers = memberRepository.saveAll(memberList);
            savedMembers.forEach(this::updateSubscriptionAccount);
            log.info("{} members updated", savedMembers.size());
        }
        catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return MemberBulkUploadResponse.builder()
                .uploads(memberUploadResponses)
                .succeededUploads(memberUploadResponses.stream().filter(response -> response.status()
                        == Status.SUCCESS).count())
                .failedUploads(memberUploadResponses.stream().filter(response -> response.status()
                        == Status.FAILED).count())
                .build();

    }

    private Specification<Member> getSpecification(MemberFilterRequest filterRequest) {
        if (isNull(filterRequest)) {
            return null;
        }
        MemberSpecificationBuilder builder = MemberSpecificationBuilder.create();
        Optional.ofNullable(filterRequest.getFirstName()).ifPresent(firstName ->
                builder.with(MemberSpecification.SearchCriteriaKey.FIRST_NAME.getValue(), firstName));
        Optional.ofNullable(filterRequest.getLastName()).ifPresent(lastName ->
                builder.with(MemberSpecification.SearchCriteriaKey.LAST_NAME.getValue(), lastName));
        Optional.ofNullable(filterRequest.getForceNumber()).ifPresent(forceNumber ->
                builder.with(MemberSpecification.SearchCriteriaKey.FORCE_NUMBER.getValue(), forceNumber));
        Optional.ofNullable(filterRequest.getMembershipNumber()).ifPresent(membershipNumber ->
                builder.with(MemberSpecification.SearchCriteriaKey.MEMBERSHIP_NUMBER.getValue(), membershipNumber));
        Optional.ofNullable(filterRequest.getMemberStatus()).ifPresent(memberStatus ->
                builder.with(MemberSpecification.SearchCriteriaKey.MEMBER_STATUS.getValue(), memberStatus));
        return builder.build();
    }

    private String generateMembershipNumber(ServiceType service) {
        int nextNumber = 1;
        StringBuilder prefix = new StringBuilder();
        switch (service) {
            case AIR_FORCE_OF_ZIMBABWE -> prefix.append("AFZ");
            case ZIMBABWE_NATIONAL_ARMY -> prefix.append("ZNA");
        }

        Optional<Member> member = memberRepository.findTopByOrderByIdDesc();
        if (member.isPresent()){
            String memberShipNumber = member.get().getMembershipNumber();
            String numberPart = memberShipNumber.substring(3, 8);
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        String formattedNumber = format("%05d", nextNumber);
        return prefix + formattedNumber;
    }

    private void handleMemberStatusUpdateLog(Member member, MemberStatusUpdateRequest request) {

        log.info("Saving status update log for Member with id : {}", member.getId());
        memberStatusRepository.save(MemberStatusLogs.builder()
                .comment(request.comment())
                .status(request.memberStatus())
                .memberId(member.getId())
                .build());
    }

    public Specification<MemberStatusLogs> getMemberStatusProperties(Long memberId, MemberStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (memberId != null) {
                predicates.add(criteriaBuilder.equal(root.get("memberId"), memberId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<UnitResponseDto> getAllUnits(){

        PageResponse<UnitResponseDto> unitResponse;
        try {
            unitResponse = parameterServiceClient.getAllUnits();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return unitResponse.getContent();
    }

    private List<RankResponseDto> getAllRanks(){
        PageResponse<RankResponseDto> rankResponse;
        try {
            rankResponse = parameterServiceClient.getAllRanks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rankResponse.getContent();
    }


    private MemberUploadResponse buildMemberResponse(String forceNumber, String firstName, String lastName, String nationalID,
                                                     LocalDate dob, MaritalStatus maritalStatus, Gender gender, ServiceType serviceType,
                                                     String initials, String prevForceNumber, String unit
            , String rank, LocalDate membershipDate, LocalDate dateOfAttestation, Double grossSalary, Double netSalary,
                                                     Double tax, String address1, String address2, String address3,
                                                     String address4, Status status,String reason,String phoneNumber) {

        log.info("Building Member response from excel file");
        return MemberUploadResponse.builder()
                .dateOfAttestation(dateOfAttestation)
                .membershipDate(membershipDate)
                .forceNumber(forceNumber)
                .status(status)
                .grossSalary(grossSalary)
                .tax(tax)
                .netSalary(netSalary)
                .unit(unit)
                .rank(rank)
                .prevForceNumber(prevForceNumber)
                .serviceType(serviceType)
                .dOB(dob)
                .firstName(firstName)
                .lastName(lastName)
                .initials(initials)
                .nationalId(nationalID)
                .gender(gender)
                .maritalStatus(maritalStatus)
                .phoneNumber(phoneNumber)
                .addressLine_1(address1)
                .addressLine_2(address2)
                .addressLine_3(address3)
                .addressLine_4(address4)
                .reason(reason)
                .build();
    }

    private MemberExcelUploadDTO extractMemberDataFromRow(Row row){
        log.info("Extracting member details on each row from Excel");
        return MemberExcelUploadDTO.builder()
                .forceNumber(excelHelper.getStringValue(row, 0))
                .firstName(excelHelper.getStringValue(row, 1))
                .lastName(excelHelper.getStringValue(row, 2))
                .nationalID(excelHelper.getStringValue(row, 3))
                .dob(excelHelper.getDateValue(row, 4))
                .maritalStatus(excelHelper.getEnumValue(row, 5, MaritalStatus.class))
                .gender(excelHelper.getEnumValue(row, 6, Gender.class))
                .serviceType(excelHelper.getEnumValue(row, 7, ServiceType.class))
                .initials(excelHelper.getStringValue(row, 8))
                .prevForceNumber(excelHelper.getStringValue(row, 9))
                .unit(excelHelper.getStringValue(row, 10))
                .rank(excelHelper.getStringValue(row, 11))
                .membershipDate(excelHelper.getDateValue(row, 12))
                .dateOfAttestation(excelHelper.getDateValue(row, 13))
                .grossSalary(excelHelper.getNumericValue(row, 14))
                .netSalary(excelHelper.getNumericValue(row, 15))
                .tax(excelHelper.getNumericValue(row, 16))
                .address1(excelHelper.getStringValue(row, 17))
                .address2(excelHelper.getStringValue(row, 18))
                .address3(excelHelper.getStringValue(row, 19))
                .address4(excelHelper.getStringValue(row, 20))
                .phoneNumber(excelHelper.getPhoneNumberValue(row, 21))
                .build();
    }

    private String formatPhoneNumber(String phoneNumber) {
        return new BigDecimal(phoneNumber).toPlainString();
    }

    private Optional<String> validateMemberData(Row row,MemberExcelUploadDTO memberData,
                                                List<UnitResponseDto> allUnits,
                                                List<RankResponseDto> allRanks) {

        if (memberData.forceNumber().isEmpty()) {
            return Optional.of(format("Force Number field is required. at row %s",row.getRowNum()));
        }

        log.info("Checking if member with force number : {} already exists", memberData.forceNumber());
        var existingMember = memberRepository.findMemberByForceNumber(memberData.forceNumber());
        if (existingMember.isPresent()) {
            return Optional.of(String.format("Member with force number %s at row : %s  already exists ",
                    memberData.forceNumber(),row.getRowNum()));
        }

        log.info("Checking if unit is valid");
        boolean isValidUnit = allUnits.stream().noneMatch(unitResponse -> unitResponse.name()
                .equals(memberData.unit()));
        if (isValidUnit) {
            return Optional.of(String.format("Invalid unit: %s at row : %s", memberData.unit(),row.getRowNum()));
        }

        log.info("Checking if rank is valid");
        boolean isValidRank = allRanks.stream().noneMatch(rankResponse -> rankResponse.name()
                .equals(memberData.rank()));
        if (isValidRank) {
            return Optional.of(String.format("Invalid rank: %s at row : %s", memberData.rank(),row.getRowNum()));
        }

        if (memberData.firstName() == null) {
            return Optional.of(format("First name field at row %s is required ",row.getRowNum()));
        }
        if (memberData.lastName() == null) {
            return Optional.of(format("Last name field at row %s is required",row.getRowNum()));
        }
        if (memberData.membershipDate() == null) {
            return Optional.of(format("Registration date field at row %s is required",row.getRowNum()));
        }
        if (memberData.dateOfAttestation() == null) {
            return Optional.of(format("Attestation date field at row %s is required",row.getRowNum()));
        }

        if (memberData.nationalID() == null) {
            return Optional.of(format("NationalID field at row %s is required",row.getRowNum()));
        }

        if (memberData.dob() == null) {
            return Optional.of(format("Date Of Birth field at row %s is required",row.getRowNum()));
        }
        if (memberData.maritalStatus() == null) {
            return Optional.of(format("Marital Status field at row %s is required",row.getRowNum()));
        }
        if (memberData.gender() == null) {
            return Optional.of(format("Gender field at row %s is required",row.getRowNum()));
        }
        if (memberData.serviceType() == null) {
            return Optional.of(format("Service field at row %s is required",row.getRowNum()));
        }

        if (!EnumUtils.isValidEnum(ServiceType.class, memberData.serviceType().name())) {
            return Optional.of(format("Invalid Service %s at row : %s", memberData.serviceType(),row.getRowNum()));
        }


        return Optional.empty();
    }

    private Optional<String> validateMemberDataUpdate(Row row,
                                                List<UnitResponseDto> allUnits,
                                                List<RankResponseDto> allRanks) {

        String forceNumber = getCellValue(row.getCell(0));
        if (forceNumber.isEmpty()) {
            return Optional.of(format("Force Number field is required for row %s", row.getRowNum()));
        }

        log.info("Checking if member with given force number: {} exists", forceNumber);
        if (memberRepository.findMemberByForceNumber(forceNumber).isEmpty()) {
            return Optional.of(String.format("Member at row %s  with force number %s does not exist.", row.getRowNum(), forceNumber));
        }

        String unitName = getCellValue(row.getCell(10));
        if (!unitName.isEmpty() && allUnits.stream().noneMatch(unit -> unit.name().equals(unitName))) {
            return Optional.of(String.format("Invalid unit: %s at row %s", unitName, row.getRowNum()));
        }

        String rankName = getCellValue(row.getCell(11));
        if (!rankName.isEmpty() && allRanks.stream().noneMatch(rank -> rank.name().equals(rankName))) {
            return Optional.of(String.format("Invalid rank: %s at row %s", rankName, row.getRowNum()));
        }
        return Optional.empty();
    }
    private Member memberUpdateOption(Member existingMember,List<UnitResponseDto> allUnits, List<RankResponseDto> allRanks
            ,Row row) {

        log.info("Updating member  {} with uploaded Excel document",existingMember);

        excelHelper.updateIfNotNull(row, 1, val -> existingMember.getPersonalDetails().setFirstName(val));
        excelHelper.updateIfNotNull(row, 2, val -> existingMember.getPersonalDetails().setLastName(val));
        excelHelper.updateIfNotNull(row, 3, val -> existingMember.getPersonalDetails().setNationalId(val));
        excelHelper.updateIfNotNullDate(row, 4, val -> existingMember.getPersonalDetails().setDOB(val));
        excelHelper.updateIfNotNullEnum(row, 5, MaritalStatus::valueOf, val -> existingMember.getPersonalDetails()
                .setMaritalStatus(val));
        excelHelper.updateIfNotNullEnum(row, 6, Gender::valueOf, val -> existingMember.getPersonalDetails()
                .setGender(val));
        excelHelper.updateIfNotNullEnum(row, 7, zw.co.afrosoft.zdf.member.ServiceType::valueOf,
                existingMember::setServiceType);
        excelHelper.updateIfNotNull(row, 8, val -> existingMember.getPersonalDetails().setInitials(val));
        excelHelper.updateIfNotNull(row, 9, existingMember::setPrevForceNumber);
        excelHelper.updateIfNotNull(row, 10, val -> existingMember.setUnitID(getUnitId(val, allUnits)));
        excelHelper.updateIfNotNull(row, 11, val -> existingMember.setRankID(getRankId(val, allRanks)));
        excelHelper.updateIfNotNullDate(row, 12, existingMember::setMembershipDate);
        excelHelper.updateIfNotNullDate(row, 13, existingMember::setDateOfAttestation);
        excelHelper.updateIfNotNullNumeric(row, 14, existingMember::setGrossSalary);
        excelHelper.updateIfNotNullNumeric(row, 15, existingMember::setNetSalary);
        excelHelper.updateIfNotNullNumeric(row, 16, existingMember::setTax);
        excelHelper.updateIfNotNull(row, 17, val -> existingMember.getPersonalDetails().getAddress()
                .setAddressLine_1(val));
        excelHelper.updateIfNotNull(row, 18, val -> existingMember.getPersonalDetails().getAddress()
                .setAddressLine_2(val));
        excelHelper.updateIfNotNull(row, 19, val -> existingMember.getPersonalDetails().getAddress()
                .setAddressLine_3(val));
        excelHelper.updateIfNotNull(row, 20, val -> existingMember.getPersonalDetails().getAddress()
                .setAddressLine_4(val));
        excelHelper.updateIfNotNull(row, 21, val -> existingMember.getPersonalDetails().
                setPhoneNumber(val));

        return existingMember;
    }

    private Long getUnitId(String unitName, List<UnitResponseDto> allUnits) {
        return allUnits.stream().filter(u -> u.name().equalsIgnoreCase(unitName)).findFirst()
                .map(UnitResponseDto::id).orElse(null);
    }

    private Long getRankId(String rankName, List<RankResponseDto> allRanks) {
        return allRanks.stream().filter(r -> r.name().equalsIgnoreCase(rankName))
                .findFirst().map(RankResponseDto::id).orElse(null);
    }

    /**
     * Helper method to safely retrieve cell values as strings.
     */
    private String getCellValue(Cell cell) {
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue().trim() : "";
    }

}
