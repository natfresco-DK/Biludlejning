package ek.dk.biludlejning.model;

import java.time.LocalDate;

public class RentalAgreement {

    protected int agreementId;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int downpayment;
    protected int monthly_payment;
    protected int maxKm;
    protected int createdBy;
    protected int car;
    protected int customer;
    protected Boolean active = true;

    public RentalAgreement() {}

    public RentalAgreement(int agreementId, LocalDate startDate, LocalDate endDate, int downpayment, int maxKm, int createdBy, int car, int customer, int monthly_payment) {
        this.agreementId = agreementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.downpayment = downpayment;
        this.maxKm = maxKm;
        this.createdBy = createdBy;
        this.car = car;
        this.customer = customer;
        this.monthly_payment = monthly_payment;
    }

    public RentalAgreement(int agreementId, LocalDate startDate, LocalDate endDate, int downpayment, int maxKm, int createdBy, int car, int customer, int monthly_payment, Boolean active) {
        this.agreementId = agreementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.downpayment = downpayment;
        this.maxKm = maxKm;
        this.createdBy = createdBy;
        this.car = car;
        this.customer = customer;
        this.monthly_payment = monthly_payment;
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

    public int getDownpayment() {
        return downpayment;
    }

    public void setDownpayment(int downpayment) {
        this.downpayment = downpayment;
    }

    public int  getMonthly_payment() {
        return monthly_payment;
    }

    public void setMonthly_payment(int monthly_payment) {
        this.monthly_payment = monthly_payment;
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
