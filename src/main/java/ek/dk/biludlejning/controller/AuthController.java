package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.AuthService;
import ek.dk.biludlejning.service.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/")
    public String index(@SessionAttribute(name = "currentUser", required = false)User currentUser){
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
        try {
            User user = authService.authenticate(email, password);
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            System.out.println("AuthController: Created session for User id=" + user.getId());
            return "redirect:/dashboard";
        } catch (AuthenticationException e) {
            System.out.println("AuthController: Authentication failed: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("AuthController: Cause: " + e.getCause().getMessage());
            }
            attrs.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@SessionAttribute(name = "currentUser", required = false) User currentUser, Model model){
        if(currentUser == null){
            return "redirect:/login";
        }
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }








}
