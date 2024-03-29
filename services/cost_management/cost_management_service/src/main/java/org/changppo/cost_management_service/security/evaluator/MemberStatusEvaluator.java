package org.changppo.cost_management_service.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.security.PrincipalHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStatusEvaluator extends Evaluator {

    private final MemberRepository memberRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    public boolean isEligible(Long id) {
        id = id != 0 ? id : PrincipalHandler.extractId();
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return member.getBannedAt() == null;
    }
}