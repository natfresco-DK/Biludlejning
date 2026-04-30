package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.DamageReport;

import java.util.Optional;

public interface IDamageReportRepository {

    // create a new damage report
    void createDamageReport(DamageReport report);

    // find damage report by attribute
    Optional<DamageReport> findByXY(String attribute, Object data);

    // update existing damage report
    void update(DamageReport report);

    // delete damage report by id
    int deleteById(int id);

    void setCarToMaintenance(DamageReport report);


}

