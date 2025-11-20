package com.eygds.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuccessPage {

    @GetMapping("/")
    public String success() {
        return "Success.html";
    }
}
