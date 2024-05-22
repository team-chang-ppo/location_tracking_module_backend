package org.changppo.account.repository.apikey;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.changppo.account.builder.apikey.ApiKeyBuilder;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.apikey.ApiKeyBuilder.buildApiKey;
import static org.changppo.account.builder.apikey.GradeBuilder.buildGrade;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.type.GradeType.GRADE_CLASSIC;

@DataJpaTest
@Import(QuerydslConfig.class)
class ApiKeyRepositoryTest {

    @Autowired
    ApiKeyRepository apiKeyRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    GradeRepository gradeRepository;
    @PersistenceContext
    EntityManager em;

    Role role;
    Member member;
    Grade grade;

    @BeforeEach
    void beforeEach() {
        role = roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
        member = memberRepository.save(buildMember(role));
        grade = gradeRepository.save(buildGrade(GRADE_CLASSIC));
    }

    @Test
    void createTest() {
        // given
        memberRepository.save(member);
        ApiKey apiKey = buildApiKey(grade, member);

        // when
        apiKeyRepository.save(apiKey);
        clear();

        // then
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(foundApiKey.getValue()).isEqualTo(apiKey.getValue());
        assertThat(foundApiKey.getGrade().getGradeType()).isEqualTo(GRADE_CLASSIC);
        assertThat(foundApiKey.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    void uniqueValueTest() {
        // given
        apiKeyRepository.save(buildApiKey(grade, member));
        clear();

        // when, then
        assertThatThrownBy(() -> apiKeyRepository.save(buildApiKey(grade, member)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dateTest() {
        // given
        ApiKey apiKey = buildApiKey(grade, member);

        // when
        apiKeyRepository.save(apiKey);
        clear();

        // then
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(foundApiKey.getCreatedAt()).isNotNull();
        assertThat(foundApiKey.getModifiedAt()).isNotNull();
        assertThat(foundApiKey.getCreatedAt()).isEqualTo(foundApiKey.getModifiedAt());
    }

    @Test
    void deleteTest() {
        // given
        ApiKey apiKey = buildApiKey(grade, member);
        apiKeyRepository.save(apiKey);
        clear();

        // when
        apiKeyRepository.delete(apiKey);
        clear();

        // then
        assertThatThrownBy(() -> apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new))
                .isInstanceOf(ApiKeyNotFoundException.class);
    }

    @Test
    void banAndUnbanForPaymentFailureTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();
        ApiKey apiKey = buildApiKey(grade, member);
        apiKeyRepository.save(apiKey);
        clear();

        // when
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        foundApiKey.banForPaymentFailure(banTime);
        apiKeyRepository.save(foundApiKey);
        clear();

        // then
        ApiKey bannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApiKey.isPaymentFailureBanned()).isTrue();

        // when
        bannedApiKey.unbanForPaymentFailure();
        apiKeyRepository.save(bannedApiKey);
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.isPaymentFailureBanned()).isFalse();
    }

    @Test
    void banAndUnbanCardDeletionTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();
        ApiKey apiKey = buildApiKey(grade, member);
        apiKeyRepository.save(apiKey);
        clear();

        // when
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        foundApiKey.banForCardDeletion(banTime);
        apiKeyRepository.save(foundApiKey);
        clear();

        // then
        ApiKey bannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApiKey.isCardDeletionBanned()).isTrue();

