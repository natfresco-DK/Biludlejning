package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Customer;

import java.util.List;
import java.util.Optional;
public interface ICustomerRepository {
    //create a car in the table
    void createCustomer(Customer customer);

    //find customer by X(data) and y(attribute) in table
    Optional<Customer> findByXY(String attribute, Object data);

    //update existing car in table
    void update(Customer customer);

    List<Customer> findAllActive();

    //delete customer by ID in table
    int deleteById(int id);
}
