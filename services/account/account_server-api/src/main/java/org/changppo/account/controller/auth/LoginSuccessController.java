package org.changppo.account.controller.auth;

import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginSuccessController {

    @GetMapping("/login/success")
    public ResponseEntity<ResponseBody<Void>> loginSuccess() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }
}
