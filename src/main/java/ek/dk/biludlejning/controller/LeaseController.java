package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.CustomerService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LeaseController {

    private final RentalAgreementService rentalAgreementService;
    private final CustomerService customerService;
    private final CarService carService;

    private static final Logger logger = LoggerFactory.getLogger(LeaseController.class);


    public LeaseController(RentalAgreementService rentalAgreementService,
                           CustomerService customerService,
                           CarService carService) {
        this.rentalAgreementService = rentalAgreementService;
        this.customerService = customerService;
        this.carService = carService;
    }

    @GetMapping("/lease-agreements")
    public String leaseAgreements(Model model,
                                  @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @GET /lease-agreements", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        model.addAttribute("activePage", "lease-agreements");
        model.addAttribute("rentalAgreementList", rentalAgreementService.getAllRentalAgreements());
        logger.info("User with User id={} with email={} accessed @GET /lease_agreements", currentUser.getId(), currentUser.getEmail());
        return "lease_agreements";
    }

    @GetMapping("/reports")
    public String reports(Model model,
                          @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @GET /reports", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        model.addAttribute("activePage", "reports");
        logger.info("User with User id={} accessed @GET /reports", currentUser.getId());
        return "reports";
    }

    @GetMapping("/lease-create")
    public String leaseCreate(Model model,
                              @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @GET /lease-create", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        model.addAttribute("activePage", "lease-agreements");

        if (!model.containsAttribute("rentalAgreement")) {
            model.addAttribute("rentalAgreement", new RentalAgreement());
            logger.info("No existing rentalAgreement found in model, adding new RentalAgreement for User id={} with email={}", currentUser.getId(), currentUser.getEmail());
        }

        model.addAttribute("customers", customerService.getAllActiveCustomers());
        model.addAttribute("cars", carService.getAvailableCars());

        logger.info("User with User id={} accessed @GET /lease-create", currentUser.getId());
        return "lease_create";
    }

    @PostMapping("/lease-create")
    public String createLease(@ModelAttribute RentalAgreement rentalAgreement,
                              @RequestParam(required = false) String createNewCustomer,
                              @RequestParam(required = false) String newCustomerFirstName,
                              @RequestParam(required = false) String newCustomerLastName,
                              @RequestParam(required = false) String newCustomerEmail,
                              @RequestParam(required = false) String newCustomerPhone,
                              @RequestParam(required = false) String newCustomerLicenceNo,
                              @RequestParam(required = false) String newCustomerStreetAddress,
                              @RequestParam(required = false) Integer newCustomerZipCode,
                              @RequestParam(required = false) String newCustomerCity,
                              @SessionAttribute(name = "currentUser", required = false) User currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @POST /lease-create", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        boolean shouldCreateCustomer = "true".equalsIgnoreCase(createNewCustomer)
                || (newCustomerFirstName != null && !newCustomerFirstName.trim().isEmpty());


        if (!shouldCreateCustomer) {
            model.addAttribute("activePage", "lease-agreements");
            model.addAttribute("errorMessage", "Vælg en eksisterende kunde eller markér 'Opret ny kunde' og udfyld navn");
            model.addAttribute("rentalAgreement", rentalAgreement);
            model.addAttribute("customers", customerService.getAllActiveCustomers());
            model.addAttribute("cars", carService.getAvailableCars());
            return "lease_create";
        }

        Customer newCustomer = null;
        if (shouldCreateCustomer) {
            // Require first and last name for new customer
            if (newCustomerFirstName == null || newCustomerFirstName.trim().isEmpty()
                    || newCustomerLastName == null || newCustomerLastName.trim().isEmpty()) {
                model.addAttribute("activePage", "lease-agreements");
                model.addAttribute("errorMessage", "Fornavn og efternavn skal udfyldes for ny kunde");
                model.addAttribute("rentalAgreement", rentalAgreement);
                model.addAttribute("customers", customerService.getAllActiveCustomers());
                model.addAttribute("cars", carService.getAvailableCars());
                return "lease_create";
            }

            newCustomer = new Customer();
            newCustomer.setFirstName(newCustomerFirstName.trim());
            newCustomer.setLastName(newCustomerLastName.trim());
            newCustomer.setEmail((newCustomerEmail != null && !newCustomerEmail.trim().isEmpty()) ? newCustomerEmail.trim() : null);
            newCustomer.setPhone((newCustomerPhone != null && !newCustomerPhone.trim().isEmpty()) ? newCustomerPhone.trim() : null);
            newCustomer.setLicenceNo((newCustomerLicenceNo != null && !newCustomerLicenceNo.trim().isEmpty()) ? newCustomerLicenceNo.trim() : null);
            newCustomer.setStreetAddress((newCustomerStreetAddress != null && !newCustomerStreetAddress.trim().isEmpty()) ? newCustomerStreetAddress.trim() : null);
            newCustomer.setZipCode(newCustomerZipCode != null ? newCustomerZipCode : 0);
            newCustomer.setCity((newCustomerCity != null && !newCustomerCity.trim().isEmpty()) ? newCustomerCity.trim() : null);
        }

        var validationError = rentalAgreementService.createRentalAgreement(
                rentalAgreement, currentUser.getId(), newCustomer
        );

        if (validationError.isPresent()) {
            model.addAttribute("activePage", "lease-agreements");
            model.addAttribute("errorMessage", validationError.get());
            model.addAttribute("rentalAgreement", rentalAgreement);
            model.addAttribute("customers", customerService.getAllActiveCustomers());
            model.addAttribute("cars", carService.getAvailableCars());
            return "lease_create";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Lejeaftalen blev oprettet");
        logger.info("User with User id={} accessed @POST /lease-create", currentUser.getId());
        logger.info("Lease agreement was created successfully");
        return "redirect:/lease-agreements";
    }

    private String checkAccess(User currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!("DATAREGISTRERING".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
            return "redirect:/access-denied";
        }
        return null;
    }
}