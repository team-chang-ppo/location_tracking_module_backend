package org.changppo.account.repository.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import java.util.Optional;

import static org.changppo.account.entity.member.QMember.member;
import static org.changppo.account.entity.member.QMemberRole.memberRole;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements QuerydslMemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByNameWithRoles(String name) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                        .join(member.memberRoles, memberRole).fetchJoin()
                        .where(nameEquals(name))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Member> findByIdWithRoles(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                        .join(member.memberRoles, memberRole).fetchJoin()
                        .where(idEquals(id))
                        .fetchOne()
        );
    }

    private BooleanExpression nameEquals(String name) {
        return member.name.eq(name);
    }

    private BooleanExpression idEquals(Long id) {
        return member.id.eq(id);
    }
}
