package io.papermc.hangarauth.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public List<Integer> get() {
        return List.of(1, 3, 5);
    }

    @GetMapping("/favicon.ico")
    public void getFavicon() {
        //
    }
}
