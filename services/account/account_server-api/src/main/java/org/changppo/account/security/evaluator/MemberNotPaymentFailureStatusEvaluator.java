package org.changppo.account.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.type.RoleType;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.security.PrincipalHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberNotPaymentFailureStatusEvaluator extends Evaluator {

    private final MemberRepository memberRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    public boolean isEligible(Long id) {
        id = id != null ? id : PrincipalHandler.extractId();
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return !member.isPaymentFailureBanned();
    }
}
