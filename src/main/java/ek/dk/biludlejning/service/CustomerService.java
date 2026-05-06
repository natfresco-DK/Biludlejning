package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.repository.ICustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final ICustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllActiveCustomers() {
        logger.info("Fetching all active customers. Total: {}", customerRepository.findAllActive().size());
        return customerRepository.findAllActive();
    }
}