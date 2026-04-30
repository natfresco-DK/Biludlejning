package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
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

    public CarController(CarService carService, RentalAgreementService rentalAgreementService) {
        this.carService = carService;
        this.rentalAgreementService = rentalAgreementService;
    }

    @GetMapping("/cars")
    public  String cars(Model model,
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
        return "cars";
    }

    @PostMapping("/car-return/{carId}")
    public String returnCar(@PathVariable int carId) {
        rentalAgreementService.setCarAsReturned(carId);
        return "redirect:/cars";
    }

}
