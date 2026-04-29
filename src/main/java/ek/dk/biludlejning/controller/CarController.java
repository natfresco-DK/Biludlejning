package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class CarController {

    @GetMapping("/cars")
    public  String damageReports(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "cars");
        return "cars";
    }

}
