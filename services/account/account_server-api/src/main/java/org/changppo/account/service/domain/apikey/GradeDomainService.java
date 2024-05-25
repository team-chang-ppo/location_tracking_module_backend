package org.changppo.account.service.domain.apikey;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.repository.apikey.GradeRepository;
import org.changppo.account.response.exception.apikey.GradeNotFoundException;
import org.changppo.account.type.GradeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class GradeDomainService {

    private final GradeRepository gradeRepository;

    public Grade getGradeByType(GradeType gradeType) {
        return gradeRepository.findByGradeType(gradeType).orElseThrow(GradeNotFoundException::new);
    }
}
