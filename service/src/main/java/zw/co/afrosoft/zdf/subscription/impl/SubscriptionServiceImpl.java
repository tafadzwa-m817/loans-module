package zw.co.afrosoft.zdf.subscription.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.member.ServiceType;
import zw.co.afrosoft.zdf.subscription.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Long;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;


@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionsAccountRepository subscriptionsRepository;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;

    @Override
    public SubscriptionsAccount addCurrency(Long id, Long currencyID) {
        var subscription = subscriptionsRepository.findById(id).orElseThrow(
                () -> new RecordNotFoundException(format("Subscription account with ID %s not found", id))
        );
        subscription.setCurrencyId(currencyID);
        return subscriptionsRepository.save(subscription);
    }

    @Override
    public List<SubscriptionsAccount> updateMemberDetailsFromFile(MultipartFile file, List<String> forceNumbers) {
        List<MemberDetailsRequest> memberDetailsRequestList;

        try (InputStream inputStream = file.getInputStream()) {
            if (requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
                memberDetailsRequestList = parseCSV(inputStream);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                memberDetailsRequestList = parseExcel(inputStream);
            } else {
                throw new RuntimeException("Unsupported file format");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }

        List<SubscriptionsAccount> updatedSubAccounts = new ArrayList<>();

        log.info("Adding member details to subscription account from file");
        IntStream.range(0, forceNumbers.size()).forEach(i -> {
            String forceNumber = forceNumbers.get(i);
            var memberDetailsRequest = memberDetailsRequestList.get(i);
            subscriptionsRepository.findByForceNumber(forceNumber).ifPresent(subAccount -> {
                if (memberDetailsRequest.getServiceType() != null)
                    subAccount.setServiceType(memberDetailsRequest.getServiceType());
                if (memberDetailsRequest.getName() != null)
                    subAccount.setName(memberDetailsRequest.getName());
                if (memberDetailsRequest.getSurname() != null)
                    subAccount.setSurname(memberDetailsRequest.getSurname());

                subscriptionsRepository.save(subAccount);
                updatedSubAccounts.add(subAccount);
            });
        });
        return updatedSubAccounts;
    }

    @Override
    public Page<SubscriptionsAccount> getAllSubscriptions(String forceNumber,
                                                          String accountNumber,
                                                          Long currencyId,
                                                          Pageable pageable) {
        return subscriptionsRepository.findAllByOrderByIdDesc(forceNumber, accountNumber, currencyId,pageable);
    }

    @Override
    public SubscriptionsAccountDto getSubscriptionById(Long id) {
        log.info("Retrieving subscription account with id : {}", id);
        var subscription = subscriptionsRepository.findById(id).orElseThrow(
                () -> new RecordNotFoundException(format("Subscription account with ID %s not found", id))
        );

//        BigDecimal currentBalance = transactionRepository.findAllByForceNumberAndCurrencyAndPaymentType(
//                        subscription.getForceNumber(), currency, PaymentType.SUBSCRIPTION)
//                .stream()
//                .map(Transactions::getAmount) // Extract amount
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SubscriptionsAccountDto(
                subscription.getId(),
                subscription.getAccountNumber(),
                subscription.getName(),
                subscription.getSurname(),
                subscription.getForceNumber(),
                subscription.getMembershipDate(),
                subscription.getServiceType(),
                subscription.getRank(),
                subscription.getCurrencyId(),
                subscription.getCurrentBalance(),
                subscription.getCurrentArrears(),
                subscription.getStartDate(),
                subscription.getAccountStatus(),
                subscription.getBalanceBForward(),
                subscription.getInterestToDate(),
                subscription.getAudit()
        );
    }

    @Override
    public List<SubscriptionsAccount> applyInterestToSubscriptions(float interest) {
        List<SubscriptionsAccount> updatedSubAccounts = new ArrayList<>();
        subscriptionsAccountRepository.findAll().forEach(subscriptionsAccount -> {
            var interestValue = subscriptionsAccount.getCurrentBalance().multiply(new BigDecimal(interest));
            subscriptionsAccount.setCurrentBalance(subscriptionsAccount.getCurrentBalance().add(interestValue));
            subscriptionsRepository.save(subscriptionsAccount);
            updatedSubAccounts.add(subscriptionsAccount);
        });
        return updatedSubAccounts;
    }

    private List<MemberDetailsRequest> parseExcel(InputStream inputStream) throws IOException {
        List<MemberDetailsRequest> list = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            MemberDetailsRequest details = new MemberDetailsRequest();
            details.setName(valueOf(row.getCell(0)));
            details.setSurname(valueOf(row.getCell(1)));
            details.setServiceType(ServiceType.valueOf(String.valueOf(row.getCell(2))));
            details.setAccountStatus(AccountStatus.valueOf(valueOf(row.getCell(3))));
            list.add(details);
        }
        return list;
    }
    private List<MemberDetailsRequest> parseCSV(InputStream inputStream) throws IOException {
        List<MemberDetailsRequest> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            MemberDetailsRequest details = new MemberDetailsRequest();
            details.setName(data[0]);
            details.setSurname(data[1]);
            details.setServiceType(ServiceType.valueOf(data[2]));
            details.setAccountStatus(AccountStatus.valueOf(data[3]));
            list.add(details);
        }
        return list;
    }
}
