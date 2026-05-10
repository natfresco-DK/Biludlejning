package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.DamageReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DamageReportRepository implements IDamageReportRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DamageReportRepository.class);

    @Autowired
    public DamageReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DamageReport> damageReportRowMapper = new RowMapper<DamageReport>() {
        @Override
        public DamageReport mapRow(ResultSet rs, int rowNum) throws SQLException {
            DamageReport report = new DamageReport();
            report.setDamgeReportId(rs.getInt("damage_report_id"));
            report.setReturnDate(rs.getDate("return_date").toLocalDate());
            report.setReportDate(rs.getDate("report_date").toLocalDate());
            report.setCost(rs.getDouble("cost"));
            report.setOdometer(rs.getInt("odometer"));
            report.setRentalAgreementId(rs.getInt("agreement_id"));
            report.setRegisteredBy(rs.getInt("registered_by"));
            return report;
        }
    };

    @Override
    public void createDamageReport(DamageReport report) {
        String sql = "INSERT INTO damage_reports (return_date, report_date, cost, odometer, agreement_id, registered_by) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                report.getReturnDate(),
                report.getReportDate(),
                report.getCost(),
                report.getOdometer(),
                report.getRentalAgreementId(),
                report.getRegisteredBy()
        );
        logger.info("Successfully created DamageReport");
    }

    @Override
    public Optional<DamageReport> findByXY(String attribute, Object data) {
        Set<String> allowedAttributes = Set.of(
                "damage_report_id",
                "return_date",
                "report_date",
                "cost",
                "odometer",
                "agreement_id",
                "registered_by"
        );
        if (!allowedAttributes.contains(attribute)) {
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM damage_reports WHERE " + attribute + " = ?";
        try {
            DamageReport report = jdbcTemplate.queryForObject(sql, damageReportRowMapper, data);
            logger.info("Successfully found DamageReport with attribute={} and data={}", attribute,  data);
            return Optional.ofNullable(report);
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No DamageReport found with attribute={} and data={}", attribute,  data);
            return Optional.empty();
        }
    }

    @Override
    public List<DamageReport> getAllDamageReports() {
        String sql = "SELECT * FROM damage_reports";
        logger.info("Successfully fetched all damage reports. Total count: {}", jdbcTemplate.query(sql, damageReportRowMapper).size());
        return jdbcTemplate.query(sql, damageReportRowMapper);
    }

    @Override
    public void update(DamageReport report) {
        String sql = "UPDATE damage_reports SET return_date = ?, report_date = ?, cost = ?, odometer = ?, agreement_id = ?, registered_by = ? WHERE damage_report_id = ?";
        jdbcTemplate.update(sql,
                report.getReturnDate(),
                report.getReportDate(),
                report.getCost(),
                report.getOdometer(),
                report.getRentalAgreementId(),
                report.getRegisteredBy(),
                report.getDamgeReportId()
        );
        logger.info("Successfully updated DamageReport with DamageReportId={}", report.getDamgeReportId());
    }

    @Override
    public void updateCost(int reportId) {
        String sql = "UPDATE damage_reports dr " +
                "SET cost = (SELECT COALESCE(SUM(price), 0) FROM damage_items di WHERE di.damage_report_id = ?) " +
                "WHERE dr.damage_report_id = ?";
        jdbcTemplate.update(sql, reportId, reportId);
        logger.info("Updated DamageReport cost for reportId={}", reportId);
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM damage_reports WHERE damage_report_id = ?";
        logger.info("Successfully deleted DamageReport with id={}", id);
        return jdbcTemplate.update(sql, id);
    }

    public void setCarToMaintenance(DamageReport report) {
        String sql = "UPDATE cars c JOIN rental_agreements ra ON c.car_id = ra.car_id SET c.status = 'MAINTENANCE' WHERE ra.agreement_id = ?";
        logger.info("Successfully set car to MAINTENANCE for DamageReport with RentalAgreementId={}", report.getRentalAgreementId());
        jdbcTemplate.update(sql, report.getRentalAgreementId());
    }

    @Override
    public DamageReport getDamageReportById(int reportId) {
        String sql = "SELECT * FROM damage_reports WHERE damage_report_id = ?";
        logger.info("Successfully fetched DamageReport with id={}. Total count: {}", reportId, jdbcTemplate.query(sql, damageReportRowMapper, reportId).size());
        return jdbcTemplate.queryForObject(sql, damageReportRowMapper, reportId);
    }

}




