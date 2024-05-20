package org.changppo.account.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.member.PrincipalDto;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.dto.member.MemberDto;
import org.changppo.account.service.member.MemberService;
import org.changppo.utils.response.body.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/v1")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/principal")
    public ResponseEntity<Response> readPrincipal() {
        PrincipalDto principalDto = new PrincipalDto(PrincipalHandler.extractId(), PrincipalHandler.extractMemberRoles());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(principalDto));
    }

    @GetMapping("/me")
    public ResponseEntity<Response> readMe() {
        MemberDto memberDto = memberService.read(PrincipalHandler.extractId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(memberDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> read(@PathVariable(name = "id") Long id) {
        MemberDto memberDto = memberService.read(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(memberDto));
    }

    @GetMapping("/all") // 사용자에게 제공 X
    public ResponseEntity<Response> readAll(@PageableDefault Pageable pageable) {
        Page<MemberDto> memberDtos = memberService.readAll(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(memberDtos));
    }

    @PutMapping("/request/me")
    public ResponseEntity<Response> requestDeleteMe(HttpServletRequest request, HttpServletResponse response) {
        memberService.requestDelete(PrincipalHandler.extractId(), request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @PutMapping("/request/{id}")
    public ResponseEntity<Response> requestDelete(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        memberService.requestDelete(id, request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @PutMapping("/cancel/{id}")  // 사용자에게 제공 X
    public ResponseEntity<Response> cancelDelete(@PathVariable(name = "id") Long id) {
        memberService.cancelDelete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}
