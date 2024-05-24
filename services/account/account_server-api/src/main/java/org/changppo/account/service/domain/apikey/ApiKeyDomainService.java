package org.changppo.account.service.domain.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class ApiKeyDomainService {

    private final ApiKeyRepository apiKeyRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void requestApiKeyDeletion(Long memberId) {
        apiKeyRepository.requestApiKeyDeletion(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelApiKeyDeletionRequest(Long memberId) {
        apiKeyRepository.cancelApiKeyDeletionRequest(memberId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banApiKeysByAdmin(Long memberId) {
        apiKeyRepository.banApiKeysByAdmin(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanApiKeysByAdmin(Long memberId) {
        apiKeyRepository.unbanApiKeysByAdmin(memberId);
    }
}
