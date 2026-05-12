package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.repository.ICarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final ICarRepository carRepository;

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    public CarService(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAvailableCars() {
        logger.info("Fetching available cars. Total: {}", carRepository.findAvailableCars().size());
        return carRepository.findAvailableCars();
    }

    public int getAllRentedCarsCount() {
        int rentedCarsCount = carRepository.findAllRentedCars();
        logger.info("Fetching all rented cars. Total: {}", rentedCarsCount);
        return rentedCarsCount;
    }

    public void addCar(String vin, String regNr, String location, int odometer,
                       String carDescription, String make, String carModel) {
        Car car = new Car(regNr, vin, make, carModel, location, odometer, carDescription, "AVAILABLE");
        carRepository.createCar(car);
        logger.info("Car created successfully: vin={}, make={}, model={}, regNr={}", vin, make, carModel, regNr);
    }

    public List<Car> findCarsFiltered(Integer carId,
                                      String regNr,
                                      String vin,
                                      String brand,
                                      String carModel,
                                      String location,
                                      Integer odometer,
                                      String carDescription,
                                      String status,
                                      Boolean active) {
        logger.info("Filtering cars with criteria - carId: {}, regNr: {}, vin: {}, brand: {}, model: {}, location: {}, odometer: {}, description: {}, status: {}, active: {}",
                carId, regNr, vin, brand, carModel, location, odometer, carDescription, status, active);
        return carRepository.findCarsFiltered(carId, regNr, vin, brand, carModel, location, odometer, carDescription, status, active);
    }
}