package com.example.social.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialController {

  @GetMapping("/social/user")
  public String getUser(@AuthenticationPrincipal OAuth2User principal) {
    String returnValue = "none";
    if (principal != null) {
      returnValue = principal.getAttribute("name");
    }

    return returnValue;
  }
}
