package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model,
                        @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }
        model.addAttribute("activePage", "users");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("roles", ALLOWED_ROLES);
        return "users";
    }

    @PostMapping("/opret-bruger")
    public String createUser(@ModelAttribute User user,
                             @SessionAttribute(name = "currentUser", required = false) User currentUser,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        model.addAttribute("currentUser", currentUser);
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
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
        return "redirect:/users";
    }

    private String checkAccess(User currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!("ADMIN".equals(currentUser.getRole()))) {
            return "redirect:/access-denied";
        }
        return null;
    }
}