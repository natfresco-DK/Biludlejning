package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.service.VinService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vin")
public class VinController {

    private final VinService vinService;

    public VinController(VinService vinService) {
        this.vinService = vinService;
    }

    @GetMapping("/{vin}")
    public String getVehicle(@PathVariable String vin) {
        return vinService.getVehicleInfo(vin);
    }
}