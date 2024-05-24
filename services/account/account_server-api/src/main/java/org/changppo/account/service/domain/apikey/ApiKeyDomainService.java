package org.changppo.account.service.domain.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.utils.jwt.apikey.ApiKeyJwtClaims;
import org.changppo.utils.jwt.apikey.ApiKeyJwtHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class ApiKeyDomainService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyJwtHandler apiKeyJwtHandler;

    @Transactional(propagation = Propagation.MANDATORY)
    public ApiKeyDto createKey(Member member, Grade grade) {
        ApiKey apiKey = ApiKey.builder()
                .value(generateTemporaryValue())
                .grade(grade)
                .member(member)
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        apiKey.updateValue(generateTokenValue(apiKey));
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(),
                apiKey.getPaymentFailureBannedAt(), apiKey.getCardDeletionBannedAt(), apiKey.getCreatedAt());
    }

    private String generateTemporaryValue() {
        return UUID.randomUUID().toString();
    }

    private String generateTokenValue(ApiKey apiKey) {
        return apiKeyJwtHandler.createToken(new ApiKeyJwtClaims(apiKey.getId(), apiKey.getMember().getId(), apiKey.getGrade().getGradeType().name()));
    }

    public ApiKeyDto getApiKeyDto(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(),
                apiKey.getPaymentFailureBannedAt(), apiKey.getCardDeletionBannedAt(), apiKey.getCreatedAt());
    }

    public ApiKeyListDto getApiKeyList(Long memberId, Long firstApiKeyId, Integer size) {
        Slice<ApiKeyDto> slice = apiKeyRepository.findAllDtosByMemberIdOrderByAsc(memberId, firstApiKeyId, Pageable.ofSize(size));
        return new ApiKeyListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    public Page<ApiKeyDto> getApiKeyDtos(Pageable pageable) {
        return apiKeyRepository.findAllDtos(pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        apiKeyRepository.delete(apiKey);
    }

    public ApiKeyValidationResponse validateApiKey(Long id) {
        return new ApiKeyValidationResponse(apiKeyRepository.isValid(id));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banApiKeysForPaymentFailure(Long memberId) {
        apiKeyRepository.banApiKeysForPaymentFailureByMemberId(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanApiKeysForPaymentFailure(Long memberId) {
        apiKeyRepository.unbanApiKeysForPaymentFailureByMemberId(memberId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banForCardDeletion(Long memberId) {
        apiKeyRepository.banForCardDeletionByMemberId(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanForCardDeletion(Long memberId) {
        apiKeyRepository.unbanForCardDeletionByMemberId(memberId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void requestApiKeyDeletion(Long memberId) {
        apiKeyRepository.requestApiKeyDeletionByMemberId(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelApiKeyDeletionRequest(Long memberId) {
        apiKeyRepository.cancelApiKeyDeletionRequestByMemberId(memberId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banApiKeysByAdmin(Long memberId) {
        apiKeyRepository.banApiKeysByAdminByMemberId(memberId, LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanApiKeysByAdmin(Long memberId) {
        apiKeyRepository.unbanApiKeysByAdminByMemberId(memberId);
    }
}
