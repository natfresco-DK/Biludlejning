package ek.dk.biludlejning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarController {

    @GetMapping("/cars")
    public  String damageReports(Model model) {
        model.addAttribute("activePage", "cars");
        return "cars";
    }

}
