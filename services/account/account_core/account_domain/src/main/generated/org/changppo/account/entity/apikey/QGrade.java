package org.changppo.account.entity.apikey;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGrade is a Querydsl query type for Grade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrade extends EntityPathBase<Grade> {

    private static final long serialVersionUID = -1014339250L;

    public static final QGrade grade = new QGrade("grade");

    public final EnumPath<org.changppo.account.type.GradeType> gradeType = createEnum("gradeType", org.changppo.account.type.GradeType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QGrade(String variable) {
        super(Grade.class, forVariable(variable));
    }

    public QGrade(Path<? extends Grade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGrade(PathMetadata metadata) {
        super(Grade.class, metadata);
    }

}

