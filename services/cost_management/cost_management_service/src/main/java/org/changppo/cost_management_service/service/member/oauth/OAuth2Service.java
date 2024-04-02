package org.changppo.cost_management_service.service.member.oauth;

import java.io.IOException;

public interface OAuth2Service {
    void unlinkMember(String providerMemberId) throws IOException;
    boolean supports(String provider);
}