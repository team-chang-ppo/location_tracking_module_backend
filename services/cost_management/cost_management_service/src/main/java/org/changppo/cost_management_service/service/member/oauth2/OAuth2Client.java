package org.changppo.cost_management_service.service.member.oauth2;

import java.io.IOException;

public interface OAuth2Client {
    void unlinkMember(String providerMemberId) throws IOException;
    boolean supports(String provider);
}