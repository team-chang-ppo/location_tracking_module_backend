package org.changppo.account.batch.job;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.batch.config.reader.QuerydslNoOffsetPagingItemReader;
import org.changppo.account.batch.config.reader.QuerydslZeroPagingItemReader;
import org.changppo.account.entity.member.Member;
import org.springframework.batch.item.querydsl.reader.expression.Expression;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
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
    public QuerydslZeroPagingItemReader<Member> memberItemReaderForDeletion() {
        return new QuerydslZeroPagingItemReader<>(
                entityManagerFactory,
                chunkSize,
                queryFactory -> queryFactory
                        .selectFrom(member)
                        .where(member.paymentFailureBannedAt.isNull()
                                .and(member.deletionRequestedAt.loe(LocalDateTime.now()))));
    }

}
