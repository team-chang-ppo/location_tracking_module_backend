package org.changppo.tracking.jwt.exception;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.changppo.tracking.exception.common.ErrorCode;

@Getter
public class JwtTokenExpiredException extends JwtAuthenticationException {

    private final Claims claims;

    public JwtTokenExpiredException(Claims claims) {
        super(ErrorCode.JWT_EXPIRED);
        this.claims = claims;
    }

}
