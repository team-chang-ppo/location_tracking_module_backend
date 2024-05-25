package org.changppo.account.service.domain.apikey;

import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.type.GradeType;
import org.changppo.account.type.RoleType;
import org.changppo.utils.jwt.apikey.ApiKeyJwtClaims;
import org.changppo.utils.jwt.apikey.ApiKeyJwtHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.apikey.ApiKeyBuilder.buildApiKey;
import static org.changppo.account.builder.apikey.ApiKeyDtoBuilder.buildApiKeyDto;
import static org.changppo.account.builder.apikey.GradeBuilder.buildGrade;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApiKeyDomainServiceTest {

    @InjectMocks
    private ApiKeyDomainService apiKeyDomainService;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ApiKeyJwtHandler apiKeyJwtHandler;

    Member member;
    Role role;
    Grade grade;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
        grade = buildGrade(GradeType.GRADE_CLASSIC);
    }

    @Test
    void createKeyTest() {
        // given
        ApiKey apiKey = buildApiKey(grade, member);
        String tokenValue =  UUID.randomUUID().toString();
        given(apiKeyRepository.save(any(ApiKey.class))).willReturn(apiKey);
        given(apiKeyJwtHandler.createToken(any(ApiKeyJwtClaims.class))).willReturn(tokenValue);

        // when
        ApiKey createdApiKey = apiKeyDomainService.createKey(member, grade);

        // then
        assertThat(createdApiKey).isEqualTo(apiKey);
    }

    @Test
    void getApiKeyTest() {
        // given
        ApiKey apiKey = buildApiKey(grade, member);
        given(apiKeyRepository.findById(anyLong())).willReturn(Optional.of(apiKey));

        // when
        ApiKey result = apiKeyDomainService.getApiKey(1L);

        // then
        assertThat(result).isEqualTo(apiKey);
    }

    @Test
    void getApiKeyExceptionTest() {
        // given
        given(apiKeyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> apiKeyDomainService.getApiKey(1L)).isInstanceOf(ApiKeyNotFoundException.class);
    }

    @Test
    void getApiKeyDtoListTest() {
        // given
        Pageable pageable = buildPage();
        List<ApiKeyDto> apiKeyDtos = List.of(buildApiKeyDto(grade), buildApiKeyDto(grade));
        Slice<ApiKeyDto> slice = new SliceImpl<>(apiKeyDtos, pageable, false);
        given(apiKeyRepository.findAllDtosByMemberIdOrderByAsc(anyLong(), anyLong(), any(PageRequest.class))).willReturn(slice);

        // when
        ApiKeyListDto result = apiKeyDomainService.getApiKeyDtoList(1L, 1L, 1);

        // then
        assertThat(result.getApiKeyList()).isEqualTo(apiKeyDtos);
    }

    @Test
    void getApiKeyDtoPageTest() {
        // given
        Pageable pageable = buildPage();
        List<ApiKeyDto> apiKeyDtos = List.of(buildApiKeyDto(grade), buildApiKeyDto(grade));
        Page<ApiKeyDto> apiKeyDtoPage = new PageImpl<>(apiKeyDtos, pageable, 1);
        given(apiKeyRepository.findAllDtos(any(PageRequest.class))).willReturn(apiKeyDtoPage);

        // when
        Page<ApiKeyDto> result = apiKeyDomainService.getApiKeyDtoPage(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(apiKeyDtos);
    }

    @Test
    void deleteApiKeyTest() {
        // given
        ApiKey apiKey = buildApiKey(grade, member);
        given(apiKeyRepository.findById(anyLong())).willReturn(Optional.of(apiKey));
        doNothing().when(apiKeyRepository).delete(any(ApiKey.class));

        // when
        apiKeyDomainService.deleteApiKey(1L);

        // then
        verify(apiKeyRepository).delete(apiKey);
    }

    @Test
    void deleteApiKeyExceptionTest() {
        // given
        given(apiKeyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> apiKeyDomainService.deleteApiKey(1L)).isInstanceOf(ApiKeyNotFoundException.class);
    }

    @Test
    void validateApiKeyTest() {
        // given
        given(apiKeyRepository.isValid(anyLong())).willReturn(true);

        // when
        ApiKeyValidationResponse result = apiKeyDomainService.validateApiKey(1L);

        // then
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void banApiKeysForPaymentFailureTest() {
        // given
        doNothing().when(apiKeyRepository).banApiKeysForPaymentFailureByMemberId(anyLong(), any(LocalDateTime.class));

        // when
        apiKeyDomainService.banApiKeysForPaymentFailure(1L);

        // then
        verify(apiKeyRepository).banApiKeysForPaymentFailureByMemberId(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void unbanApiKeysForPaymentFailureTest() {
        // given
        doNothing().when(apiKeyRepository).unbanApiKeysForPaymentFailureByMemberId(anyLong());

        // when
        apiKeyDomainService.unbanApiKeysForPaymentFailure(1L);

        // then
        verify(apiKeyRepository).unbanApiKeysForPaymentFailureByMemberId(1L);
    }

    @Test
    void banForCardDeletionTest() {
        // given
        doNothing().when(apiKeyRepository).banForCardDeletionByMemberId(anyLong(), any(LocalDateTime.class));

        // when
        apiKeyDomainService.banForCardDeletion(1L);

        // then
        verify(apiKeyRepository).banForCardDeletionByMemberId(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void unbanForCardDeletionTest() {
        // given
        doNothing().when(apiKeyRepository).unbanForCardDeletionByMemberId(anyLong());

        // when
        apiKeyDomainService.unbanForCardDeletion(1L);

        // then
        verify(apiKeyRepository).unbanForCardDeletionByMemberId(1L);
    }

    @Test
    void requestApiKeyDeletionTest() {
        // given
        doNothing().when(apiKeyRepository).requestApiKeyDeletionByMemberId(anyLong(), any(LocalDateTime.class));

        // when
        apiKeyDomainService.requestApiKeyDeletion(1L);

        // then
        verify(apiKeyRepository).requestApiKeyDeletionByMemberId(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void cancelApiKeyDeletionRequestTest() {
        // given
        doNothing().when(apiKeyRepository).cancelApiKeyDeletionRequestByMemberId(anyLong());

        // when
        apiKeyDomainService.cancelApiKeyDeletionRequest(1L);

        // then
        verify(apiKeyRepository).cancelApiKeyDeletionRequestByMemberId(1L);
    }

    @Test
    void banApiKeysByAdminTest() {
        // given
        doNothing().when(apiKeyRepository).banApiKeysByAdminByMemberId(anyLong(), any(LocalDateTime.class));

        // when
        apiKeyDomainService.banApiKeysByAdmin(1L);

        // then
        verify(apiKeyRepository).banApiKeysByAdminByMemberId(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void unbanApiKeysByAdminTest() {
        // given
        doNothing().when(apiKeyRepository).unbanApiKeysByAdminByMemberId(anyLong());

        // when
        apiKeyDomainService.unbanApiKeysByAdmin(1L);

        // then
        verify(apiKeyRepository).unbanApiKeysByAdminByMemberId(1L);
    }
}
