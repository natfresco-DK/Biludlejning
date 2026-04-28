package ek.dk.biludlejning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("activePage", "users");
        return "users";
    }

}
