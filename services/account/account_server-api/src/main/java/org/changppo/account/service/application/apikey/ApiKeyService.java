package org.changppo.account.service.application.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.apikey.GradeRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.apikey.GradeNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.type.GradeType;
import org.changppo.utils.jwt.apikey.ApiKeyJwtClaims;
import org.changppo.utils.jwt.apikey.ApiKeyJwtHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final GradeRepository gradeRepository;
    private final MemberRepository memberRepository;
    private final ApiKeyJwtHandler apiKeyJwtHandler;

    @Transactional
    @PreAuthorize("@memberNotPaymentFailureStatusEvaluator.check(#req.memberId)")
    public ApiKeyDto createFreeKey(@Param("req") ApiKeyCreateRequest req) {
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Grade grade = gradeRepository.findByGradeType(GradeType.GRADE_FREE).orElseThrow(GradeNotFoundException::new);
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

    @Transactional
    @PreAuthorize("@memberNotPaymentFailureStatusEvaluator.check(#req.memberId)")
    public ApiKeyDto createClassicKey(@Param("req") ApiKeyCreateRequest req) {
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Grade grade = gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC).orElseThrow(GradeNotFoundException::new);
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

    @PreAuthorize("@apiKeyAccessEvaluator.check(#id)")
    public ApiKeyDto read(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(),
                apiKey.getPaymentFailureBannedAt(), apiKey.getCardDeletionBannedAt(), apiKey.getCreatedAt());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public ApiKeyListDto readAll(@Param("memberId")Long memberId, ApiKeyReadAllRequest req){
        Slice<ApiKeyDto> slice = apiKeyRepository.findAllDtosByMemberIdOrderByAsc(memberId, req.getFirstApiKeyId(), Pageable.ofSize(req.getSize()));
        return new ApiKeyListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    public Page<ApiKeyDto> readList(Pageable pageable) {
        return apiKeyRepository.findAllDtos(pageable);
    }

    @Transactional
    @PreAuthorize("@apiKeyAccessEvaluator.check(#id) and @apiKeyNotPaymentFailureStatusEvaluator.check(#id)")
    public void delete(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        apiKeyRepository.delete(apiKey);
    }

    public ApiKeyValidationResponse validate(Long id) {
        return new ApiKeyValidationResponse(apiKeyRepository.isValid(id));
    }
}
