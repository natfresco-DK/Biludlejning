package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.RentalAgreement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface IRentalAgreementRepository {

    void createRentalAgreement(RentalAgreement rentalAgreement);

    RentalAgreement mapRentalAgreement(ResultSet rs, int rowNum) throws SQLException;

    Optional<RentalAgreement> findByXY(String attribute, Object data) throws Exception;

    List<RentalAgreement> getAllRentalAgreements();

    List<RentalAgreement> getFilteredRentalAgreements(Integer agreementId,
                                                      Integer customerId,
                                                      Integer carId,
                                                      LocalDate startDate,
                                                      LocalDate endDate,
                                                      Double downpayment,
                                                      Double monthlyPayment,
                                                      Integer maxKm,
                                                      Boolean active);

    void updateRentalAgreement(RentalAgreement rentalAgreement);

    int deleteRentalAgreementById(int id);

    List<RentalAgreement> findByCarId(int carId);

    double getTotalActiveRevenue();

    List<RentalAgreement> getReturnedRentalAgreements();



}
