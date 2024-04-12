package com.example.social.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
public class SocialController {

  @GetMapping("/social/user")
  public ResponseEntity<String> getUser(@AuthenticationPrincipal OAuth2User principal) {
    String returnValue = "none";
    System.out.println("/social/user has been called");
    if (principal != null) {
      returnValue = principal.getAttribute("name");
    } else {
      return ResponseEntity.status(401).body("non autorizzato");
    }

    return ResponseEntity.status(200).body(returnValue);
  }

  @GetMapping("/something/error")
  public ResponseEntity<String> getSomeError(@AuthenticationPrincipal OAuth2User principal) {
    throw new HttpServerErrorException(HttpStatusCode.valueOf(500));
  }

  @GetMapping("/error")
  public String doError(HttpServletRequest req) {
    String msg = (String) req.getSession().getAttribute("error.message");
    req.getSession().removeAttribute("error.message");
    return msg;
  }
}
