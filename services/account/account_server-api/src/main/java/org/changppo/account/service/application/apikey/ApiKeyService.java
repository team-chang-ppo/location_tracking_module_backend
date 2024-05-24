package org.changppo.account.service.application.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.entity.member.Member;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.type.GradeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ApiKeyService {

    private final ApiKeyDomainService apiKeyDomainService;
    private final MemberDomainService memberDomainService;

    @Transactional
    @PreAuthorize("@memberNotPaymentFailureStatusEvaluator.check(#req.memberId)")
    public ApiKeyDto createFreeKey(@Param("req") ApiKeyCreateRequest req) {
        Member member = memberDomainService.getMember(req.getMemberId());
        return apiKeyDomainService.createKey(member, GradeType.GRADE_FREE);
    }

    @Transactional
    @PreAuthorize("@memberNotPaymentFailureStatusEvaluator.check(#req.memberId)")
    public ApiKeyDto createClassicKey(@Param("req") ApiKeyCreateRequest req) {
        Member member = memberDomainService.getMember(req.getMemberId());
        return apiKeyDomainService.createKey(member, GradeType.GRADE_CLASSIC);
    }

    @PreAuthorize("@apiKeyAccessEvaluator.check(#id)")
    public ApiKeyDto read(@Param("id")Long id) {
        return apiKeyDomainService.getApiKeyDto(id);
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public ApiKeyListDto readAll(@Param("memberId")Long memberId, ApiKeyReadAllRequest req){
        return apiKeyDomainService.getApiKeyList(memberId, req);
    }

    public Page<ApiKeyDto> readList(Pageable pageable) {
        return apiKeyDomainService.getApiKeyDtos(pageable);
    }

    @Transactional
    @PreAuthorize("@apiKeyAccessEvaluator.check(#id) and @apiKeyNotPaymentFailureStatusEvaluator.check(#id)")
    public void delete(@Param("id")Long id) {
        apiKeyDomainService.deleteApiKey(id);
    }

    public ApiKeyValidationResponse validate(Long id) {
        return apiKeyDomainService.validateApiKey(id);
    }
}