        // when
        bannedApiKey.unbanForCardDeletion();
        apiKeyRepository.save(bannedApiKey);
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.isCardDeletionBanned()).isFalse();
    }

    @Test
    void deletionRequestedAndCancelTest() {
        // given
        LocalDateTime requestTime = LocalDateTime.now();
        ApiKey apiKey = buildApiKey(grade, member);
        apiKeyRepository.save(apiKey);
        clear();

        // when
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        foundApiKey.requestDeletion(requestTime);
        apiKeyRepository.save(foundApiKey);
        clear();

        // then
        ApiKey deletionRequestedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(deletionRequestedApiKey.isDeletionRequested()).isTrue();

        // when
        deletionRequestedApiKey.cancelDeletionRequest();
        apiKeyRepository.save(deletionRequestedApiKey);
        clear();

        // then
        ApiKey cancelRequestedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(cancelRequestedApiKey.isDeletionRequested()).isFalse();
    }

    @Test
    void adminBanAndUnbanTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();
        ApiKey apiKey = buildApiKey(grade, member);
        apiKeyRepository.save(apiKey);
        clear();

        // when
        ApiKey foundApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        foundApiKey.banByAdmin(banTime);
        apiKeyRepository.save(foundApiKey);
        clear();

        // then
        ApiKey bannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApiKey.isAdminBanned()).isTrue();

        // when
        bannedApiKey.unbanByAdmin();
        apiKeyRepository.save(bannedApiKey);
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.isAdminBanned()).isFalse();
    }

    @Test
    void findAllDtosByMemberIdOrderByAsc() {
        // given
        ApiKey apiKey1 = apiKeyRepository.save(ApiKeyBuilder.buildApiKey("testApiKeyValue1", grade, member));
        ApiKey apiKey2 = apiKeyRepository.save(ApiKeyBuilder.buildApiKey("testApiKeyValue2", grade, member));
        clear();

        // when
        Slice<ApiKeyDto> apiKeyDtos = apiKeyRepository.findAllDtosByMemberIdOrderByAsc(member.getId(), null, PageableBuilder.build());

        // then
        assertThat(apiKeyDtos.getNumberOfElements()).isEqualTo(2);
        assertThat(apiKeyDtos.getContent().get(0).getId()).isEqualTo(apiKey1.getId());
        assertThat(apiKeyDtos.getContent().get(1).getId()).isEqualTo(apiKey2.getId());
    }

    @Test
    void deleteAllByMemberId() {
        // given
        apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();

        // when
        apiKeyRepository.deleteAllByMemberId(member.getId());
        clear();

        // then
        List<ApiKey> apiKeys = apiKeyRepository.findAll();
        assertThat(apiKeys.isEmpty()).isTrue();
    }

    @Test
    void banForCardDeletionByMemberId() {
        // given
        ApiKey apikey = apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKeyRepository.banForCardDeletionByMemberId(member.getId(), banTime);
        clear();

        // then
        ApiKey bannedApikey = apiKeyRepository.findById(apikey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApikey.isCardDeletionBanned()).isTrue();
    }

    @Test
    void unbanForCardDeletionByMemberId() {
        // given
        ApiKey apiKey = ApiKeyBuilder.buildApiKey(grade, member);
        apiKey.banForCardDeletion(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        clear();

        // when
        apiKeyRepository.unbanForCardDeletionByMemberId(member.getId());
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.isCardDeletionBanned()).isFalse();
    }

    @Test
    void banApiKeysForPaymentFailure() {
        // given
        ApiKey apikey = apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKeyRepository.banApiKeysForPaymentFailure(member.getId(), banTime);
        clear();

        // then
        ApiKey bannedApikey = apiKeyRepository.findById(apikey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApikey.isPaymentFailureBanned()).isTrue();
    }

    @Test
    void unbanApiKeysForPaymentFailure() {
        // given
        ApiKey apiKey = ApiKeyBuilder.buildApiKey(grade, member);
        apiKey.banForPaymentFailure(LocalDateTime.now());
        ApiKey bannedApiKey = apiKeyRepository.save(apiKey);
        clear();

        // when
        apiKeyRepository.unbanApiKeysForPaymentFailure(member.getId());
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(bannedApiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        Assertions.assertThat(unbannedApiKey.isPaymentFailureBanned()).isFalse();
    }

    @Test
    void requestApiKeyDeletion() {
        // given
        ApiKey apiKey = apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        apiKeyRepository.requestApiKeyDeletion(member.getId(), requestTime);
        clear();

        // then
        ApiKey bannedApikey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApikey.isDeletionRequested()).isTrue();
    }

    @Test
    void cancelApiKeyDeletionRequest() {
        // given
        ApiKey apiKey = ApiKeyBuilder.buildApiKey(grade, member);
        apiKey.requestDeletion(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        clear();

        // when
        apiKeyRepository.cancelApiKeyDeletionRequest(member.getId());
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.getDeletionRequestedAt()).isNull();
    }

    @Test
    void banApiKeysByAdmin() {
        // given
        ApiKey apiKey = apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKeyRepository.banApiKeysByAdmin(member.getId(), banTime);
        clear();

        // then
        ApiKey bannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(bannedApiKey.isAdminBanned()).isTrue();
    }

    @Test
    void unbanApiKeysByAdmin() {
        // given
        ApiKey apiKey = ApiKeyBuilder.buildApiKey(grade, member);
        apiKey.banByAdmin(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        clear();

        // when
        apiKeyRepository.unbanApiKeysByAdmin(member.getId());
        clear();

        // then
        ApiKey unbannedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow(ApiKeyNotFoundException::new);
        assertThat(unbannedApiKey.getAdminBannedAt()).isNull();
    }

    @Test
    void isValid() {
        // given
        ApiKey apiKey = apiKeyRepository.save(ApiKeyBuilder.buildApiKey(grade, member));
        clear();

        // when
        boolean isValid = apiKeyRepository.isValid(apiKey.getId());

        // then
        assertThat(isValid).isTrue();

        // when
        apiKey.banByAdmin(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        clear();
        isValid = apiKeyRepository.isValid(apiKey.getId());

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void findAllDtos() {
        // given
        
        apiKeyRepository.save(ApiKeyBuilder.buildApiKey("testApiKeyValue1", grade, member));
        apiKeyRepository.save(ApiKeyBuilder.buildApiKey("testApiKeyValue2", grade, member));
        clear();

        // when
        Page<ApiKeyDto> apiKeyDtos = apiKeyRepository.findAllDtos(PageableBuilder.build());

        // then
        assertThat(apiKeyDtos.getTotalElements()).isEqualTo(2);
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
