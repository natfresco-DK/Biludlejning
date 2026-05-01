package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.User;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    void createUser(User user);

    User mapUser(ResultSet rs, int rowNum) throws Exception;

    Optional<User> findByXY(String attribute, Object data);

    void updateUser(User user);

    int deleteUser(int id);

    List<User> getAllUsers();
}
