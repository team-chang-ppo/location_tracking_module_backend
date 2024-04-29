package org.changppo.cost_management_service.service.payment.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ReaderConfig {

    private final MemberRepository memberRepository;

    @Bean
    public RepositoryItemReader<Member> memberItemReader() {  //TODO. ZeroOffSet 기반 조회로 성능 향상
        return new RepositoryItemReaderBuilder<Member>()
                .repository(memberRepository)
                .methodName("findByPaymentFailureBannedAtIsNull")
                .pageSize(10)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .name("repositoryItemReader")
                .build();
    }
}
