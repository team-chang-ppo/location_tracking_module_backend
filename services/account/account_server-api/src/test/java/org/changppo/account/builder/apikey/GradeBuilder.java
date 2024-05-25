package org.changppo.account.builder.apikey;

import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.type.GradeType;

public class GradeBuilder {
    public static Grade buildGrade(GradeType gradeType) {
        return new Grade(gradeType);
    }
}
