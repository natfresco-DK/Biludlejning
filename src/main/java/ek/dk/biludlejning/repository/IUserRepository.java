package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.Optional;

public interface IUserRepository {

    void createUser(User user);

    User mapUser(ResultSet rs, int rowNum) throws Exception;

    Optional<User> findByXY(String attribute, Object data) throws Exception;

    void updateUser(User user);

    int deleteUser(int id);

}
