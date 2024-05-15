package org.changppo.account.entity.member.oauth2;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuth2AuthorizedClientId implements Serializable {

    private String clientRegistrationId;
    private String principalName;
}
