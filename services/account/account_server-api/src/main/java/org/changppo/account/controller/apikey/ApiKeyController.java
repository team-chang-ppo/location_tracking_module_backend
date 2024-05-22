package org.changppo.account.controller.apikey;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.aop.AssignMemberId;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyListDto;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.dto.apikey.ApiKeyValidationResponse;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.apikey.ApiKeyService;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ResponseBody<ApiKeyDto>> createFreeKey(@Valid @RequestBody ApiKeyCreateRequest req) {
        ApiKeyDto apiKeyDto = apiKeyService.createFreeKey(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponseBody<>(apiKeyDto));
    }

    @AssignMemberId
    @PostMapping("/createClassicKey")
    public ResponseEntity<ResponseBody<ApiKeyDto>> createClassicKey(@Valid @RequestBody ApiKeyCreateRequest req) {
        ApiKeyDto apiKeyDto = apiKeyService.createClassicKey(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponseBody<>(apiKeyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBody<ApiKeyDto>> read(@PathVariable(name = "id") Long id) {
        ApiKeyDto apiKeyDto = apiKeyService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(apiKeyDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<ResponseBody<ApiKeyListDto>> readAll(@Valid @ModelAttribute ApiKeyReadAllRequest req) {
         ApiKeyListDto apiKeyListDto = apiKeyService.readAll(PrincipalHandler.extractId(), req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(apiKeyListDto));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<ResponseBody<ApiKeyListDto>> readAll(@PathVariable(name = "id") Long id, @Valid @ModelAttribute ApiKeyReadAllRequest req) {
        ApiKeyListDto apiKeyListDto = apiKeyService.readAll(id, req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(apiKeyListDto));
    }

    @GetMapping("/list")  // 사용자에게 제공 X
    public ResponseEntity<ResponseBody<Page<ApiKeyDto>>> readList(Pageable pageable) {
        Page<ApiKeyDto> apiKeyDtos = apiKeyService.readList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(apiKeyDtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBody<Void>> delete(@PathVariable(name = "id") Long id) {
        apiKeyService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @GetMapping("/validate/{id}")
    public  ResponseEntity<ResponseBody<ApiKeyValidationResponse>> validate(@PathVariable(name = "id") Long id) {
        ApiKeyValidationResponse apiKeyValidationResponse = apiKeyService.validate(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(apiKeyValidationResponse));
    }
}
