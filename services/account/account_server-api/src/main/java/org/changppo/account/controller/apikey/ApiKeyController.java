package org.changppo.account.controller.apikey;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.aop.AssignMemberId;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.response.Response;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.apikey.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apikeys/v1")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @AssignMemberId
    @PostMapping("/createFreeKey")
    public ResponseEntity<Response> createFreeKey(@Valid @RequestBody ApiKeyCreateRequest req) {
        ApiKeyDto apiKeyDto = apiKeyService.createFreeKey(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(apiKeyDto));
    }

    @AssignMemberId
    @PostMapping("/createClassicKey")
    public ResponseEntity<Response> createClassicKey(@Valid @RequestBody ApiKeyCreateRequest req) {
        ApiKeyDto apiKeyDto = apiKeyService.createClassicKey(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(apiKeyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> read(@PathVariable(name = "id") Long id) {
        ApiKeyDto apiKeyDto = apiKeyService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(apiKeyDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<Response> readAll(@Valid @ModelAttribute ApiKeyReadAllRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(apiKeyService.readAll(PrincipalHandler.extractId(), req)));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<Response> readAll(@PathVariable(name = "id") Long id, @Valid @ModelAttribute ApiKeyReadAllRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(apiKeyService.readAll(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable(name = "id") Long id) {
        apiKeyService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}
