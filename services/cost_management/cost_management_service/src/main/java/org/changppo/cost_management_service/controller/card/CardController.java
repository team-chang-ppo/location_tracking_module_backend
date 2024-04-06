package org.changppo.cost_management_service.controller.card;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.card.CardDto;
import org.changppo.cost_management_service.response.Response;
import org.changppo.cost_management_service.security.PrincipalHandler;
import org.changppo.cost_management_service.service.card.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards/v1")
public class CardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public ResponseEntity<Response> read(@PathVariable(name = "id") Long id) {
        CardDto cardDto = cardService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(cardDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<Response> readAllMe() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(cardService.readAll(PrincipalHandler.extractId())));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<Response> readAll(@PathVariable(name = "id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(cardService.readAll(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable(name = "id") Long id) {
        cardService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

}
