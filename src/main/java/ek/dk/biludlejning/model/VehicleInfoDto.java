package ek.dk.biludlejning.model;

public class VehicleInfoDto {
    private String make;
    private String model;
    private String modelYear;

    public VehicleInfoDto(String make, String model, String modelYear) {
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getModelYear() {
        return modelYear;
    }
}