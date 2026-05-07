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

    public List<Car> getAllCars() {
        logger.info("Fetching all cars. Total: {}", carRepository.getAllCars().size());
        return carRepository.getAllCars();
    }

    public int getAllRentedCarsCount() {
        logger.info("Fetching all rented cars. Total: {}", carRepository.findAllRentedCars());
        return carRepository.findAllRentedCars();
    }

    public List<Car> getReturnedCars() {
        logger.info("Fetching all returned cars. Total: {}", carRepository.findReturnedCars().size());
        return carRepository.findReturnedCars();
    }

    public void updateCarStatus(int carId, String status) {
        logger.info("Updating car status for carId={} to new status={}", carId, status);
        carRepository.updateCarStatus(carId, status);
    }

    public void addCar(String vin, String regNr, String location, int odometer,
                       String carDescription, String make, String carModel) {
        Car car = new Car(regNr, vin, make, carModel, location, odometer, carDescription, "AVAILABLE");
        carRepository.createCar(car);
        logger.info("Car created successfully: vin={}, make={}, model={}, regNr={}", vin, make, carModel, regNr);
    }
}