package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Repository
public class RentalAgreementRepository {


    private final JdbcTemplate jdbcTemplate;

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
        return rentalAgreement;
    }


    public void createRentalAgreement(RentalAgreement rentalAgreement) {
            jdbcTemplate.update(
                "INSERT INTO rental_agreements (agreement_id, start_date, end_date, downpayment, monthly_payment, max_km, created_by, car_id, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                rentalAgreement.getAgreementId(),
                rentalAgreement.getStartDate(),
                rentalAgreement.getEndDate(),
                rentalAgreement.getDownpayment(),
                rentalAgreement.getMonthly_payment(),
                rentalAgreement.getMaxKm(),
                rentalAgreement.getCreatedBy(),
                rentalAgreement.getCar(),
                rentalAgreement.getCustomer()
        );
    }


    public Optional<RentalAgreement> findByXY(String attribute, Object data){
        Set<String> allowedAttributes = Set.of("agreement_id", "start_date", "end_date", "downpayment", "monthly_payment", "max_km", "created_by", "car_id", "customer_id");
        if (!allowedAttributes.contains(attribute)) {
            throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
        String sql = "SELECT * FROM rental_agreements WHERE " + attribute + " = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    sql,
                    this::mapRentalAgreement,
                    data
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateRentalAgreement(RentalAgreement rentalAgreement) {
        jdbcTemplate.update(
                "UPDATE rental_agreements SET start_date = ?, end_date = ?, downpayment = ?, monthly_payment = ?, max_km = ?, created_by = ?, car_id = ?, customer_id = ? WHERE agreement_id = ?",
                rentalAgreement.getStartDate(),
                rentalAgreement.getEndDate(),
                rentalAgreement.getDownpayment(),
                rentalAgreement.getMonthly_payment(),
                rentalAgreement.getMaxKm(),
                rentalAgreement.getCreatedBy(),
                rentalAgreement.getCar(),
                rentalAgreement.getCustomer()
        );
    }

    public int deleteRentalAgreementById(int id) {
        return jdbcTemplate.update("DELETE FROM rental_agreements WHERE created_by = ?", id);
    }

}
