package org.changppo.account.repository.payment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.dto.payment.QPaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static org.changppo.account.entity.payment.QPayment.payment;
import static org.changppo.account.type.PaymentStatus.COMPLETED_FREE;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements QuerydslPaymentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByMemberId(Long memberId) {
        queryFactory.delete(payment)
                .where(memberIdEquals(memberId))
                .execute();
    }

    @Override
    public Optional<Payment> findFirstByMemberIdOrderByEndedAtDesc(Long memberId) {
        return Optional.ofNullable(queryFactory.selectFrom(payment)
                .where(memberIdEquals(memberId))
                .orderBy(payment.endedAt.desc())
                .fetchFirst());
    }

    @Override
    public Slice<PaymentDto> findAllDtosByMemberIdAndStatusNotCompletedFree(Long memberId, Long lastPaymentId, Pageable pageable) {
        List<PaymentDto> results = queryFactory.select(
                new QPaymentDto(
                        payment.id,
                        payment.amount,
                        payment.status,
                        payment.startedAt,
                        payment.endedAt,
                        payment.cardInfo,
                        payment.createdAt))
                .from(payment)
                .where(memberIdEquals(memberId)
                        .and(paymentIdLessThanOrEqual(lastPaymentId))
                        .and(statusNotCompletedFree()))
                .orderBy(payment.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Page<PaymentDto> findAllDtos(Pageable pageable) {
        List<PaymentDto> content = queryFactory
                .select(
                    new QPaymentDto(
                            payment.id,
                            payment.amount,
                            payment.status,
                            payment.startedAt,
                            payment.endedAt,
                            payment.cardInfo,
                            payment.createdAt))
                .from(payment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(payment.count())
                .from(payment);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return payment.member.id.eq(memberId);
    }

    private BooleanExpression paymentIdLessThanOrEqual(Long lastPaymentId) {
        return lastPaymentId != null ? payment.id.loe(lastPaymentId) : null;
    }

    private BooleanExpression statusNotCompletedFree() {
        return payment.status.ne(COMPLETED_FREE);
    }
}
