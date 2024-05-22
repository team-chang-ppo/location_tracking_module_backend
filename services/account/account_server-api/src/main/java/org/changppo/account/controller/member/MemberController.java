package org.changppo.account.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.member.PrincipalDto;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.dto.member.MemberDto;
import org.changppo.account.service.member.MemberService;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/v1")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/principal")
    public ResponseEntity<ResponseBody<PrincipalDto>> readPrincipal() {
        PrincipalDto principalDto = new PrincipalDto(PrincipalHandler.extractId(), PrincipalHandler.extractMemberRoles());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(principalDto));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseBody<MemberDto>> readMe() {
        MemberDto memberDto = memberService.read(PrincipalHandler.extractId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(memberDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBody<MemberDto>> read(@PathVariable(name = "id") Long id) {
        MemberDto memberDto = memberService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(memberDto));
    }

    @GetMapping("/list") // 사용자에게 제공 X
    public ResponseEntity<ResponseBody<Page<MemberDto>>> readList(Pageable pageable) {
        Page<MemberDto> memberDtos = memberService.readList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(memberDtos));
    }

    @PutMapping("/request/me")
    public ResponseEntity<ResponseBody<Void>> requestDeleteMe(HttpServletRequest request, HttpServletResponse response) {
        memberService.requestDelete(PrincipalHandler.extractId(), request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @PutMapping("/request/{id}")
    public ResponseEntity<ResponseBody<Void>> requestDelete(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        memberService.requestDelete(id, request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @PutMapping("/cancel/{id}")  // 사용자에게 제공 X
    public ResponseEntity<ResponseBody<Void>> cancelDelete(@PathVariable(name = "id") Long id) {
        memberService.cancelDelete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @PutMapping("/ban/{id}")
    public ResponseEntity<ResponseBody<Void>> ban(@PathVariable(name = "id") Long id) {
        memberService.ban(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @PutMapping("/unban/{id}")
    public ResponseEntity<ResponseBody<Void>> unban(@PathVariable(name = "id") Long id) {
        memberService.unban(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }
}
