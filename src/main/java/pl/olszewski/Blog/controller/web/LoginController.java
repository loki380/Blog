package pl.olszewski.Blog.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("logincontroller")
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
