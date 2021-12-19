package io.papermc.hangarauth.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/update")
public class UpdateController {

    @PostMapping("/user")
    public void updateUser(@NotNull @RequestBody ObjectNode body, @RequestHeader("X-Kratos-Hook-Api-Key") String apiKey) {
        System.out.println(apiKey);
        System.out.println(body);
    }
}
