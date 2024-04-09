package org.changppo.cost_management_service.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @DeleteMapping("/me")
    public ResponseEntity<Response> deleteMe(HttpServletRequest request, HttpServletResponse response) {
        memberService.delete(PrincipalHandler.extractId(), request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        memberService.delete(id, request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}