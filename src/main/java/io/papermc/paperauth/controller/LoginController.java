package io.papermc.paperauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.papermc.paperauth.model.request.LoginRequest;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    @ResponseBody
    @PostMapping("/")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        System.out.println("login " + request);
        return ResponseEntity.ok().build();
    }
}
