package ek.dk.biludlejning.controller;


import ek.dk.biludlejning.model.DamageItem;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.DamageService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class DamageController {
    private final DamageService damageService;
    private final RentalAgreementService rentalAgreementService;

    private static final Logger logger = LoggerFactory.getLogger(DamageController.class);


    public DamageController(DamageService damageService, RentalAgreementService rentalAgreementService) {
        this.damageService = damageService;
        this.rentalAgreementService = rentalAgreementService;
    }

    @GetMapping("/damage-reports")
    public  String damageReports(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @GET /damage-reports", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        model.addAttribute("activePage", "damage-reports");
        model.addAttribute("damageReportList", damageService.getAllDamageReports());
        logger.info("User with User id={} and email={} accessed /damage_reports", currentUser.getId(), currentUser.getEmail());
        return "damage_reports";
    }

    @GetMapping("/damage-report-create")
    public String damageReportCreate(Model model,
                                     @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @GET /damage-report-create", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        if (!model.containsAttribute("damageReport")) {
            logger.info("No existing damageReport found in model, adding new DamageReport for User id={} and email={}", currentUser.getId(), currentUser.getEmail());
            model.addAttribute("damageReport", new DamageReport());
        }

        model.addAttribute("activePage", "damage-reports");
        model.addAttribute("rentalAgreements",
                rentalAgreementService.getReturnedRentalAgreements());
        logger.info("User with User id={} and email={} accessed @GET /damage_report_create", currentUser.getId(), currentUser.getEmail());
        return "damage_report_create";
    }

    @PostMapping("/damage-report-create")
    public String createDamageReport(@ModelAttribute DamageReport damageReport,
                                     Model model,
                                     RedirectAttributes redirectAttributes,
                                     @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @POST /damage-report-create", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        Optional<String> error = damageService.createDamageReport(damageReport, currentUser.getId());

        if (error.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", error.get());
            redirectAttributes.addFlashAttribute("damageReport", damageReport);
            return "redirect:/damage-report-create";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Skaderapporten blev oprettet, og bilen er sat til MAINTENANCE.");

        logger.info("User with User id={} and email={} accessed @POST /damage_report_create", currentUser.getId(), currentUser.getEmail());
        logger.info("Damage report was created, car set to MAINTENANCE");
        return "redirect:/damage-report-create";
    }

    @PostMapping("/add-damage-item")
    public String addDamageItem(
            Model model,
            @RequestParam int reportId,
            @RequestParam String description,
            @RequestParam double price,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = "currentUser", required = false) User currentUser) {

        model.addAttribute("currentUser", currentUser);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @POST /add-damage-item", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        DamageItem damageItem = new DamageItem();
        damageItem.setReportId(reportId);
        damageItem.setDescription(description);
        damageItem.setPrice(price);

        Optional<String> error = damageService.createDamageItem(damageItem);

        if (error.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", error.get());
            return "redirect:/add-damage-item/" + reportId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Skaden blev tilføjet.");
        logger.info("User with User id={} and email={} accessed @POST /damage_report_create", currentUser.getId(), currentUser.getEmail());
        logger.info("Damage item added to report id={}", reportId);
        return "redirect:/damage-reports";
    }

    @GetMapping("/add-damage-item/{reportId}")
    public String showAddDamageItemPage(
            @PathVariable int reportId,
            Model model,
            @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);


        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @GET /add-damge-item/{reportId}", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        model.addAttribute("reportId", reportId);
        model.addAttribute("damageItem", new DamageItem());

        logger.info("User with User id={} and email={} accessed @GET /damage_add_item", currentUser.getId(), currentUser.getEmail());
        logger.info("Showing add damage item page for report id={}", reportId);
        return "damage_add_item";
    }


    @GetMapping("/damage-report/{reportId}")
    public String viewDamageReport(
            @PathVariable int reportId,
            Model model,
            @SessionAttribute(name = "currentUser", required = false) User currentUser) {

        model.addAttribute("currentUser", currentUser);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} and email={} at @GET /damage-report/{reportId}", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        model.addAttribute("damageReport", damageService.getDamageReportById(reportId));
        model.addAttribute("damageItems", damageService.getDamageItemsByReportId(reportId));
        model.addAttribute("activePage", "damage-reports");

        logger.info("User with User id={} and email={} accessed @GET /damage_view", currentUser.getId(), currentUser.getEmail());
        return "damage_view";
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
