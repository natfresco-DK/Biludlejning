package ek.dk.biludlejning.controller;


import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.DamageService;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DamageController {
    private final DamageService damageService;
    private final CarService carService;
    private final RentalAgreementService rentalAgreementService;

    public DamageController(DamageService damageService, CarService carService, RentalAgreementService rentalAgreementService) {
        this.damageService = damageService;
        this.carService = carService;
        this.rentalAgreementService = rentalAgreementService;
    }

    @GetMapping("/damage-reports")
    public  String damageReports(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "damage-reports");
        return "damage_reports";
    }

    @GetMapping("/damage-report-create")
    public String damageReportCreate(Model model,
                                     @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }

        if (!model.containsAttribute("damageReport")) {
            model.addAttribute("damageReport", new DamageReport());
        }

        List<Car> returnedCars = carService.getReturnedCars();

        List<Integer> returnedCarIds = returnedCars.stream()
                .map(Car::getCarId)
                .collect(Collectors.toList());

        List<RentalAgreement> rentalAgreementsWithReturnedCars =
                rentalAgreementService.getAllRentalAgreements().stream()
                        .filter(ra -> returnedCarIds.contains(ra.getCar()))
                        .collect(Collectors.toList());

        model.addAttribute("activePage", "damage-reports");
        model.addAttribute("rentalAgreements", rentalAgreementsWithReturnedCars);
        return "damage_create";
    }

    @PostMapping("/damage-report-create")
    public String createDamageReport(@ModelAttribute DamageReport damageReport,
                                     RedirectAttributes redirectAttributes,
                                     @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }
        damageService.createDamageReport(damageReport, currentUser.getId());

        RentalAgreement rentalAgreement =
                rentalAgreementService.getAllRentalAgreements().stream()
                        .filter(ra -> ra.getAgreementId() == damageReport.getRentalAgreementId())
                        .findFirst()
                        .orElse(null);

        if (rentalAgreement == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lejeaftalen blev ikke fundet.");
            return "redirect:/damage-report-create";
        }

        int carId = rentalAgreement.getCar();
        carService.updateCarStatus(carId, "MAINTENANCE");
        redirectAttributes.addFlashAttribute("successMessage",
                "Skaderapporten blev oprettet, og bilen er sat til MAINTENANCE.");

        return "redirect:/damage-report-create";
    }

    private String checkAccess(User currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!("SKADE/UDBEDRING".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
            return "redirect:/access-denied";
        }
        return null;
    }
}
