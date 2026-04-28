package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Car;
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
public class CarRepository implements ICarRepository {

    private final JdbcTemplate jdbctemplate;

    @Autowired
    public CarRepository(JdbcTemplate jdbctemplate) {
        this.jdbctemplate = jdbctemplate;
    }

    private final RowMapper<Car> carRowMapper = new RowMapper<Car>() {
        @Override
        public Car mapRow(ResultSet rs, int rowNum) throws SQLException {
            Car car = new Car();
            car.setCarId(rs.getInt("car_id"));
            car.setRegNr(rs.getString("reg_nr"));
            car.setVin(rs.getString("vin"));
            car.setBrand(rs.getString("brand"));
            car.setModel(rs.getString("model"));
            car.setLocation(rs.getString("location"));
            car.setOdometer(rs.getInt("odometer"));
            car.setCarDescription(rs.getString("car_description"));
            car.setStatus(rs.getString("status"));
            car.setActive(rs.getBoolean("active"));
            return car;
        }
    };

    @Override
    public void createCar(Car car) {
        String sql = "INSERT INTO cars (reg_nr, vin, brand, model, location, odometer, car_description, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbctemplate.update(sql,
                car.getRegNr(),
                car.getVin(),
                car.getBrand(),
                car.getModel(),
                car.getLocation(),
                car.getOdometer(),
                car.getCarDescription(),
                car.getStatus()
        );
    }

    @Override
    public Optional<Car> findByXY(String attribute, Object data) {
        Set<String> allowedAttributes = Set.of(
                "car_id",
                "reg_nr",
                "vin",
                "brand",
                "model",
                "location",
                "odometer",
                "car_description",
                "status",
                "active"
        );
        if (!allowedAttributes.contains(attribute)) {
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM cars WHERE " + attribute + " = ?";
        try {
            Car car = jdbctemplate.queryForObject(sql, carRowMapper, data);
            return Optional.ofNullable(car);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Car car) {
        String sql = "UPDATE cars SET reg_nr = ?, vin = ?, brand = ?, model = ?, location = ?, odometer = ?, car_description = ?, status = ?, active = ? " +
                "WHERE car_id = ?";
        jdbctemplate.update(sql,
                car.getRegNr(),
                car.getVin(),
                car.getBrand(),
                car.getModel(),
                car.getLocation(),
                car.getOdometer(),
                car.getCarDescription(),
                car.getStatus(),
                car.isActive(),
                car.getCarId()
        );
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM cars WHERE car_id = ?";
        return jdbctemplate.update(sql, id);
    }
}