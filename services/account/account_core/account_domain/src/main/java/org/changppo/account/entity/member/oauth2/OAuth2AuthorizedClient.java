package org.changppo.account.entity.member.oauth2;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Entity
@IdClass(OAuth2AuthorizedClientId.class)
@Table(name = "oauth2_authorized_client")
public class OAuth2AuthorizedClient {

    @Id
    @Column(length = 100, nullable = false)
    private String clientRegistrationId;

    @Id
    @Column(length = 200, nullable = false)
    private String principalName;

    @Column(length = 100, nullable = false)
    private String accessTokenType;

    @Column(nullable = false)
    private byte[] accessTokenValue;

    @Column(nullable = false)
    private LocalDateTime accessTokenIssuedAt;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @Column(length = 1000, nullable = false)
    private String accessTokenScopes;

    private byte[] refreshTokenValue;

    private LocalDateTime refreshTokenIssuedAt;
}
