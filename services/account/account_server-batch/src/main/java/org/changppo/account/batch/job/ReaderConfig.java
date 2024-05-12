package org.changppo.account.batch.job;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.batch.config.reader.QuerydslNoOffsetPagingItemReader;
import org.changppo.account.batch.config.reader.QuerydslZeroPagingItemReader;
import org.changppo.account.entity.member.Member;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.querydsl.reader.expression.Expression;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import static org.changppo.account.entity.member.QMember.member;

@Configuration
@RequiredArgsConstructor
public class ReaderConfig {

    public static final String AUTOMATIC_PAYMENT_READER = "memberItemReaderForAutomaticPayment";
    public static final String DELETION_READER = "memberItemReaderForDeletion";

    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 10;

    @Bean(AUTOMATIC_PAYMENT_READER)
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

    @Bean(DELETION_READER)
    @StepScope
    public QuerydslZeroPagingItemReader<Member> memberItemReaderForDeletion(@Value("#{jobParameters[JobStartTime]}") LocalDateTime jobStartTime) {
        return new QuerydslZeroPagingItemReader<>(
                entityManagerFactory,
                chunkSize,
                queryFactory -> queryFactory
                        .selectFrom(member)
                            .where(member.paymentFailureBannedAt.isNull()
                            .and(member.deletionRequestedAt.loe(jobStartTime.minusDays(2)))));  //결제 정합성을 고려하여 이틀 이전의 시간으로 조회 TODO. 추후 개선
    }
}
