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

    public CarService(ICarRepository carRepository, RentalAgreementService rentalAgreementService) {
        this.carRepository = carRepository;
    }

    public List<Car> getAvailableCars() {
        logger.info("Fetching available cars. Total: {}", carRepository.findAvailableCars().size());
        return carRepository.findAvailableCars();
    }

    public void setCarAsRented(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));
        logger.info("Setting Car as Rented: {}", car);

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            logger.error("Car with id={} is not available for rent. Current status: {}", carId, car.getStatus());
            throw new IllegalArgumentException("Bilen er ikke tilgængelig");
        }
        car.setStatus("RENTED");
        carRepository.update(car);
        logger.info("Successfully set Car as Rented: {}", car);
    }
    public List<Car> getAllCars(){
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



}