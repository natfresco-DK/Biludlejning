package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CustomerRepository implements ICustomerRepository {


    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CustomerRepository.class);

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

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, new String[]{"customer_id"});
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getLicenceNo());
            ps.setString(6, customer.getStreetAddress());
            ps.setInt(7, customer.getZipCode());
            ps.setString(8, customer.getCity());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new IllegalStateException("Could not retrieve generated customer ID");
        }

        customer.setCustomerId(generatedId.intValue());

        logger.info("Successfully created customer with id={}", customer.getCustomerId());
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
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM customers WHERE " + attribute + " = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, data);
            logger.info("Successfully found customer with attribute={} and data={}", attribute, data);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No customer found with attribute={} and data={}", attribute, data);
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
        logger.info("Successfully updated customer with id={}: ", customer.getCustomerId());
    }

    @Override
    public List<Customer> findAllActive() {
        String sql = "SELECT * FROM customers WHERE active = true";
        logger.info("Successfully fetched all active customers. Total count: {}", jdbcTemplate.query(sql, customerRowMapper).size());
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        logger.info("Successfully deleted customer with id={}", id);
        return jdbcTemplate.update(sql, id);
    }
}
