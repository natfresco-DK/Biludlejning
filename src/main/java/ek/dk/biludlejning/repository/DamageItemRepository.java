package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.DamageItem;
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
public class DamageItemRepository implements IDamageItemRepository{

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DamageItemRepository.class);

    @Autowired
    public DamageItemRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DamageItem> damageItemRowMapper = new RowMapper<DamageItem>() {
        @Override
        public DamageItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            DamageItem damageItem = new DamageItem();
            damageItem.setDamageId(rs.getInt("damage_id"));
            damageItem.setReportId(rs.getInt("damage_report_id"));
            damageItem.setDescription(rs.getString("description"));
            damageItem.setPrice(rs.getDouble("price"));
            return damageItem;
        }
    };

    @Override
    public void createDamageItem(DamageItem damageItem) {
        String sql = "INSERT INTO damage_items (damage_report_id, description, price) " + //damage_id
                "VALUES (?,?,?)";
        jdbcTemplate.update(sql,
                damageItem.getReportId(),
                damageItem.getDescription(),
                damageItem.getPrice()
        );
        logger.info("Successfully created DamageItem with reportId={}, description={}, price={}", damageItem.getReportId(), damageItem.getDescription(), damageItem.getPrice());
    }

    @Override
    public Optional<DamageItem> findByXY(String attribute, Object data) {
        Set<String> allowedAttributes = Set.of(
                "damage_id",
                "damage_report_id",
                "description",
                "price"
        );
        if (!allowedAttributes.contains(attribute)) {
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM damage_items WHERE " + attribute + " = ?";
        try {
            DamageItem damageItem = jdbcTemplate.queryForObject(sql, damageItemRowMapper, data);
            logger.info("Successfully found DamageItem with attribute={} and data={}", attribute,  data);
            return Optional.ofNullable(damageItem);
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No DamageItem found with attribute={} and data={}", attribute,  data);
            return Optional.empty();
        }
    }

    @Override
    public void update(DamageItem damageItem) {
        String sql = "UPDATE damage_items SET damage_report_id = ?, description = ?, price = ? " +
                "where damage_id = ?";
        jdbcTemplate.update(sql,
                damageItem.getReportId(),
                damageItem.getDescription(),
                damageItem.getDescription(),
                damageItem.getDamageId()
        );
        logger.info("Successfully updated DamageItem with id={}: ", damageItem.getDamageId());
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM damage_items WHERE damage_id = ?";
        logger.info("Successfully deleted DamageItem with id={}", id);
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<DamageItem> getDamageItemsByReportId(int reportId) {
        String sql = "SELECT * FROM damage_items WHERE damage_report_id = ?";
        logger.info("Successfully fetched DamageItems with reportId={}. Total count: {}", reportId, jdbcTemplate.query(sql, damageItemRowMapper, reportId).size());
        return jdbcTemplate.query(sql, damageItemRowMapper, reportId);
    }
}
