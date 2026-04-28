package ek.dk.biludlejning.repository;

import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface IRentalAgreementRepository {

    void createRentalAgreement(RentalAgreement rentalAgreement);

    RentalAgreement mapRentalAgreement(ResultSet rs, int rowNum) throws SQLException;

    Optional<User> findByXY(String attribute, Object data) throws Exception;


    void updateRentalAgreement(RentalAgreement rentalAgreement);
    int deleteRentalAgreementById(int id);

}
