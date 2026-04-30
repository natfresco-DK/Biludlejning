package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.repository.ICarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final ICarRepository carRepository;

    public CarService(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAvailableCars() {
        return carRepository.findAvailableCars();
    }
    public void setCarAsRented(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            throw new IllegalArgumentException("Bilen er ikke tilgængelig");
        }

        car.setStatus("RENTED");
        carRepository.update(car);
    }

    public int getAllRentedCarsCount() {
        return carRepository.findAllRentedCars();
    }

    public List<Car> getReturnedCars() {
        return carRepository.findReturnedCars();
    }


}