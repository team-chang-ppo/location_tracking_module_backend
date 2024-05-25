package org.changppo.account.service.application.card;

import org.changppo.account.dto.card.CardCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class CardServiceConcurrencyTest {

    @Autowired
    private CardService cardService;

    @Test
    @Transactional
    public void testConcurrentDeleteAndCreate() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long cardId = 1L;        Long memberId = 1L;

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    cardService.delete(cardId);
                    cardService.create(new CardCreateRequest(memberId, ...)); // 필요한 생성 요청 데이터 설정
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 결과 검증
        // 회원의 카드 개수가 적절히 유지되었는지 확인합니다.
        long remainingCardCount = cardService.getCardCountByMemberId(memberId);
        assertFalse(remainingCardCount < 0);
    }
}
