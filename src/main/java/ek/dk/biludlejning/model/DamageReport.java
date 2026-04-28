package ek.dk.biludlejning.model;

import java.time.LocalDate;
import java.util.List;

public class DamageReport {

    protected int damgeReportId;
    protected LocalDate returnDate;
    protected LocalDate reportDate;
    protected double cost;
    protected int odometer;
    protected int rentalAgreementId;
    protected int registeredBy;
    protected List<DamageItem> damages;

    public DamageReport() {}

    public DamageReport(LocalDate reportDate, LocalDate returnDate, double cost, int odometer, int rentalAgreementId, int registeredBy, List<DamageItem> damages) {
        this.damages = damages;
        this.rentalAgreementId = rentalAgreementId;
        this.odometer = odometer;
        this.cost = cost;
        this.reportDate = reportDate;
        this.returnDate = returnDate;
        this.registeredBy = registeredBy;
    }

    public DamageReport(int damgeReportId, LocalDate reportDate, LocalDate returnDate, double cost, int odometer, int rentalAgreementId, int registeredBy, List<DamageItem> damages) {
        this.damgeReportId = damgeReportId;
        this.damages = damages;
        this.rentalAgreementId = rentalAgreementId;
        this.odometer = odometer;
        this.cost = cost;
        this.reportDate = reportDate;
        this.returnDate = returnDate;
        this.registeredBy = registeredBy;
    }

    public int getDamgeReportId() {
        return damgeReportId;
    }

    public void setDamgeReportId(int damgeReportId) {
        this.damgeReportId = damgeReportId;
    }

    public List<DamageItem> getDamages() {
        return damages;
    }

    public void setDamages(List<DamageItem> damages) {
        this.damages = damages;
    }

    public int getRentalAgreementId() {
        return rentalAgreementId;
    }

    public void setRentalAgreementId(int rentalAgreementId) {
        this.rentalAgreementId = rentalAgreementId;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public int getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(int registeredBy) {
        this.registeredBy = registeredBy;
    }
}
