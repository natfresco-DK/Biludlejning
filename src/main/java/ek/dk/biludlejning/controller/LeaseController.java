package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.CustomerService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LeaseController {

    private final RentalAgreementService rentalAgreementService;
    private final CustomerService customerService;
    private final CarService carService;

    public LeaseController(RentalAgreementService rentalAgreementService,
                           CustomerService customerService,
                           CarService carService) {
        this.rentalAgreementService = rentalAgreementService;
        this.customerService = customerService;
        this.carService = carService;
    }

    @GetMapping("/lease-agreements")
    public String leaseAgreements(Model model) {
        model.addAttribute("activePage", "lease-agreements");
        model.addAttribute("rentalAgreementList", rentalAgreementService.getAllRentalAgreements());
        return "lease_agreements";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("activePage", "reports");
        return "reports";
    }

    @GetMapping("/lease-create")
    public String leaseCreate(Model model) {
        model.addAttribute("activePage", "lease-agreements");

        if (!model.containsAttribute("rentalAgreement")) {
            model.addAttribute("rentalAgreement", new RentalAgreement());
        }

        model.addAttribute("customers", customerService.getAllActiveCustomers());
        model.addAttribute("cars", carService.getAvailableCars());

        return "lease_create";
    }

    @PostMapping("/lease-create")
    public String createLease(@ModelAttribute RentalAgreement rentalAgreement,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        int createdBy = 1; // midlertidigt, indtil login/session er på plads

        var validationError = rentalAgreementService.createRentalAgreement(rentalAgreement, createdBy);

        if (validationError.isPresent()) {
            model.addAttribute("activePage", "lease-agreements");
            model.addAttribute("errorMessage", validationError.get());
            model.addAttribute("rentalAgreement", rentalAgreement);
            model.addAttribute("customers", customerService.getAllActiveCustomers());
            model.addAttribute("cars", carService.getAvailableCars());
            return "lease_create";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Lejeaftalen blev oprettet");
        return "redirect:/lease-agreements";
    }
}