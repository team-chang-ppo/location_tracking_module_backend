package org.changppo.account.repository.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.member.Role;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;

@DataJpaTest
@Import(QuerydslConfig.class)
class RoleRepositoryTest {
    @Autowired
    RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void createTest() {
        // given
        Role role = buildRole(RoleType.ROLE_NORMAL);

        // when
        roleRepository.save(role);
        clear();

        // then
        Role foundRole = roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new);
        assertThat(foundRole.getId()).isEqualTo(role.getId());
    }

    @Test
    void deleteTest() {
        // given
        Role role = roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
        clear();

        // when
        roleRepository.delete(role);

        // then
        assertThatThrownBy(() -> roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void uniqueRoleTypeTest() {
        // given
        roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
        clear();

        // when, then
        assertThatThrownBy(() -> roleRepository.save(buildRole(RoleType.ROLE_NORMAL)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
