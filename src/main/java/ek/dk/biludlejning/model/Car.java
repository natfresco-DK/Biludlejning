package ek.dk.biludlejning.model;

public class Car {
    protected int carId;
    protected String regNr;
    protected String vin;
    protected String brand;
    protected String model;
    protected String location;
    protected int odometer;
    protected String status;
    protected boolean active;

    public Car(){}

    public Car(int carId, String regNr, String vin, String brand, String model, String location, int odometer, String status,  boolean active) {
        this.carId = carId;
        this.regNr = regNr;
        this.vin = vin;
        this.brand = brand;
        this.model = model;
        this.location = location;
        this.odometer = odometer;
        this.status = status;
    }

    public String getRegNr() {
        return regNr;
    }

    public void setRegNr(String regNr) {
        this.regNr = regNr;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
