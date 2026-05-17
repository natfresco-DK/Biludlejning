package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.model.VehicleInfoDto;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
import ek.dk.biludlejning.service.VinParsingService;
import ek.dk.biludlejning.service.VinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarController {

    private final CarService carService;
    private final RentalAgreementService rentalAgreementService;
    private final VinService vinService;
    private final VinParsingService vinParsingService;

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    public CarController(CarService carService, RentalAgreementService rentalAgreementService, VinService vinService, VinParsingService vinParsingService) {
        this.carService = carService;
        this.rentalAgreementService = rentalAgreementService;
        this.vinService = vinService;
        this.vinParsingService = vinParsingService;
    }

    @GetMapping("/cars")
    public String cars(Model model,
                       @RequestParam(required = false) Integer carId,
                       @RequestParam(required = false) String regNr,
                       @RequestParam(required = false) String vin,
                       @RequestParam(required = false) String brand,
                       @RequestParam(required = false) String carModel,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) Integer odometerMin,
                       @RequestParam(required = false) Integer odometerMax,
                       @RequestParam(required = false) String carDescription,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) Boolean active,
                       @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }

        List<Car> cars = carService.findCarsFiltered(
                carId, regNr, vin, brand, carModel, location,
                odometerMin, odometerMax, carDescription, status, active
        );
        Map<Integer, Boolean> leaseCompletedMap = new HashMap<>();
        for (Car car : cars) {
            leaseCompletedMap.put(car.getCarId(),
                    rentalAgreementService.isLeaseCompleted(car.getCarId()));
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("status", status);
        model.addAttribute("active", active);
        model.addAttribute("cars", cars);
        model.addAttribute("leaseCompletedMap", leaseCompletedMap);
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

    @GetMapping("/cars/add")
    public String showAddCarForm(Model model,
                                 @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        logger.info("GET /cars/add requested. currentUser={}, role={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null");
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            logger.warn("Access denied for GET /cars/add. currentUser={}, role={}",
                    currentUser != null ? currentUser.getEmail() : "null",
                    currentUser != null ? currentUser.getRole() : "null");
            return accessCheck;
        }

        model.addAttribute("currentUser", currentUser);

        model.addAttribute("step", 1);

        logger.info("Rendering cars_add_car with step=1");
        return "cars_add_car";
    }

    // Step 1: Decode VIN and show results
    @PostMapping("/car/decode")
    public String decodeVin(@RequestParam String vin, Model model,
                            @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }

        model.addAttribute("currentUser", currentUser);

        try {
            String rawResponse = vinService.getVehicleInfo(vin);
            VehicleInfoDto info = vinParsingService.parseVehicleInfo(rawResponse);

            if (info.getMake() == null || info.getModel() == null) {
                model.addAttribute("error", "Kunne ikke finde mærke eller model for denne VIN");
                model.addAttribute("step", 1);
                model.addAttribute("vin", vin);
                return "cars_add_car";
            }

            model.addAttribute("step", 2);
            model.addAttribute("vin", vin);
            model.addAttribute("make", info.getMake());
            model.addAttribute("model", info.getModel());
            model.addAttribute("modelYear", info.getModelYear());

            return "cars_add_car";

        } catch (Exception e) {
            model.addAttribute("error", "Fejl ved dekodning af VIN: " + e.getMessage());
            model.addAttribute("step", 1);
            model.addAttribute("vin", vin);
            return "cars_add_car";
        }
    }

    // Step 2: Confirm and add car to database
    @PostMapping("/car/add")
    public String addCar(@RequestParam String vin,
                         @RequestParam(required = false) String regNr,
                         @RequestParam String location,
                         @RequestParam int odometer,
                         @RequestParam(required = false) String carDescription,
                         @RequestParam String make,
                         @RequestParam("model") String carModel,
                         Model model,
                         @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        logger.info("POST /car/add received. currentUser={}, role={}, vin={}, regNr={}, location={}, odometer={}, description={}, make={}, model={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null",
                vin, regNr, location, odometer, carDescription, make, carModel);

        String accessCheck = checkAccess(currentUser);
        if (accessCheck != null) {
            return accessCheck;
        }

        model.addAttribute("currentUser", currentUser);

        try {
            carService.addCar(vin, regNr, location, odometer, carDescription, make, carModel);
            model.addAttribute("success", true);
            return "cars_add_car";

        } catch (Exception e) {
            logger.error("Error while adding car. vin={}, regNr={}, user={}, message={}",
                    vin,
                    regNr,
                    currentUser != null ? currentUser.getEmail() : "null",
                    e.getMessage(),
                    e);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("step", 2);
            model.addAttribute("vin", vin);
            model.addAttribute("regNr", regNr);
            model.addAttribute("location", location);
            model.addAttribute("odometer", odometer);
            model.addAttribute("carDescription", carDescription);
            model.addAttribute("make", make);
            model.addAttribute("model", carModel);
            return "cars_add_car";
        }
    }

    private String checkAccess(User currentUser) {
        logger.info("Access check: currentUser={}, role={}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUser != null ? currentUser.getRole() : "null");

        if (currentUser == null) {
            logger.warn("Access denied because currentUser is null");
            return "redirect:/login";
        }

        String role = currentUser.getRole() != null ? currentUser.getRole().trim() : "";

        if (!("DATAREGISTRERING".equals(role) || "ADMIN".equals(role) || "FORETNINGSUDVIKLING".equals(role))) {
            logger.warn("Access denied for user='{}' with role='{}'", currentUser.getEmail(), role);
            return "redirect:/access-denied";
        }

        logger.info("Access granted for user='{}' with role='{}'", currentUser.getEmail(), role);
        return null;
    }


}
