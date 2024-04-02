package org.changppo.cost_management_service.exception.oauth2.kakao;

import org.changppo.cost_management_service.exception.common.ExceptionType;
import org.changppo.cost_management_service.exception.oauth2.OAuth2BusinessException;

public class KakaoOAuth2UnlinkFailureException extends OAuth2BusinessException {
    public KakaoOAuth2UnlinkFailureException(Throwable cause) {
        super(ExceptionType.KAKAO_OAUTH2_UNLINK_FAILURE_EXCEPTION, cause);
    }
}