package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private static final List<String> ALLOWED_ROLES = List.of(
            "ADMIN",
            "DATAREGISTRERING",
            "SKADE/UDBEDRING",
            "FORETNINGSUDVIKLING"
    );

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model,
                        @RequestParam(required = false) Integer userId,
                        @RequestParam(required = false) String username,
                        @RequestParam(required = false) String role,
                        @RequestParam(required = false) String firstName,
                        @RequestParam(required = false) String lastName,
                        @RequestParam(required = false) String phone,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) Boolean active,
                        @SessionAttribute(name = "currentUser", required = false) User currentUser) {

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @GET /users", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        List<User> users = userService.findUsersFiltered(
                userId, username, role, firstName, lastName, phone, email, active
        );

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("active", active);
        model.addAttribute("role", role);
        model.addAttribute("roles", ALLOWED_ROLES);
        model.addAttribute("usersList", users);
        model.addAttribute("activePage", "users");

        logger.info("User with User id={} with email={} accessed @GET /users", currentUser.getId(), currentUser.getEmail());
        return "users";
    }

    @GetMapping("/users-create")
    public String createUserForm(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @GET /users-create", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }

        model.addAttribute("activePage", "users");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("roles", ALLOWED_ROLES);
        logger.info("User with User id={} with email={} accessed @GET /users-create", currentUser.getId(), currentUser.getEmail());
        return "users_create";
    }

    @PostMapping("/opret-bruger")
    public String createUser(@ModelAttribute User user,
                             @SessionAttribute(name = "currentUser", required = false) User currentUser,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access check has been denied for User id={} with email={} at @POST /opret-bruger", currentUser.getId(), currentUser.getEmail());
            return accessCheck;
        }
        Optional<String> validationError = userService.validateUser(user);
        if (validationError.isPresent()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("errorMessage", validationError.get());
            model.addAttribute("user", user);
            model.addAttribute("roles", ALLOWED_ROLES);
            return "users";
        }
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Brugeren blev oprettet");
        logger.info("User created successfully by User id={} with email={}", currentUser.getId(), currentUser.getEmail());
        return "redirect:/users";
    }




    private String checkAccess(User currentUser) {
        logger.info("Access check: currentUser={}, role={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null");

        if (currentUser == null) {
            logger.warn("Access denied because currentUser is null");
            return "redirect:/login";
        }

        String role = currentUser.getRole() != null ? currentUser.getRole().trim() : "";

        if (!"ADMIN".equals(role)) {
            logger.warn("Access denied for user='{}' with role='{}'", currentUser.getEmail(), role);
            return "redirect:/access-denied";
        }

        logger.info("Access granted for user='{}' with role='{}'", currentUser.getEmail(), role);
        return null;
    }
}