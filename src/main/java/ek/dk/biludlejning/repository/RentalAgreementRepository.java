package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.RentalAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class RentalAgreementRepository implements IRentalAgreementRepository{


    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RentalAgreementRepository.class);

    public RentalAgreementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RentalAgreement mapRentalAgreement(ResultSet rs, int rowNum) throws SQLException {
        RentalAgreement rentalAgreement = new RentalAgreement();
        rentalAgreement.setAgreementId(rs.getInt("agreement_id"));
        rentalAgreement.setStartDate(rs.getDate("start_date").toLocalDate());
        rentalAgreement.setEndDate(rs.getDate("end_date").toLocalDate());
        rentalAgreement.setDownpayment(rs.getInt("downpayment"));
        rentalAgreement.setMonthly_payment(rs.getInt("monthly_payment"));
        rentalAgreement.setMaxKm(rs.getInt("max_km"));
        rentalAgreement.setCreatedBy(rs.getInt("created_by"));
        rentalAgreement.setCar(rs.getInt("car_id"));
        rentalAgreement.setCustomer(rs.getInt("customer_id"));
        rentalAgreement.setActive(rs.getBoolean("active"));
        return rentalAgreement;
    }

    public void createRentalAgreement(RentalAgreement rentalAgreement) {
            jdbcTemplate.update(
                "INSERT INTO rental_agreements (start_date, end_date, downpayment, monthly_payment, max_km, created_by, car_id, customer_id, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                rentalAgreement.getStartDate(),
                rentalAgreement.getEndDate(),
                rentalAgreement.getDownpayment(),
                rentalAgreement.getMonthly_payment(),
                rentalAgreement.getMaxKm(),
                rentalAgreement.getCreatedBy(),
                rentalAgreement.getCar(),
                rentalAgreement.getCustomer(),
                    rentalAgreement.getActive()
        );
        logger.info("Successfully created RentalAgreement");
    }

    //finders
    public Optional<RentalAgreement> findByXY(String attribute, Object data){
        Set<String> allowedAttributes = Set.of("agreement_id", "start_date", "end_date", "downpayment", "monthly_payment", "max_km", "created_by", "car_id", "customer_id","active");
        if (!allowedAttributes.contains(attribute)) {
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
        String sql = "SELECT * FROM rental_agreements WHERE " + attribute + " = ?";
        try {
            logger.info("Successfully found RentalAgreement with attribute={} and data={}", attribute,  data);
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    this::mapRentalAgreement,
                    data
            ));
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No RentalAgreement found with attribute={} and data={}", attribute,  data);
            return Optional.empty();
        }
    }

    public List<RentalAgreement> getAllRentalAgreements(){
        String sql = "SELECT * FROM rental_agreements";
        logger.info("Successfully fetched all RentalAgreements. Total count: {}", jdbcTemplate.query(sql, this::mapRentalAgreement).size());
        return jdbcTemplate.query(sql, this::mapRentalAgreement);
    }

    @Override
    public List<RentalAgreement> getFilteredRentalAgreements(Integer agreementId,
                                                             Integer customerId,
                                                             Integer carId,
                                                             LocalDate startDate,
                                                             LocalDate endDate,
                                                             Double downpayment,
                                                             Double monthlyPayment,
                                                             Integer maxKm,
                                                             Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT * FROM rental_agreements WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (agreementId != null) {
            sql.append(" AND agreement_id = ?");
            params.add(agreementId);
        }
        if (customerId != null) {
            sql.append(" AND customer_id = ?");
            params.add(customerId);
        }
        if (carId != null) {
            sql.append(" AND car_id = ?");
            params.add(carId);
        }
        if (startDate != null) {
            sql.append(" AND start_date = ?");
            params.add(java.sql.Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append(" AND end_date = ?");
            params.add(java.sql.Date.valueOf(endDate));
        }
        if (downpayment != null) {
            sql.append(" AND downpayment = ?");
            params.add(downpayment);
        }
        if (monthlyPayment != null) {
            sql.append(" AND monthly_payment = ?");
            params.add(monthlyPayment);
        }
        if (maxKm != null) {
            sql.append(" AND max_km = ?");
            params.add(maxKm);
        }
        if (active != null) {
            sql.append(" AND active = ?");
            params.add(active);
        }

        logger.info("Executing filtered rental agreements query: {}", sql);
        return jdbcTemplate.query(sql.toString(), this::mapRentalAgreement, params.toArray());
    }

    public List<RentalAgreement> findByCarId(int carId) {
        String sql = "SELECT * FROM rental_agreements WHERE car_id = ?";
        logger.info("Successfully fetched car from carId={}", carId);
        return jdbcTemplate.query(sql, this::mapRentalAgreement, carId);
    }

    //updater
    public void updateRentalAgreement(RentalAgreement rentalAgreement) {
        jdbcTemplate.update(
                "UPDATE rental_agreements SET " +
                        "start_date = ?, " +
                        "end_date = ?, " +
                        "downpayment = ?, " +
                        "monthly_payment = ?, " +
                        "max_km = ?, " +
                        "created_by = ?, " +
                        "car_id = ?, " +
                        "customer_id = ?, " +
                        "active = ? " +
                        "WHERE agreement_id = ?",
                rentalAgreement.getStartDate(),
                rentalAgreement.getEndDate(),
                rentalAgreement.getDownpayment(),
                rentalAgreement.getMonthly_payment(),
                rentalAgreement.getMaxKm(),
                rentalAgreement.getCreatedBy(),
                rentalAgreement.getCar(),
                rentalAgreement.getCustomer(),
                rentalAgreement.getActive(),
                rentalAgreement.getAgreementId()
        );
        logger.info("Successfully updated RentalAgreement");
    }

    //deleter
    public int deleteRentalAgreementById(int id) {
        logger.info("Successfully deleted RentalAgreement with id={}", id);
        return jdbcTemplate.update("DELETE FROM rental_agreements WHERE agreement_id = ?", id);
    }

    public double getTotalActiveRevenue() {
        String sql = "SELECT COALESCE(SUM(downpayment + (monthly_payment * TIMESTAMPDIFF(MONTH, start_date, end_date))),0) FROM rental_agreements WHERE active = TRUE";
        logger.info("Successfully fetched total active revenue: Total={}", jdbcTemplate.queryForObject(sql, Double.class));
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    @Override
    public List<RentalAgreement> getReturnedRentalAgreements() {
        String sql = "SELECT ra.* FROM rental_agreements ra JOIN cars c ON ra.car_id = c.car_id WHERE c.status = 'RETURNED'";
        logger.info("Sucessfully fetched all returned RentalAgreements. Total count: {}", jdbcTemplate.query(sql, this::mapRentalAgreement).size());
        return jdbcTemplate.query(sql, this::mapRentalAgreement);
    }
}
