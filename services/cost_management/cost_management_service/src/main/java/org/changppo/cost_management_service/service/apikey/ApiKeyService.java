package org.changppo.cost_management_service.service.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.apikey.ApiKeyCreateRequest;
import org.changppo.cost_management_service.dto.apikey.ApiKeyDto;
import org.changppo.cost_management_service.dto.apikey.ApiKeyListDto;
import org.changppo.cost_management_service.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.apikey.Grade;
import org.changppo.cost_management_service.entity.apikey.GradeType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.cost_management_service.response.exception.apikey.GradeNotFoundException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.apikey.GradeRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.service.apikey.token.JwtHandler;
import org.changppo.cost_management_service.service.apikey.token.TokenClaims;
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
    private final JwtHandler jwtHandler;

    @Transactional
    @PreAuthorize("@memberPaymentFailureStatusEvaluator.check(#req.memberId)")
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
    @PreAuthorize("@memberPaymentFailureStatusEvaluator.check(#req.memberId)")
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
        return jwtHandler.createToken(new TokenClaims(apiKey.getId(), apiKey.getMember().getId(), apiKey.getGrade().getGradeType().name()));
    }

    @PreAuthorize("@apiKeyAccessEvaluator.check(#id)")
    public ApiKeyDto read(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(),
                apiKey.getPaymentFailureBannedAt(), apiKey.getCardDeletionBannedAt(), apiKey.getCreatedAt());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public ApiKeyListDto readAll(@Param("memberId")Long memberId, ApiKeyReadAllRequest req){
        Slice<ApiKeyDto> slice = apiKeyRepository.findAllByMemberIdOrderByAsc(memberId, req.getFirstApiKeyId(), Pageable.ofSize(req.getSize()));
        return new ApiKeyListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    @Transactional
    @PreAuthorize("@apiKeyAccessEvaluator.check(#id) and @apiKeyPaymentFailureStatusEvaluator.check(#id) and @memberPaymentFailureStatusEvaluator.check(null)")
    public void delete(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        apiKeyRepository.delete(apiKey);
    }
}
