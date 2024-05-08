package org.changppo.account.batch.job;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.QMember;
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader;
import org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader;
import org.springframework.batch.item.querydsl.reader.expression.Expression;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.util.function.Function;
import static org.changppo.account.entity.member.QMember.member;

@Configuration
@RequiredArgsConstructor
public class ReaderConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 10;

    @Bean
    public QuerydslNoOffsetPagingItemReader<Member> memberItemReaderForAutomaticPayment() {
        QuerydslNoOffsetNumberOptions<Member, Long> options = new QuerydslNoOffsetNumberOptions<>(member.id, Expression.ASC);
        return new QuerydslNoOffsetPagingItemReader<>(
                entityManagerFactory,
                chunkSize,
                options,
                queryFactory -> queryFactory
                        .selectFrom(member)
                        .where(member.paymentFailureBannedAt.isNull()
                                .and(member.deletionRequestedAt.isNull())));
    }

    @Bean
    public QuerydslPagingItemReader<Member> memberItemReaderForDeletion() {
        return new QuerydslPagingItemReader<Member>(entityManagerFactory, chunkSize,
                (Function<JPAQueryFactory, JPAQuery<Member>>) queryFactory ->
                        queryFactory
                                .selectFrom(QMember.member)
                                .where(QMember.member.deletionRequestedAt.isNotNull()
                                        .and(QMember.member.deletionRequestedAt.loe(LocalDateTime.now()))
                                        .and(QMember.member.paymentFailureBannedAt.isNull())));
    }

}
