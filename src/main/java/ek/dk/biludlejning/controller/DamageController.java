package ek.dk.biludlejning.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class DamageController {

@GetMapping("/damage-reports")
    public  String damageReports(Model model) {
    model.addAttribute("activePage", "damage-reports");
    return "damage_reports";
    }

}
