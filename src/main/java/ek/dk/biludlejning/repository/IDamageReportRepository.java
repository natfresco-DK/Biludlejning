package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.DamageReport;

import java.util.List;
import java.util.Optional;

public interface IDamageReportRepository {

    // create a new damage report
    void createDamageReport(DamageReport report);

    // find damage report by attribute
    Optional<DamageReport> findByXY(String attribute, Object data);

    List<DamageReport> getAllDamageReports();

    List<DamageReport> getFilteredDamageReports(Integer reportId,
                                                java.time.LocalDate returnDate,
                                                java.time.LocalDate reportDate,
                                                Double cost,
                                                Integer odometer,
                                                Integer rentalAgreementId);

    // update existing damage report
    void update(DamageReport report);

    void updateCost(int reportId);

    // delete damage report by id
    int deleteById(int id);

    void setCarToMaintenance(DamageReport report);

    DamageReport getDamageReportById(int reportId);

}

