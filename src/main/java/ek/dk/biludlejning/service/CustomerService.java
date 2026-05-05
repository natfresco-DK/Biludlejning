package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.repository.ICustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final ICustomerRepository customerRepository;

    public CustomerService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findAllActive();
    }

    public Customer createCustomer(Customer customer){
        return customerRepository.createCustomer(customer);
    }
}