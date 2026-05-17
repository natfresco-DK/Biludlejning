package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User mapUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstname(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        logger.info("Successfully fetched all users. Total count: {}", jdbcTemplate.query(sql, this::mapUser).size());
        return jdbcTemplate.query(sql, this::mapUser);
    }

    @Override
    public void createUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (first_name, last_name, username, password, email, phone, role, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive()
        );
        logger.info("Successfully created user with id={}", user.getId());
    }

    @Override
    public Optional<User> findByXY(String attribute, Object data) {
        Set<String> allowedAttributes = Set.of("first_name", "last_name", "username", "email", "phone", "role", "active", "user_id");
        if (!allowedAttributes.contains(attribute)) {
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
        String sql = "SELECT * FROM users WHERE " + attribute + " = ?";
        try {
            logger.info("Successfully found User with attribute={} and data={}", attribute, data);
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    this::mapUser,
                    data
            ));
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No User found with attribute={} and data={}", attribute, data);
            return Optional.empty();
        }
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, phone = ?, role = ?, active = ? WHERE user_id = ?",
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getId()
        );
        logger.info("Successfully updated user");
    }

    @Override
    public int deleteUser(int id) {
        logger.info("Successfully deleted User with id={}", id);
        return jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    public List<User> findUsersFiltered(Integer userId,
                                        String username,
                                        String role,
                                        String firstName,
                                        String lastName,
                                        String phone,
                                        String email,
                                        Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        if (username != null && !username.isBlank()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role.trim());
        }
        if (firstName != null && !firstName.isBlank()) {
            sql.append(" AND first_name LIKE ?");
            params.add("%" + firstName.trim() + "%");
        }
        if (lastName != null && !lastName.isBlank()) {
            sql.append(" AND last_name LIKE ?");
            params.add("%" + lastName.trim() + "%");
        }
        if (phone != null && !phone.isBlank()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
        }
        if (email != null && !email.isBlank()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email.trim() + "%");
        }
        if (active != null) {
            sql.append(" AND active = ?");
            params.add(active);
        }
        logger.info("Filtering users with criteria - userId: {}, username: {}, role: {}, firstName: {}, lastName: {}, phone: {}, email: {}, active: {}",
                userId, username, role, firstName, lastName, phone, email, active);
        return jdbcTemplate.query(sql.toString(), this::mapUser, params.toArray());
    }
}
