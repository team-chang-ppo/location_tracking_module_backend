package org.changppo.cost_management_service.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.response.exception.apikey.ApiKeyNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
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