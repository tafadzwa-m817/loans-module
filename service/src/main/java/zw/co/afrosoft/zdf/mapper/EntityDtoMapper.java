package zw.co.afrosoft.zdf.mapper;

import org.springframework.beans.BeanUtils;
import zw.co.afrosoft.zdf.claim.Claim;
import zw.co.afrosoft.zdf.dto.*;
import zw.co.afrosoft.zdf.entity.SecuritiesAccount;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.entity.Payments;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.loans.LoanAccount;
import zw.co.afrosoft.zdf.member.Member;
import zw.co.afrosoft.zdf.member.MemberResponseDto;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountDto;
import zw.co.afrosoft.zdf.transaction.Transactions;
import zw.co.afrosoft.zdf.transaction.dto.TransactionHistory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;



public class EntityDtoMapper {
    public static LoanResponseDto toLoanResponseDto(Loan loan){
        LoanResponseDto responseDto = new LoanResponseDto();
        copyProperties(loan, responseDto);
        return responseDto;
    }

    public static List<LoanResponseDto> toLoanResponseDtoList(List<Loan> loans){
        return loans.stream()
                .map(EntityDtoMapper::toLoanResponseDto)
                .toList();
    }
    public static PaymentsResponseDto toPaymentsResponseDto(Payments payments){
        PaymentsResponseDto responseDto = new PaymentsResponseDto();
        copyProperties(payments, responseDto);
        return responseDto;
    }
    public static List<PaymentsResponseDto> toPaymentsResponseDtoList(List<Payments> payments) {
        return payments.stream()
                .map(EntityDtoMapper::toPaymentsResponseDto)
                .toList();
    }
    public static AppResponseDto toAppResponseDto(Securities transaction){
        AppResponseDto responseDto = new AppResponseDto();
        copyProperties(transaction, responseDto);
        return responseDto;
    }
    public static List<AppResponseDto> toAppResponseDtoList(List<Securities> transactions){
        return transactions.stream()
                .map(EntityDtoMapper::toAppResponseDto)
                .toList();
    }
    public static MemberResponseDto toMemberResponseDto(Member member){
        MemberResponseDto responseDto = new MemberResponseDto();
        copyProperties(member, responseDto);
        return responseDto;
    }
    public static List<MemberResponseDto> toMemberResponseDtoList(List<Member> members){
        return members.stream()
                .map(EntityDtoMapper::toMemberResponseDto)
                .toList();
    }
    public static SubscriptionsAccountDto toSubscriptionsAccountDto(SubscriptionsAccount subscriptionsAccount){
        SubscriptionsAccountDto responseDto = new SubscriptionsAccountDto();
        copyProperties(subscriptionsAccount, responseDto);
        return responseDto;
    }
    public static List<SubscriptionsAccountDto> toSubscriptionsAccountDtoList(List<SubscriptionsAccount> accounts){
        return accounts.stream()
                .map(EntityDtoMapper::toSubscriptionsAccountDto)
                .toList();
    }
    public static TransactionHistory toTransactionHistory(Transactions transactions){
        TransactionHistory responseDto = new TransactionHistory();
        copyProperties(transactions, responseDto);
        return responseDto;
    }
    public static List<TransactionHistory> toTransactionHistoryList(List<Transactions> transactions){
        return transactions.stream()
                .map(EntityDtoMapper::toTransactionHistory)
                .toList();
    }
    public static LoanAccountDto toLoanAccountDto(LoanAccount loanAccount){
        LoanAccountDto responseDto = new LoanAccountDto();
        copyProperties(loanAccount, responseDto);
        return responseDto;
    }
    public static List<LoanAccountDto> toLoanAccountDtoList(List<LoanAccount> loanAccounts){
        return loanAccounts.stream()
                .map(EntityDtoMapper::toLoanAccountDto)
                .toList();
    }
    public static SecuritiesAccountDto toSecuritiesAccountDto(SecuritiesAccount securitiesAccount){
        SecuritiesAccountDto responseDto = new SecuritiesAccountDto();
        copyProperties(securitiesAccount, responseDto);
        return responseDto;
    }
    public static List<SecuritiesAccountDto> toSecuritiesAccountDtoList(List<SecuritiesAccount> securitiesAccounts){
        return securitiesAccounts.stream()
                .map(EntityDtoMapper::toSecuritiesAccountDto)
                .toList();
    }
    public static ClaimDto toClaimDto(Claim claim){
        ClaimDto responseDto = new ClaimDto();
        copyProperties(claim, responseDto);
        return responseDto;
    }
    public static List<ClaimDto> toClaimDtoList(List<Claim> claims){
        return claims.stream()
                .map(EntityDtoMapper::toClaimDto)
                .toList();
    }
}
