package org.changppo.account.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.account.apikey.ApiKey;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.type.RoleType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAccessEvaluator extends Evaluator {

    private final ApiKeyRepository apiKeyRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isEligible(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        return apiKey.getMember().getId().equals(PrincipalHandler.extractId());
    }
}