package ek.dk.biludlejning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class VinService {
    
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL =
            "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/";

    public String getVehicleInfo(String vin) {
        vin = vin.toUpperCase();

        String url = BASE_URL + vin + "?format=json";

        return restTemplate.getForObject(url, String.class);
    }
}
