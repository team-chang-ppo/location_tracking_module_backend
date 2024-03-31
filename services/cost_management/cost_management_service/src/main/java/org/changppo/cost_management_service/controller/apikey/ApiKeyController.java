package org.changppo.cost_management_service.controller.apikey;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.aop.AssignMemberId;
import org.changppo.cost_management_service.dto.apikey.ApiKeyCreateRequest;
import org.changppo.cost_management_service.dto.apikey.ApiKeyDto;
import org.changppo.cost_management_service.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.cost_management_service.response.Response;
import org.changppo.cost_management_service.service.apikey.ApiKeyService;
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

    @AssignMemberId
    @GetMapping("")
    public ResponseEntity<Response> readAll(@Valid @ModelAttribute ApiKeyReadAllRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(apiKeyService.readAll(req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable(name = "id") Long id) {
        apiKeyService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}