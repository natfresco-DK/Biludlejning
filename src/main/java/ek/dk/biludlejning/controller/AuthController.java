package ek.dk.biludlejning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activePage", "/");
        return "index";
    }


}
