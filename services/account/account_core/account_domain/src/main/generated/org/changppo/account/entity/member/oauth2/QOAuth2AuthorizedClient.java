package org.changppo.account.entity.member.oauth2;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOAuth2AuthorizedClient is a Querydsl query type for OAuth2AuthorizedClient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOAuth2AuthorizedClient extends EntityPathBase<OAuth2AuthorizedClient> {

    private static final long serialVersionUID = 334558496L;

    public static final QOAuth2AuthorizedClient oAuth2AuthorizedClient = new QOAuth2AuthorizedClient("oAuth2AuthorizedClient");

    public final DateTimePath<java.time.LocalDateTime> accessTokenExpiresAt = createDateTime("accessTokenExpiresAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> accessTokenIssuedAt = createDateTime("accessTokenIssuedAt", java.time.LocalDateTime.class);

    public final StringPath accessTokenScopes = createString("accessTokenScopes");

    public final StringPath accessTokenType = createString("accessTokenType");

    public final ArrayPath<byte[], Byte> accessTokenValue = createArray("accessTokenValue", byte[].class);

    public final StringPath clientRegistrationId = createString("clientRegistrationId");

    public final StringPath principalName = createString("principalName");

    public final DateTimePath<java.time.LocalDateTime> refreshTokenIssuedAt = createDateTime("refreshTokenIssuedAt", java.time.LocalDateTime.class);

    public final ArrayPath<byte[], Byte> refreshTokenValue = createArray("refreshTokenValue", byte[].class);

    public QOAuth2AuthorizedClient(String variable) {
        super(OAuth2AuthorizedClient.class, forVariable(variable));
    }

    public QOAuth2AuthorizedClient(Path<? extends OAuth2AuthorizedClient> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOAuth2AuthorizedClient(PathMetadata metadata) {
        super(OAuth2AuthorizedClient.class, metadata);
    }

}

