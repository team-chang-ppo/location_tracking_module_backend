package org.changppo.cost_management_service.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.exception.apikey.ApiKeyNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyStatusEvaluator extends Evaluator {

    private final ApiKeyRepository apiKeyRepository;

    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    public boolean isEligible(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        return !apiKey.isBanned();
    }
}