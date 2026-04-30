package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUserRepository userRepository;

    private static final List<String> ALLOWED_ROLES = List.of(
            "ADMIN",
            "DATAREGISTRERING",
            "SKADE/UDBEDRING",
            "FORETNINGSUDVIKLING"
    );

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        user.setActive(true);
        user.setPassword(hashPassword(user.getPassword()));
        userRepository.createUser(user);
    }

    public Optional<String> validateUser(User user) {
        if (isBlank(user.getFirstname()) ||
                isBlank(user.getLastname()) ||
                isBlank(user.getUsername()) ||
                isBlank(user.getPassword()) ||
                isBlank(user.getPhone()) ||
                isBlank(user.getEmail()) ||
                isBlank(user.getRole())) {
            return Optional.of("Udfyld venligst alle felter");
        }
        if (!ALLOWED_ROLES.contains(user.getRole())) {
            return Optional.of("Ugyldig rolle valgt");
        }
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

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}