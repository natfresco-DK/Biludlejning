package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.DamageItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Repository
public class DamageItemRepository implements IDamageItemRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DamageItemRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DamageItem> damageItemRowMapper = new RowMapper<DamageItem>() {
        @Override
        public DamageItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            DamageItem damageItem = new DamageItem();
            damageItem.setDamageId(rs.getInt("damage_id"));
            damageItem.setDamageId(rs.getInt("damage_report_id"));
            damageItem.setDescription(rs.getString("description"));
            damageItem.setPrice(rs.getDouble("price"));
            return damageItem;
        }
    };

    @Override
    public void createCar(DamageItem damageItem) {
        String sql = "INSERT INTO damage_items (damage_id, damage_report_id, description, price) " +
                "VALUES (?,?,?,?)";
        jdbcTemplate.update(sql,
                damageItem.getReportId(),
                damageItem.getDescription(),
                damageItem.getPrice()
        );
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
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM damage_items WHERE " + attribute + " = ?";
        try {
            DamageItem damageItem = jdbcTemplate.queryForObject(sql, damageItemRowMapper, data);
            return Optional.ofNullable(damageItem);
        } catch (EmptyResultDataAccessException e) {
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
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM damage_items WHERE damage_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
