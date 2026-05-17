package ek.dk.biludlejning.model;

import java.time.LocalDate;

public class RentalAgreement {

    protected int agreementId;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected Double downpayment;
    protected Double monthlyPayment;
    protected int maxKm;
    protected int createdBy;
    protected String createdByUsername;
    protected int car;
    protected int customer;
    protected Boolean active = true;

    public RentalAgreement() {}

    public RentalAgreement(int agreementId, LocalDate startDate, LocalDate endDate, Double downpayment, int maxKm, int createdBy, int car, int customer, Double monthly_payment) {
        this.agreementId = agreementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.downpayment = downpayment;
        this.maxKm = maxKm;
        this.createdBy = createdBy;
        this.car = car;
        this.customer = customer;
        this.monthlyPayment = monthly_payment;
    }

    public RentalAgreement(int agreementId, LocalDate startDate, LocalDate endDate, Double downpayment, int maxKm, int createdBy, int car, int customer, Double monthly_payment, Boolean active) {
        this.agreementId = agreementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.downpayment = downpayment;
        this.maxKm = maxKm;
        this.createdBy = createdBy;
        this.car = car;
        this.customer = customer;
        this.monthlyPayment = monthly_payment;
        this.active = active;
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

    public Double getDownpayment() {
        return downpayment;
    }

    public void setDownpayment(Double downpayment) {
        this.downpayment = downpayment;
    }

    public Double  getMonthly_payment() {
        return monthlyPayment;
    }

    public void setMonthly_payment(Double monthly_payment) {
        this.monthlyPayment = monthly_payment;
    }


    public int getMaxKm() {
        return maxKm;
    }

    public void setMaxKm(int maxKm) {
        this.maxKm = maxKm;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
