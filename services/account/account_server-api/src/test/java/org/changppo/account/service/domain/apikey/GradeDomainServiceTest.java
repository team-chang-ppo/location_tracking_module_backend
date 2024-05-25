package org.changppo.account.service.domain.apikey;

import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.repository.apikey.GradeRepository;
import org.changppo.account.response.exception.apikey.GradeNotFoundException;
import org.changppo.account.type.GradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.apikey.GradeBuilder.buildGrade;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GradeDomainServiceTest {

    @InjectMocks
    private GradeDomainService gradeDomainService;

    @Mock
    private GradeRepository gradeRepository;

    Grade grade;

    @BeforeEach
    void beforeEach() {
        grade = buildGrade(GradeType.GRADE_CLASSIC);
    }

    @Test
    void getGradeByTypeTest() {
        // given
        given(gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC)).willReturn(Optional.of(grade));

        // when
        Grade result = gradeDomainService.getGradeByType(GradeType.GRADE_CLASSIC);

        // then
        assertThat(result).isEqualTo(grade);
    }

    @Test
    void getGradeByTypeExceptionTest() {
        // given
        given(gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> gradeDomainService.getGradeByType(GradeType.GRADE_CLASSIC))
                .isInstanceOf(GradeNotFoundException.class);
    }
}
