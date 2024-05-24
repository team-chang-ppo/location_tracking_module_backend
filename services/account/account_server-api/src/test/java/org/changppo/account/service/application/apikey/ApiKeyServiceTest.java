package org.changppo.account.service.application.apikey;

import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.apikey.GradeDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.type.GradeType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.changppo.account.builder.apikey.ApiKeyBuilder.buildApiKey;
import static org.changppo.account.builder.apikey.ApiKeyDtoBuilder.buildApiKeyDto;
import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyCreateRequest;
import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyReadAllRequest;
import static org.changppo.account.builder.apikey.ApiKeyResponseBuilder.buildApiKeyValidationResponse;
import static org.changppo.account.builder.apikey.GradeBuilder.buildGrade;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceTest {

    @InjectMocks
    private ApiKeyService apiKeyService;
    @Mock
    private ApiKeyDomainService apiKeyDomainService;
    @Mock
    private GradeDomainService gradeDomainService;
    @Mock
    private MemberDomainService memberDomainService;

    Role role;
    Member member;
    Grade freeGrade;
    Grade classicGrade;

    @BeforeEach
    void setUp() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
        freeGrade = buildGrade(GradeType.GRADE_FREE);
        classicGrade = buildGrade(GradeType.GRADE_CLASSIC);
    }

    @Test
    void createFreeKeyTest() {
        // given
        ApiKey apiKey = buildApiKey(freeGrade, member);
        ApiKeyCreateRequest request = buildApiKeyCreateRequest(member.getId());
        given(memberDomainService.getMember(member.getId())).willReturn(member);
        given(gradeDomainService.getGradeByType(GradeType.GRADE_FREE)).willReturn(freeGrade);
        given(apiKeyDomainService.createKey(member, freeGrade)).willReturn(apiKey);

        // when
        ApiKeyDto result = apiKeyService.createFreeKey(request);

        // then
        assertThat(result.getValue()).isEqualTo(apiKey.getValue());
    }

    @Test
    void createClassicKeyTest() {
        // given
        ApiKey apiKey = buildApiKey(classicGrade, member);
        ApiKeyCreateRequest request = buildApiKeyCreateRequest(member.getId());
        given(memberDomainService.getMember(member.getId())).willReturn(member);
        given(gradeDomainService.getGradeByType(GradeType.GRADE_CLASSIC)).willReturn(classicGrade);
        given(apiKeyDomainService.createKey(member, classicGrade)).willReturn(apiKey);

        // when
        ApiKeyDto result = apiKeyService.createClassicKey(request);

        // then
        assertThat(result.getValue()).isEqualTo(apiKey.getValue());
    }

    @Test
    void readTest() {
        // given
        ApiKey apiKey = buildApiKey(freeGrade, member);
        given(apiKeyDomainService.getApiKey(anyLong())).willReturn(apiKey);

        // when
        ApiKeyDto result = apiKeyService.read(1L);

        // then
        assertThat(result.getValue()).isEqualTo(apiKey.getValue());
    }

    @Test
    void readAllTest() {
        // given
        ApiKeyReadAllRequest request = buildApiKeyReadAllRequest(1L, 10);
        List<ApiKeyDto> apiKeyDtos = List.of(buildApiKeyDto(freeGrade), buildApiKeyDto(classicGrade));
        ApiKeyListDto apiKeyListDto = new ApiKeyListDto(apiKeyDtos.size(), true, apiKeyDtos);
        given(apiKeyDomainService.getApiKeyDtoList(member.getId(), request.getFirstApiKeyId(), request.getSize())).willReturn(apiKeyListDto);

        // when
        ApiKeyListDto result = apiKeyService.readAll(member.getId(), request);

        // then
        assertThat(result.getApiKeyList()).isEqualTo(apiKeyDtos);
    }

    @Test
    void readListTest() {
        // given
        Pageable pageable = buildPage();
        List<ApiKeyDto> apiKeyDtos = List.of(buildApiKeyDto(freeGrade), buildApiKeyDto(classicGrade));
        Page<ApiKeyDto> page = new PageImpl<>(apiKeyDtos, pageable, apiKeyDtos.size());
        given(apiKeyDomainService.getApiKeyDtoPage(pageable)).willReturn(page);

        // when
        Page<ApiKeyDto> result = apiKeyService.readList(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(apiKeyDtos);
    }

    @Test
    void deleteTest() {
        // given
        ApiKey apiKey = buildApiKey(freeGrade, member);
        doNothing().when(apiKeyDomainService).deleteApiKey(apiKey.getId());

        // when
        apiKeyService.delete(apiKey.getId());

        // then
        verify(apiKeyDomainService).deleteApiKey(apiKey.getId());
    }

    @Test
    void validateTest() {
        // given
        ApiKey apiKey = buildApiKey(freeGrade, member);
        ApiKeyValidationResponse response = buildApiKeyValidationResponse(true);
        given(apiKeyDomainService.validateApiKey(apiKey.getId())).willReturn(response);

        // when
        ApiKeyValidationResponse result = apiKeyService.validate(apiKey.getId());

        // then
        assertThat(result.isValid()).isEqualTo(response.isValid());
    }
}
