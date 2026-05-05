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


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public Customer createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, licence_no, street_address, zip_code, city) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getLicenceNo(),
                customer.getStreetAddress(),
                customer.getZipCode(),
                customer.getCity()
        );

        String querySql = "SELECT customer_id FROM customers WHERE first_name = ? AND last_name = ? ORDER BY customer_id DESC LIMIT 1";
        Integer customerId = jdbcTemplate.queryForObject(querySql, Integer.class,
                customer.getFirstName(),
                customer.getLastName());


        customer.setCustomerId(customerId);


        return customer;
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
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, data);
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
        jdbcTemplate.update(sql,
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
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
