package ek.dk.biludlejning.model;

import java.time.LocalDate;

public class RentalAgreement {

    protected int agreementId;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int price;
    protected int maxKm;
    protected User createdBy;
    protected Car car;
    protected Customer customer;

    public RentalAgreement() {}

    public RentalAgreement(int agreementId, LocalDate startDate, LocalDate endDate, int price, int maxKm, User createdBy, Car car, Customer customer) {
        this.agreementId = agreementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.maxKm = maxKm;
        this.createdBy = createdBy;
        this.car = car;
        this.customer = customer;
    }

    public int getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMaxKm() {
        return maxKm;
    }

    public void setMaxKm(int maxKm) {
        this.maxKm = maxKm;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
