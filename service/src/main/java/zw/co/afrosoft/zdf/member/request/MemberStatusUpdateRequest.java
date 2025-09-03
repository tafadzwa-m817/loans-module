package zw.co.afrosoft.zdf.member.request;

import zw.co.afrosoft.zdf.member.MemberStatus;


public record MemberStatusUpdateRequest(
        Long memberId,
        MemberStatus memberStatus,
        String comment
) {
}
