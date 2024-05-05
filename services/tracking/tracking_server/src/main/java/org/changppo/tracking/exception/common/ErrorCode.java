package org.changppo.tracking.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "COMMON_0001", "요청한 값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(400, "COMMON_0002", "해당 리소스를 찾을 수 없습니다."),
    HEADER_NOT_FOUND(400, "COMMON_0003", "헤더의 값이 올바르지 않습니다."),
    UNEXPECTED_SERVER_ERROR(500, "COMMON_0003", "예상치 못한 서버 에러가 발생했습니다."),

    // tracking - coordinates
    TRACKING_DUPLICATE(409,"TRACKING_0001","TRACKING 중복되는 정보가 있습니다."),
    TRACKING_NOT_FOUND(404, "TRACKING_0002", "해당 TRACKING 정보가 존재하지 않습니다."),
    TRACKING_ALREADY_EXITED(410, "TRACKING_0003", "해당 TRACKING은 이미 종료되었습니다."),
    COORDINATES_NOT_FOUND(404, "TRACKING_0004", "해당 COORDINATES 정보가 존재하지 않습니다."),
    API_KEY_ID_IS_NOT_MATCHED(403, "TRACKING_0005", "해당 Tracking 정보를 볼 권한이 없습니다."),

    // jwt
    AUTHORIZED_FAILED(401,"SECURITY_0001","인증이 필요합니다."),
    JWT_EXPIRED(401, "SECURITY_0002", "JWT 토큰이 만료되었습니다."),
    JWT_INVALID(401, "SECURITY_0003", "JWT 토큰이 올바르지 않습니다."),
    JWT_NOT_EXIST(401, "SECURITY_0004", "JWT 토큰이 존재하지 않습니다."),
    ACCESS_DENIED(403, "SECURITY_0005", "해당 리소스에 접근할 권한이 없습니다."),
    REQUIRED_AUTHENTICATION(401, "SECURITY_0006", "토큰이 필요한 접근입니다."),


    ;

    private final int status;
    private final String code;
    private final String message;
}
