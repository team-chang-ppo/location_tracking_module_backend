package org.changppo.cost_management_service.service.member.oauth;

import java.io.IOException;

public interface OAuth2Service {
    void unlinkUser(String providerUserId) throws IOException;
    boolean supports(String provider);
}