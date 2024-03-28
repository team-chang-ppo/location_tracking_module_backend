package org.changppo.cost_management_service.repository.member;

import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}