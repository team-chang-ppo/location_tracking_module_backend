package org.changppo.account.controller.card;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.card.CardListDto;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.application.card.CardService;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards/v1")
public class CardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBody<CardDto>> read(@PathVariable(name = "id") Long id) {
        CardDto cardDto = cardService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(cardDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<ResponseBody<CardListDto>> readAllMe() {
        CardListDto cardListDto = cardService.readAll(PrincipalHandler.extractId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(cardListDto));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<ResponseBody<CardListDto>> readAll(@PathVariable(name = "id") Long id) {
        CardListDto cardListDto = cardService.readAll(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(cardListDto));
    }

    @GetMapping("/list")  // 사용자에게 제공 X
    public ResponseEntity<ResponseBody<Page<CardDto>>> readList(Pageable pageable) {
        Page<CardDto> cardDtos = cardService.readList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(cardDtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBody<Void>> delete(@PathVariable(name = "id") Long id) {
        cardService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

}
