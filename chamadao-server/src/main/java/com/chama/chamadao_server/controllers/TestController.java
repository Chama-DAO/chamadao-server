package com.chama.chamadao_server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-notifications")
    public String testNotifications() {
        return "index";
    }
}
