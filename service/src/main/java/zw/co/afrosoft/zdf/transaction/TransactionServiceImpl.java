package zw.co.afrosoft.zdf.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.enums.PaymentType;

import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Page<Transactions> transactions(String forceNumber, List<PaymentType> paymentTypes,
                                           Long currencyId, Long referenceId, Pageable pageable) {

        if (log.isDebugEnabled()) {
            log.debug("Fetching transactions with params - ForceNumber: {}, PaymentTypes: {}, CurrencyId: {}, ReferenceId: {}",
                    forceNumber, paymentTypes, currencyId, referenceId);
        }

        Specification<Transactions> specification = TransactionHistorySpecification.getProperties(
                forceNumber, paymentTypes, currencyId, referenceId);

        return transactionRepository.findAll(specification, pageable);
    }
}
