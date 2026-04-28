package ek.dk.biludlejning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class LeaseController {

    @GetMapping("/lease-agreements")
    public String leaseAgreements(Model model) {
        model.addAttribute("activePage", "lease-agreements");
        return "lease_agreements";
    }

    @GetMapping("reports")
    public String reports(Model model) {
        model.addAttribute("activePage", "reports");
        return "reports";
    }


}
