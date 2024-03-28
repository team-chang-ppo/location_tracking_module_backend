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
import org.changppo.cost_management_service.exception.ApiKeyNotFoundException;
import org.changppo.cost_management_service.exception.GradeNotFoundException;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.apikey.GradeRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
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

    @Transactional
    public ApiKeyDto createFreeKey(ApiKeyCreateRequest req) {
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Grade grade = gradeRepository.findByGradeType(GradeType.GRADE_FREE).orElseThrow(GradeNotFoundException::new);
        ApiKey apiKey = ApiKey.builder()
                .value(generateUniqueKeyValue())
                .grade(grade)
                .member(member)
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(), apiKey.getCreatedAt());
    }

    @Transactional
    public ApiKeyDto createClassicKey(ApiKeyCreateRequest req) {
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Grade grade = gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC).orElseThrow(GradeNotFoundException::new);
        ApiKey apiKey = ApiKey.builder()
                .value(generateUniqueKeyValue())
                .grade(grade)
                .member(member)
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(), apiKey.getCreatedAt());
    }

    private String generateUniqueKeyValue() {
        return UUID.randomUUID().toString();
    }

    @PreAuthorize("@apiKeyGuard.check(#id)")
    public ApiKeyDto read(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findByIdWithGrade(id).orElseThrow(ApiKeyNotFoundException::new);
        return new ApiKeyDto(apiKey.getId(), apiKey.getValue(), apiKey.getGrade().getGradeType(), apiKey.getCreatedAt());
    }

    public ApiKeyListDto readAll(ApiKeyReadAllRequest req){
        Slice<ApiKeyDto> slice = apiKeyRepository.findAllByMemberIdOrderByApiKeyIdDesc(req.getMemberId(), req.getLastApiKeyId(), Pageable.ofSize(req.getSize()));
        return new ApiKeyListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    @Transactional
    @PreAuthorize("@apiKeyGuard.check(#id)")
    public void delete(@Param("id")Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);
        apiKeyRepository.delete(apiKey);
    }
}
