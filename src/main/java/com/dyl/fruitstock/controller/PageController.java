package com.dyl.fruitstock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {


    @GetMapping("/")
    public String dashboard(Model model, @RequestParam(value = "userName", required = false) String userName) {
        if (userName != null) {
            model.addAttribute("userName", userName);
        }
        return "frontToC";
    }
}
