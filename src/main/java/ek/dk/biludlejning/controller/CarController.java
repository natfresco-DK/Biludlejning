package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarController {

    private final CarService carService;
    private final RentalAgreementService rentalAgreementService;

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    public CarController(CarService carService, RentalAgreementService rentalAgreementService) {
        this.carService = carService;
        this.rentalAgreementService = rentalAgreementService;
    }

    @GetMapping("/cars")
    public String cars(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        Map<Integer, Boolean> leaseCompletedMap =  new HashMap<>();
        for (Car car : cars) {
            leaseCompletedMap.put(car.getCarId(),
                    rentalAgreementService.isLeaseCompleted(car.getCarId()));
        }
        model.addAttribute("leaseCompletedMap",leaseCompletedMap);
        model.addAttribute("activePage", "cars");
        logger.info("User with User id={} with email={} accessed /cars", (currentUser != null ? currentUser.getId() : "null"), (currentUser != null ? currentUser.getEmail() : "null"));
        return "cars";
    }

    @PostMapping("/car-return/{carId}")
    public String returnCar(@PathVariable int carId, @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        rentalAgreementService.setCarAsReturned(carId);
        logger.info("Car with id={} has been marked as returned by User id={}", carId, currentUser.getId());
        return "redirect:/cars";
    }

}
