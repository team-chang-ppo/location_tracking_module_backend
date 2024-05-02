package org.changppo.account.repository.apikey;

import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.type.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByGradeType(GradeType gradeType);
}
