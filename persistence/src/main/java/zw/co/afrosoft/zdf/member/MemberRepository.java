package zw.co.afrosoft.zdf.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zw.co.afrosoft.zdf.loans.Loan;

import java.util.Optional;



public interface MemberRepository extends JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member> {

    Optional<Member> findTopByOrderByIdDesc();
    Optional<Member> findMemberByForceNumber(String forceNumber);
    Optional<Member> findByForceNumberAndMemberStatus(String forceNumber, MemberStatus memberStatus);
}
