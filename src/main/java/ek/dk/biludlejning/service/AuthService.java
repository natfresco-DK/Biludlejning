package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String password) {
        System.out.println("\n\n========start authentication==============\n\n");
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            System.out.println("AuthService: Missing email or password");
            throw new AuthenticationException("Email og password er påkrævet");
        }
        String trimmedEmail = email.trim();
        System.out.println("AuthService: Authenticate called for email='" + trimmedEmail + "'");
        try {
            Optional<User> opt = userRepository.findByXY("email", trimmedEmail);
            if (opt.isEmpty()) {
                System.out.println("AuthService: No user found with email='" + trimmedEmail + "'");
                throw new AuthenticationException("Ugyldig email");
            }
            User user = opt.get();
            System.out.println("AuthService: Found user with id=" + user.getId() + ", comparing passwords");
            String hashedInput = hashPassword(password);
            System.out.println("DEBUG: Stored hash = " + user.getPassword());
            System.out.println("DEBUG: Plain password = " + password);
            System.out.println("DEBUG: Match result = " + user.getPassword().equals(hashedInput));

            System.out.println("AuthService: Found user with id=" + user.getId() + ", comparing passwords");
            //checks if password and hashedpassword matches
            if (!user.getPassword().equals(hashedInput)) {
                System.out.println("AuthService: Password mismatch for email='" + trimmedEmail + "'");
                throw new AuthenticationException("Ugyldig password");
            }
            System.out.println("AuthService: Authentication successful for user id=" + user.getId());
            return user;
        } catch (Exception e) {
            System.out.println("AuthService: Error while finding user: " + e.getMessage());
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
