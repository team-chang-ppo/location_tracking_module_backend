package org.changppo.account.repository.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.service.dto.member.MemberDto;
import org.changppo.account.service.dto.member.QMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static org.changppo.account.entity.member.QMember.member;
import static org.changppo.account.entity.member.QRole.role;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements QuerydslMemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByNameWithRoles(String name) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                        .join(member.role, role).fetchJoin()
                        .where(nameEquals(name))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Member> findByIdWithRoles(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                        .join(member.role , role).fetchJoin()
                        .where(idEquals(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<MemberDto> findDtoById(Long id) {
        return Optional.ofNullable(
                queryFactory.select(new QMemberDto(
                                member.id,
                                member.name,
                                member.username,
                                member.profileImage,
                                member.role.roleType,
                                member.paymentFailureBannedAt,
                                member.createdAt
                        ))
                        .from(member)
                        .join(member.role, role)
                        .where(idEquals(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<MemberDto> findAllDtos(Pageable pageable) {
        List<MemberDto> content = queryFactory
                .select(new QMemberDto(
                        member.id,
                        member.name,
                        member.username,
                        member.profileImage,
                        member.role.roleType,
                        member.paymentFailureBannedAt,
                        member.createdAt
                ))
                .from(member)
                .join(member.role, role)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .join(member.role, role);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameEquals(String name) {
        return member.name.eq(name);
    }

    private BooleanExpression idEquals(Long id) {
        return member.id.eq(id);
    }
}
