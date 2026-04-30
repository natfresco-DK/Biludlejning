package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.repository.RentalAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RentalAgreementService {

    private final IRentalAgreementRepository rentalAgreementRepository;
    private final CarService carService;


    public RentalAgreementService(IRentalAgreementRepository rentalAgreementRepository, CarService carService) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.carService = carService;
    }

    @Transactional
    public Optional<String> createRentalAgreement(RentalAgreement rentalAgreement, int createdBy) {
        Optional<String> validationError = validateRentalAgreement(rentalAgreement);
        if (validationError.isPresent()) {
            return validationError;
        }

        rentalAgreement.setActive(true);
        rentalAgreement.setCreatedBy(createdBy);

        carService.setCarAsRented(rentalAgreement.getCar());
        rentalAgreementRepository.createRentalAgreement(rentalAgreement);

        return Optional.empty();
    }

    public List<RentalAgreement> getAllRentalAgreements(){
        List<RentalAgreement> rentalAgreementList = rentalAgreementRepository.getAllRentalAgreements();
        return rentalAgreementList;
    }

    private Optional<String> validateRentalAgreement(RentalAgreement rentalAgreement) {
        if (rentalAgreement.getCustomer() == 0) {
            return Optional.of("Kunde skal være valgt");
        }
        if (rentalAgreement.getCar() == 0) {
            return Optional.of("Bil skal være valgt");
        }
        if (rentalAgreement.getStartDate() == null) {
            return Optional.of("Startdato skal udfyldes");
        }
        if (rentalAgreement.getEndDate() == null) {
            return Optional.of("Slutdato skal udfyldes");
        }
        if (rentalAgreement.getEndDate().isBefore(rentalAgreement.getStartDate())) {
            return Optional.of("Slutdato kan ikke være før startdato");
        }
        return Optional.empty();
    }

    public Double getTotalActiveRevenue() {
        return rentalAgreementRepository.getTotalActiveRevenue();
    }

    public List<RentalAgreement> getReturnedRentalAgreements() {
        return rentalAgreementRepository.getReturnedRentalAgreements();
    }


}