package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.AuthService;
import ek.dk.biludlejning.service.AuthenticationException;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {
    private final AuthService authService;
    private final CarService carService;
    private final RentalAgreementService rentalAgreementService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, CarService carService, RentalAgreementService rentalAgreementService) {
        this.authService = authService;
        this.carService = carService;
        this.rentalAgreementService = rentalAgreementService;
    }


    @GetMapping("/")
    public String index(@SessionAttribute(name = "currentUser", required = false)User currentUser){
        logger.info("User with User id={} with email={} accessed root", (currentUser != null ? currentUser.getId() : "null"), (currentUser != null ? currentUser.getEmail() : "null"));
        return (currentUser == null) ? "redirect:/login" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @SessionAttribute(name = "currentUser", required = false)User currentUser,
                                Model model) {
        if(currentUser != null){
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Forkert email eller adgangskode");
        }
        return "index";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpServletRequest request,
                          RedirectAttributes attrs) {
        logger.info("Login attempt for email='{}'", email);
        try {
            User user = authService.authenticate(email, password);
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            logger.info("Created session for User id={} with email={} and role={}", user.getId(), user.getEmail(), user.getRole());
            return "redirect:/dashboard";
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email={}: {}", email, e.getMessage());
            if (e.getCause() != null) {
                logger.warn("Cause: {}", e.getCause().getMessage());
            }
            attrs.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@SessionAttribute(name = "currentUser", required = false) User currentUser, Model model){
        logger.info("GET /dashboard with currentUser={}, role={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null");
        if(currentUser == null){
            logger.error("Current User is null");
            return "redirect:/login";
        }
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("activePage", "dashboard");

        int rentedCarsCount = carService.getAllRentedCarsCount();
        model.addAttribute("rentedCarsCount", rentedCarsCount);

        double totalActiveRevenue = rentalAgreementService.getTotalActiveRevenue();
        model.addAttribute("totalActiveRevenue", totalActiveRevenue);


        logger.info("Dashboard login with User id={} with email={}", currentUser.getId(), currentUser.getEmail());
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, @SessionAttribute(name = "currentUser", required = false) User currentUser){
        logger.info("Logout requested by user={}, role={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null");
        HttpSession session = request.getSession(false);
        logger.info("Invalidated session for User id={} with email={}", currentUser.getId(), currentUser.getEmail());
        if (session != null) {
            session.invalidate();
        }
        logger.info("Logged out a User");
        return "redirect:/login";
    }







}
