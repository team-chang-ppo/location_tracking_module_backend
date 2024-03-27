package org.changppo.cost_management_service.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.response.Response;
import org.changppo.cost_management_service.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/members/{id}")
    public ResponseEntity<Response> read(@PathVariable(name = "id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(memberService.read(id)));
    }

    @DeleteMapping("/api/members/{id}")
    public ResponseEntity<Response> delete(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        memberService.delete(id, request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }
}