package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User mapUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstname(rs.getString("first_name"));
        user.setLastname(rs.getString("last_name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }

    @Override
    public void createUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (first_name, last_name, username, password, email, phone, role, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive()
        );
    }

    @Override
    public Optional<User> findByXY(String attribute, Object data){
        Set<String> allowedAttributes = Set.of("first_name", "last_name", "username", "email", "phone", "role", "active", "user_id");
        if (!allowedAttributes.contains(attribute)) {
            throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
        String sql = "SELECT * FROM users WHERE " + attribute + " = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    this::mapUser,
                    data
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, phone = ?, role = ?, active = ? WHERE user_id = ?",
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getId()
        );
    }

    @Override
    public void deleteUser(int id) {
        try {
            jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
        } catch (Exception e) {
            System.out.println("Error deleting user_id " + id + ": " + e.getMessage());
        }
    }
}
