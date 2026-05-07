package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.VehicleInfoDto;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class VinParsingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public VehicleInfoDto parseVehicleInfo(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode results = root.get("Results");

        String make = null;
        String model = null;
        String modelYear = null;

        if (results != null && results.isArray()) {
            for (JsonNode item : results) {
                String variable = item.path("Variable").asText(null);
                String value = item.path("Value").asText(null);

                if ("Make".equals(variable)) {
                    make = value;
                } else if ("Model".equals(variable)) {
                    model = value;
                } else if ("Model Year".equals(variable)) {
                    modelYear = value;
                }
            }
        }

        return new VehicleInfoDto(make, model, modelYear);
    }
}