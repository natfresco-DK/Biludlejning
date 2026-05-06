package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers(){
        List<User> allUsers = userRepository.getAllUsers();
        logger.info("Fetching all users. Total: {}", allUsers.size());
        return allUsers;
    }

    public void createUser(User user) {
        user.setActive(true);
        user.setPassword(hashPassword(user.getPassword()));
        userRepository.createUser(user);
        logger.info("Successfully created user with userID: {}", user.getId());
    }

    public Optional<String> validateUser(User user) {
        if (isBlank(user.getFirstname()) ||
                isBlank(user.getLastname()) ||
                isBlank(user.getUsername()) ||
                isBlank(user.getPassword()) ||
                isBlank(user.getPhone()) ||
                isBlank(user.getEmail()) ||
                isBlank(user.getRole())) {
            logger.error("Validation failed for user creation due to missing fields: {}", user);
            return Optional.of("Udfyld venligst alle felter");
        }
        if (!ALLOWED_ROLES.contains(user.getRole())) {
            logger.error("Validation failed for user creation due to invalid role: '{}'", user.getRole());
            return Optional.of("Ugyldig rolle valgt");
        }
        if (userRepository.findByXY("username", user.getUsername()).isPresent()) {
            logger.error("Validation failed for user creation due to username already exists: '{}'", user.getUsername());
            return Optional.of("Brugernavnet findes allerede");
        }
        if (userRepository.findByXY("email", user.getEmail()).isPresent()) {
            logger.error("Validation failed for user creation due to email already exists: '{}'", user.getEmail());
            return Optional.of("Emailen findes allerede");
        }
        if (userRepository.findByXY("phone", user.getPhone()).isPresent()) {
            logger.error("Validation failed for user creation due to phone number already exists: '{}'", user.getPhone());
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
            logger.error("Exception: Error hashing password: {}", e.getMessage());
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}