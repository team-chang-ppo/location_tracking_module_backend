package org.changppo.account.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.member.MemberRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ReaderConfig {

    private final MemberRepository memberRepository;

    @Bean
    public RepositoryItemReader<Member> memberItemReaderForAutomaticPayment() {  //TODO. ZeroOffSet 기반 조회로 성능 향상
        return new RepositoryItemReaderBuilder<Member>()
                .repository(memberRepository)
                .methodName("findMembersForAutomaticPayment")
                .pageSize(10)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .name("repositoryItemReader")
                .build();
    }

    @Bean
    public RepositoryItemReader<Member> memberItemReaderForDeletion() {  //TODO. ZeroOffSet 기반 조회로 성능 향상
        return new RepositoryItemReaderBuilder<Member>()
                .repository(memberRepository)
                .methodName("findMembersForDeletion")
                .arguments(Collections.singletonList(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIDNIGHT)))
                .pageSize(10)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .name("deletionRepositoryItemReader")
                .build();
    }
}
