package zw.co.afrosoft.zdf.payment;

import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.enums.PaymentType;
import zw.co.afrosoft.zdf.subscription.dto.PaymentBulkUploadResponse;
import zw.co.afrosoft.zdf.transaction.Transactions;

import java.time.LocalDate;



public interface PaymentsService {

    /**
     * Processes a single payment transaction.
     *
     * @param paymentType       the type of payment to be processed
     * @param paymentRequest    the details of the payment request
     * @param transactionTypeId the ID representing the transaction type
     * @param date              the date of the transaction
     * @return the created {@link Transactions} entity representing the payment
     */
    Transactions makePayment(PaymentType paymentType, PaymentRequest paymentRequest,
                             Long transactionTypeId, LocalDate date);

    /**
     * Handles bulk upload of payment transactions from an Excel file.
     *
     * @param file              the Excel file containing payment data
     * @param transactionTypeId  the ID representing the transaction type
     * @param transactionDate   the date for all transactions in the file
     * @param paymentType       the payment type for the uploaded payments
     * @param currencyId        the currency ID applicable to the payments
     * @return a {@link PaymentBulkUploadResponse} summarizing the results of the bulk upload,
     *         including counts of successful and failed uploads
     */
    PaymentBulkUploadResponse uploadPaymentExcel(MultipartFile file, Long transactionTypeId,
                                                 LocalDate transactionDate,
                                                 PaymentType paymentType, Long currencyId);

    /**
     * Reverses a previously made payment transaction based on the specified reversal action and context.
     *
     * @param paymentType           the type of payment to be reversed (e.g., subscription, loan, etc.)
     * @param paymentReversalRequest the request object containing reversal details such as reason or original transaction reference
     * @param transactionTypeId     the ID of the transaction type to reverse
     * @param date                  the date on which the reversal should be applied
     * @param reversalAction        the action to be taken for the reversal (e.g., full reversal, partial reversal)
     * @return a {@link Transactions} object representing the reversed transaction
     */
    Transactions paymentReversal(PaymentType paymentType,
                                 PaymentReversalRequest paymentReversalRequest,
                                 Long transactionTypeId,
                                 LocalDate date,
                                 ReversalAction reversalAction);
}

