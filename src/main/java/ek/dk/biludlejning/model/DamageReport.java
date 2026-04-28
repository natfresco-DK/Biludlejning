package ek.dk.biludlejning.model;

import java.time.LocalDate;
import java.util.List;

public class DamageReport {

    protected int damgeReportId;
    protected LocalDate returnDate;
    protected LocalDate reportDate;
    protected double cost;
    protected int odometer;
    protected RentalAgreement rentalAgreement;
    protected List<DamageItem> damages;

    public DamageReport() {}

    public DamageReport(int damgeReportId, List<DamageItem> damages, RentalAgreement rentalAgreement, int odometer, double cost, LocalDate reportDate, LocalDate returnDate) {
        this.damgeReportId = damgeReportId;
        this.damages = damages;
        this.rentalAgreement = rentalAgreement;
        this.odometer = odometer;
        this.cost = cost;
        this.reportDate = reportDate;
        this.returnDate = returnDate;
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

    public RentalAgreement getRentalAgreement() {
        return rentalAgreement;
    }

    public void setRentalAgreement(RentalAgreement rentalAgreement) {
        this.rentalAgreement = rentalAgreement;
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
}
