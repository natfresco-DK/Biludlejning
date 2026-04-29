package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.IUserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final IUserRepository userRepository;
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        userRepository.createUser(user);
    }

    public Optional<String> validateUser(User user) {
        if (userRepository.findByXY("username", user.getUsername()).isPresent()) {
            return Optional.of("Brugernavnet findes allerede");
        }
        if (userRepository.findByXY("email", user.getEmail()).isPresent()) {
            return Optional.of("Emailen findes allerede");
        }
        if (userRepository.findByXY("phone", user.getPhone()).isPresent()) {
            return Optional.of("Telefonnummeret findes allerede");
        }
        return Optional.empty();
    }



}









    /*
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

        if  (userRepository.findByXY("phone", user.getPhone()).isPresent()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("errorMessage", "Telefonnummeret findes allerede");
            model.addAttribute("user", user);
            return "users";
        }


 */


