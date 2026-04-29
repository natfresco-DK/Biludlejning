package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CustomerRepository implements ICustomerRepository {


    private final JdbcTemplate jdbctemplate;

    @Autowired
    public CustomerRepository(JdbcTemplate jdbctemplate) {
        this.jdbctemplate = jdbctemplate;
    }

    private final RowMapper<Customer> customerRowMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("customer_id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            customer.setLicenceNo(rs.getString("licence_no"));
            customer.setStreetAddress(rs.getString("street_address"));
            customer.setZipCode(rs.getInt("zip_code"));
            customer.setCity(rs.getString("city"));
            customer.setActive(rs.getBoolean("active"));
            return customer;
        }
    };

    @Override
    public void createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, licence_no, street_address, zip_code, city) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbctemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getLicenceNo(),
                customer.getStreetAddress(),
                customer.getZipCode(),
                customer.getCity()
        );
    }

    @Override
    public Optional<Customer> findByXY(String attribute, Object data) {
        Set<String> allowedAttributes = Set.of(
                "customer_id",
                "first_name",
                "last_name",
                "email",
                "phone",
                "licence_no",
                "street_address",
                "zip_code",
                "city",
                "active"
        );
        if (!allowedAttributes.contains(attribute)) {
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM customers WHERE " + attribute + " = ?";
        try {
            Customer customer = jdbctemplate.queryForObject(sql, customerRowMapper, data);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE customers SET " +
                "first_name = ?, " +
                "last_name = ?, " +
                "email = ?, " +
                "phone = ?, " +
                "licence_no = ?, " +
                "street_address = ?, " +
                "zip_code = ?, " +
                "city = ?, " +
                "active = ? " +
                "WHERE customer_id = ?";
        jdbctemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getLicenceNo(),
                customer.getStreetAddress(),
                customer.getZipCode(),
                customer.getCity(),
                customer.isActive(),
                customer.getCustomerId()
        );
    }

    @Override
    public List<Customer> findAllActive() {
        String sql = "SELECT * FROM customers WHERE active = true";
        return jdbctemplate.query(sql, customerRowMapper);
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        return jdbctemplate.update(sql, id);
    }
}
