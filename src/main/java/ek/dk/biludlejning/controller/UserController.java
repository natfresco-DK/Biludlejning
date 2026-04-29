package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.security.MessageDigest;
import java.util.Base64;

@Controller
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("activePage", "users");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "users";
    }

    @PostMapping("/opret-bruger")
    public String createUser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        user.setRole("Dataregistrering");
        user.setActive(true);

        if ((user.getFirstname().isEmpty()) ||
                (user.getLastname().isEmpty()) ||
                (user.getUsername().isEmpty()) ||
                (user.getPassword().isEmpty()) ||
                (user.getEmail()).isEmpty()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("errorMessage", "Udfyld venligst alle felter");
            model.addAttribute("user", user);
            return "users";
        }

        if (userRepository.findByXY("username", user.getUsername()).isPresent()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("errorMessage", "Brugernavnet findes allerede");
            model.addAttribute("user", user);
            return "users";
        }

        if (userRepository.findByXY("email", user.getEmail()).isPresent()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("errorMessage", "Emailen findes allerede");
            model.addAttribute("user", user);
            return "users";
        }

        System.out.println("password before hash" +  user.getPassword());
        String hashedInput = hashPassword(user.getPassword());
        System.out.println("password after hash" +  hashedInput);
        user.setPassword(hashedInput);
        System.out.println("password after set af hash" +  user.getPassword());


        userRepository.createUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Brugeren blev oprettet");
        System.out.println("Brugeren blev oprettet korrekt");
        return "redirect:/users";
    }

    private String hashPassword(String plainPassword){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

}
