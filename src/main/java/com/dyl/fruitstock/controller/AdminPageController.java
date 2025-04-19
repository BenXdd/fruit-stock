package com.dyl.fruitstock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/admin")
@Controller
public class AdminPageController {


    @GetMapping("/")
    public String index() {
        return "login1"; // 对应 templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(value = "userName", required = false) String userName) {
        if (userName != null) {
            model.addAttribute("userName", userName);
        }
        return "dashboard"; // 渲染 templates/dashboard.html
    }


    @GetMapping("/orders")
    public String orders(Model model) {

        return "orders"; // 渲染 templates/dashboard.html
    }

}
