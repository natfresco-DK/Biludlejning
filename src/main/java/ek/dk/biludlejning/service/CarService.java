package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.ICarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final ICarRepository carRepository;

    public CarService(ICarRepository carRepository, RentalAgreementService rentalAgreementService) {
        this.carRepository = carRepository;
    }

    public List<Car> getAvailableCars() {
        return carRepository.findAvailableCars();
    }

    public List<Car> getAllCars(){
        return carRepository.getAllCars();
    }

    public int getAllRentedCarsCount() {
        return carRepository.findAllRentedCars();
    }
}