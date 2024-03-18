package com.example.social.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialController {

  @GetMapping("/social/user")
  public ResponseEntity<String> getUser(@AuthenticationPrincipal OAuth2User principal) {
    String returnValue = "none";
    if (principal != null) {
      returnValue = principal.getAttribute("name");
    } else {
      return ResponseEntity.status(401).body("non autorizzato");
    }

    return ResponseEntity.status(200).body(returnValue);
  }
}
