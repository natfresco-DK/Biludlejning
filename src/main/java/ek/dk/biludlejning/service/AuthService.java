package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final IUserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            logger.error("Missing email or password during authentication attempt");
            throw new AuthenticationException("Email og password er påkrævet");
        }
        String trimmedEmail = email.trim();
        try {
            Optional<User> opt = userRepository.findByXY("email", trimmedEmail);
            if (opt.isEmpty()) {
                logger.error("Invalid email during authentication attempt: email='{}'", trimmedEmail);
                throw new AuthenticationException("Ugyldig email");
            }
            User user = opt.get();
            logger.info("Successfully found user with id='{}', starts comparing passwords", user.getId());
            String hashedInput = hashPassword(password);
            logger.debug("Hashed password: '{}'", hashedInput);
            logger.debug("Plain password: '{}'", password);
            logger.debug("Matching results: stored='{}', input='{}'", user.getPassword(), hashedInput);

            //checks if password and hashedpassword matches
            if (!user.getPassword().equals(hashedInput)) {
                logger.error("Invalid password during authentication attempt: email='{}'", trimmedEmail);
                throw new AuthenticationException("Ugyldig password");
            }
            logger.info("Successfully found User with user id='{}', authentication successful", user.getId());
            return user;
        } catch (Exception e) {
            logger.error("Exception during authentication attempt for email='{}'. No user found: {}", trimmedEmail, e.getMessage());
            throw new AuthenticationException("Database fejl", e);
        }
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
