package org.changppo.cost_management_service.security.guard;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.security.PrincipalHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyGuard extends Guard{

    private final ApiKeyRepository apiKeyRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return apiKeyRepository.findById(id)
                .map(ApiKey::getMember)
                .map(Member::getId)
                .filter(memberId -> memberId.equals(PrincipalHandler.extractId()))
                .isPresent();
    }
}