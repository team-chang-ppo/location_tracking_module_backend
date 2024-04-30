package org.changppo.cost_management_service.controller.member;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.member.MemberDto;
import org.changppo.cost_management_service.response.Response;
import org.changppo.cost_management_service.security.PrincipalHandler;
import org.changppo.cost_management_service.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/v1")
public class MemberController {

    private final MemberService memberService;

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

    @DeleteMapping("/request/me")
    public ResponseEntity<Response> requestDeleteMe() {
        memberService.requestDelete(PrincipalHandler.extractId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<Response> requestDelete(@PathVariable(name = "id") Long id) {
        memberService.requestDelete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @DeleteMapping("/cancel/me")
    public ResponseEntity<Response> cancelDeleteMe() {
        memberService.cancelDelete(PrincipalHandler.extractId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Response> cancelDelete(@PathVariable(name = "id") Long id) {
        memberService.cancelDelete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}