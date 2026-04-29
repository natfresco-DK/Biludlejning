package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class ErrorController {
    @GetMapping("/access-denied")
    public String accessDenied(Model model,
                               @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("errorMessage", "Du har ikke tilladelse til at få adgang til denne side.");
        return "error";
    }
}
