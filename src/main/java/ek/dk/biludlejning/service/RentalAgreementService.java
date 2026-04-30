package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.CarRepository;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.repository.RentalAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RentalAgreementService {

    private final IRentalAgreementRepository rentalAgreementRepository;
    private final ICarRepository carRepository;


    public RentalAgreementService(IRentalAgreementRepository rentalAgreementRepository, ICarRepository carRepository) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.carRepository = carRepository;
    }

    @Transactional
    public Optional<String> createRentalAgreement(RentalAgreement rentalAgreement, int createdBy) {
        Optional<String> validationError = validateRentalAgreement(rentalAgreement);
        if (validationError.isPresent()) {
            return validationError;
        }

        rentalAgreement.setActive(true);
        rentalAgreement.setCreatedBy(createdBy);

        setCarAsRented(rentalAgreement.getCar());
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

    public List<RentalAgreement> findByCarId(int carId){
        return rentalAgreementRepository.findByCarId(carId);
    }

    public void setCarAsRented(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            throw new IllegalArgumentException("Bilen er ikke tilgængelig");
        }

        car.setStatus("RENTED");
        carRepository.update(car);
    }

    public void setCarAsReturned(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));
        if (!"RENTED".equalsIgnoreCase(car.getStatus())) {
            throw new IllegalArgumentException("Bilen er ikke udlejet");
        }
        if (!isLeaseCompleted(carId)) {
            throw new IllegalArgumentException("Leaset er ikke afsluttet endnu");
        }
        car.setStatus("AVAILABLE");
        carRepository.update(car);
    }

    public boolean isLeaseCompleted(int carId) {
        List<RentalAgreement> activeLeases = findByCarId(carId);
        if (activeLeases.isEmpty()) {
            return true;
        }
        LocalDate today = LocalDate.now();
        for (RentalAgreement lease : activeLeases) {
            if (lease.getActive() && lease.getEndDate().isAfter(today)) {
                return false;
            }
        }
        return true;
    }

}