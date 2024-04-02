package org.changppo.cost_management_service.service.member.oauth2;

public interface OAuth2Client {
    void unlink(String MemberId);
    boolean supports(String provider);
}