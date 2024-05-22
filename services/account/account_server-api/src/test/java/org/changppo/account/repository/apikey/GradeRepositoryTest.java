package org.changppo.account.repository.apikey;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.response.exception.apikey.GradeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.apikey.GradeBuilder.buildGrade;
import static org.changppo.account.type.GradeType.GRADE_CLASSIC;

@DataJpaTest
@Import(QuerydslConfig.class)
class GradeRepositoryTest {
    @Autowired
    GradeRepository gradeRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void createAndReadTest() {
        // given
        Grade grade = gradeRepository.save(buildGrade(GRADE_CLASSIC));

        // when
        gradeRepository.save(grade);
        clear();

        // then
        Grade foundGrade = gradeRepository.findById(grade.getId()).orElseThrow(GradeNotFoundException::new);
        assertThat(foundGrade.getId()).isEqualTo(grade.getId());
    }

    @Test
    void deleteTest() {
        // given
        Grade grade = gradeRepository.save(buildGrade(GRADE_CLASSIC));
        clear();

        // when
        gradeRepository.delete(grade);

        // then
        assertThatThrownBy(() -> gradeRepository.findById(grade.getId()).orElseThrow(GradeNotFoundException::new))
                .isInstanceOf(GradeNotFoundException.class);
    }

    @Test
    void uniqueGradeTypeTest() {
        // given
        gradeRepository.save(buildGrade(GRADE_CLASSIC));
        clear();

        // when, then
        assertThatThrownBy(() -> gradeRepository.save(buildGrade(GRADE_CLASSIC)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
