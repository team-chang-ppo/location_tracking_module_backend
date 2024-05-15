package org.changppo.account.entity.member.oauth2;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOAuth2AuthorizedClientId is a Querydsl query type for OAuth2AuthorizedClientId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QOAuth2AuthorizedClientId extends BeanPath<OAuth2AuthorizedClientId> {

    private static final long serialVersionUID = -611830181L;

    public static final QOAuth2AuthorizedClientId oAuth2AuthorizedClientId = new QOAuth2AuthorizedClientId("oAuth2AuthorizedClientId");

    public final StringPath clientRegistrationId = createString("clientRegistrationId");

    public final StringPath principalName = createString("principalName");

    public QOAuth2AuthorizedClientId(String variable) {
        super(OAuth2AuthorizedClientId.class, forVariable(variable));
    }

    public QOAuth2AuthorizedClientId(Path<? extends OAuth2AuthorizedClientId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOAuth2AuthorizedClientId(PathMetadata metadata) {
        super(OAuth2AuthorizedClientId.class, metadata);
    }

}

