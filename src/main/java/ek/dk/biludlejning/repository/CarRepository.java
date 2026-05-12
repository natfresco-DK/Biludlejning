package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CarRepository implements ICarRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CarRepository.class);

    @Autowired
    public CarRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        jdbcTemplate.update(sql,
                car.getRegNr(),
                car.getVin(),
                car.getBrand(),
                car.getModel(),
                car.getLocation(),
                car.getOdometer(),
                car.getCarDescription(),
                car.getStatus()
        );
        logger.info("Successfully created car with id={}: ", car.getCarId());
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
            logger.error("Attribute not allowed: {}", attribute);
            throw new IllegalArgumentException("Invalid Attribute: " + attribute);
        }
        String sql = "SELECT * FROM cars WHERE " + attribute + " = ?";
        try {
            Car car = jdbcTemplate.queryForObject(sql, carRowMapper, data);
            logger.debug("Successfully found car with attribute={} and data={}", attribute, data);
            return Optional.ofNullable(car);
        } catch (EmptyResultDataAccessException e) {
            logger.error("EmptyResultDataAccessException: No car found with attribute={} and data={}", attribute, data);
            return Optional.empty();
        }
    }

    @Override
    public void update(Car car) {
        String sql = "UPDATE cars SET reg_nr = ?, vin = ?, brand = ?, model = ?, location = ?, odometer = ?, car_description = ?, status = ?, active = ? " +
                "WHERE car_id = ?";
        jdbcTemplate.update(sql,
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
        logger.info("Successfully updated car with id={}: ", car.getCarId());
    }

    @Override
    public List<Car> findAvailableCars() {
        String sql = "SELECT * FROM cars WHERE active = true AND UPPER(status) = ?";
        logger.info("Successfully fetched all available cars. Total count: {}", jdbcTemplate.query(sql, carRowMapper, "AVAILABLE").size());
        return jdbcTemplate.query(sql, carRowMapper,
                "AVAILABLE");

    }

    @Override
    public List<Car> getAllCars() {
        String sql = "SELECT * FROM cars";
        logger.info("Successfully fetched all cars. Total count: {}", jdbcTemplate.query(sql, carRowMapper).size());
        return jdbcTemplate.query(sql, carRowMapper);
    }

    @Override
    public List<Car> findReturnedCars() {
        String sql = "SELECT * FROM cars WHERE active = true AND UPPER(status) = ?";
        logger.info("Successfully fetched all returned cars. Total count: {}", jdbcTemplate.query(sql, carRowMapper, "RETURNED").size());
        return jdbcTemplate.query(sql, carRowMapper, "RETURNED");
    }

    @Override
    public int findAllRentedCars() {
        String sql = "SELECT COUNT(*) FROM cars WHERE UPPER(status) = ?";
        logger.info("Successfully fetched all rented cars. Total={}", jdbcTemplate.queryForObject(sql, Integer.class, "RENTED"));
        return jdbcTemplate.queryForObject(sql, Integer.class, "RENTED");
    }

    @Override
    public List<Car> findCarsFiltered(Integer carId,
                                      String regNr,
                                      String vin,
                                      String brand,
                                      String carModel,
                                      String location,
                                      Integer odometerMin,
                                      Integer odometerMax,
                                      String carDescription,
                                      String status,
                                      Boolean active) {

        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (carId != null) {
            sql.append(" AND car_id = ?");
            params.add(carId);
        }
        if (regNr != null && !regNr.isBlank()) {
            sql.append(" AND UPPER(reg_nr) LIKE UPPER(?)");
            params.add("%" + regNr.trim() + "%");
        }
        if (vin != null && !vin.isBlank()) {
            sql.append(" AND UPPER(vin) LIKE UPPER(?)");
            params.add("%" + vin.trim() + "%");
        }
        if (brand != null && !brand.isBlank()) {
            sql.append(" AND UPPER(brand) LIKE UPPER(?)");
            params.add("%" + brand.trim() + "%");
        }
        if (carModel != null && !carModel.isBlank()) {
            sql.append(" AND UPPER(model) LIKE UPPER(?)");
            params.add("%" + carModel.trim() + "%");
        }
        if (location != null && !location.isBlank()) {
            sql.append(" AND UPPER(location) LIKE UPPER(?)");
            params.add("%" + location.trim() + "%");
        }
        if (odometerMin != null) {
            sql.append(" AND odometer >= ?");
            params.add(odometerMin);
        }
        if (odometerMax != null) {
            sql.append(" AND odometer <= ?");
            params.add(odometerMax);
        }
        if (carDescription != null && !carDescription.isBlank()) {
            sql.append(" AND UPPER(car_description) LIKE UPPER(?)");
            params.add("%" + carDescription.trim() + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND UPPER(status) = UPPER(?)");
            params.add(status.trim());
        }
        if (active != null) {
            sql.append(" AND active = ?");
            params.add(active);
        }

        logger.debug("Filtering cars with SQL: {}", sql);
        logger.debug("Parameters: {}", params);

        return jdbcTemplate.query(sql.toString(), carRowMapper, params.toArray());
    }

    public void updateCarStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ? WHERE car_id = ?";
        logger.info("Successfully updated car with id={} with new status={}:", carId, status);
        jdbcTemplate.update(sql, status, carId);
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM cars WHERE car_id = ?";
        logger.info("Successfully deleted car with id={}", id);
        return jdbcTemplate.update(sql, id);
    }
}