package org.changppo.account.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomOAuth2UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByNameWithRoles(username).orElseThrow(MemberNotFoundException::new);

        return new CustomOAuth2UserDetails(
                member.getId(),
                member.getName(),
                member.getPassword(),
                member.getMemberRoles().stream()
                        .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                        .collect(Collectors.toSet())
        );
    }
}
