package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.DamageReport;
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
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM damage_reports WHERE " + attribute + " = ?";
        try {
            DamageReport report = jdbcTemplate.queryForObject(sql, damageReportRowMapper, data);
            return Optional.ofNullable(report);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<DamageReport> getAllDamageReports() {
        String sql = "SELECT * FROM damage_reports";
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
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM damage_reports WHERE damage_report_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void setCarToMaintenance(DamageReport report) {
        String sql = "UPDATE cars c JOIN rental_agreements ra ON c.car_id = ra.car_id SET c.status = 'MAINTENANCE' WHERE ra.agreement_id = ?";
        jdbcTemplate.update(sql, report.getRentalAgreementId());
    }

}




