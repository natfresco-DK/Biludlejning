package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RentalAgreementService {

    private final IRentalAgreementRepository rentalAgreementRepository;
    private final ICarRepository carRepository;
    private final CustomerService customerService;
    private final UserService userService;

    private static final Logger logger =  LoggerFactory.getLogger(RentalAgreementService.class);


    public RentalAgreementService(IRentalAgreementRepository rentalAgreementRepository, ICarRepository carRepository, CustomerService customerService, UserService userService) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.carRepository = carRepository;
        this.customerService = customerService;
        this.userService = userService;
    }

    @Transactional
    public Optional<String> createRentalAgreement(RentalAgreement rentalAgreement, int createdBy, Customer newCustomer) {
        Optional<String> validationError = validateRentalAgreement(rentalAgreement);
        if (validationError.isPresent()) {
            return validationError;
        }

        if(newCustomer != null){
            Customer createdCustomer = customerService.createCustomer(newCustomer);
            rentalAgreement.setCustomer(createdCustomer.getCustomerId());
        }

        rentalAgreement.setCreatedBy(createdBy);

        setCarAsRented(rentalAgreement.getCar());
        rentalAgreementRepository.createRentalAgreement(rentalAgreement);

        return Optional.empty();
    }

    public List<RentalAgreement> getAllRentalAgreements(){
        logger.info("Successfully fetched all rental agreements. Total: {}", rentalAgreementRepository.getAllRentalAgreements().size());
        return rentalAgreementRepository.getAllRentalAgreements();
    }

    public List<RentalAgreement> getFilteredRentalAgreements(Integer agreementId,
                                                             Integer customerId,
                                                             Integer carId,
                                                             LocalDate startDate,
                                                             LocalDate endDate,
                                                             Double downpayment,
                                                             Double monthlyPayment,
                                                             Integer maxKm,
                                                             String createdByUsername,
                                                             Boolean active) {
        logger.info("Filtering rental agreements with criteria - agreementId: {}, customerId: {}, carId: {}, startDate: {}, endDate: {}, downpayment: {}, monthlyPayment: {}, maxKm: {}, createdByUsername: {}, active: {}",
                agreementId, customerId, carId, startDate, endDate, downpayment, monthlyPayment, maxKm, createdByUsername, active);

        List<RentalAgreement> agreements = rentalAgreementRepository.getFilteredRentalAgreements(
                agreementId, customerId, carId, startDate, endDate, downpayment, monthlyPayment, maxKm, active
        );

        for (RentalAgreement agreement : agreements) {
            userService.findById(agreement.getCreatedBy()).ifPresent(
                    user -> agreement.setCreatedByUsername(user.getUsername())
            );
        }

        if (createdByUsername != null && !createdByUsername.isBlank()) {
            String search = createdByUsername.trim().toLowerCase();
            List<RentalAgreement> filteredAgreements = new ArrayList<>();

            for (RentalAgreement agreement : agreements) {
                if (agreement.getCreatedByUsername() != null &&
                        agreement.getCreatedByUsername().toLowerCase().contains(search)) {
                    filteredAgreements.add(agreement);
                }
            }
            agreements = filteredAgreements;
        }

        return agreements;
    }

    private Optional<String> validateRentalAgreement(RentalAgreement rentalAgreement) {
        if (rentalAgreement.getCustomer() == 0) {
            logger.warn("Validation failed, customer is missing");
            return Optional.of("Kunde skal være valgt");
        }
        if (rentalAgreement.getCar() == 0) {
            logger.warn("Validation failed, car is missing");
            return Optional.of("Bil skal være valgt");
        }
        if (rentalAgreement.getStartDate() == null) {
            logger.warn("Validation failed, start date is missing");
            return Optional.of("Startdato skal udfyldes");
        }
        if (rentalAgreement.getEndDate() == null) {
            logger.warn("Validation failed, end date is missing");
            return Optional.of("Slutdato skal udfyldes");
        }
        if (rentalAgreement.getEndDate().isBefore(rentalAgreement.getStartDate())) {
            logger.warn("Validation failed, end date {} is before start date {}", rentalAgreement.getEndDate(), rentalAgreement.getStartDate());
            return Optional.of("Slutdato kan ikke være før startdato");
        }
        return Optional.empty();
    }

    public Double getTotalActiveRevenue() {
        logger.info("Successfully fetched all active revenue. Total: {}.",  rentalAgreementRepository.getTotalActiveRevenue());
        return rentalAgreementRepository.getTotalActiveRevenue();
    }

    public List<RentalAgreement> getReturnedRentalAgreements() {
        logger.info("Successfully fetched all returned Rental Agreements. Total: {}", rentalAgreementRepository.getReturnedRentalAgreements().size());
        return rentalAgreementRepository.getReturnedRentalAgreements();
    }

    public List<RentalAgreement> findByCarId(int carId){
        List<RentalAgreement> rentalAgreements = rentalAgreementRepository.findByCarId(carId);
        logger.info("Successfully found car by id={}. Total Rental Agreements found: {}", carId, rentalAgreements.size());
        return rentalAgreements;
    }

    public void setCarAsRented(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            logger.error("Car status {} is not AVAILABLE", car.getStatus());
            throw new IllegalArgumentException("Bilen er ikke tilgængelig");
        }
        car.setStatus("RENTED");
        carRepository.update(car);
        logger.info("Successfully updated car status to RENTED for carId={}", carId);
    }

    public void setCarAsReturned(int carId) {
        Car car = carRepository.findByXY("car_id", carId)
                .orElseThrow(() -> new IllegalArgumentException("Bilen blev ikke fundet"));
        if (!"RENTED".equalsIgnoreCase(car.getStatus())) {
            logger.error("Car status {} is not RENTED", car.getStatus());
            throw new IllegalArgumentException("Bilen er ikke udlejet");
        }
        if (!isLeaseCompleted(carId)) {
            logger.error("Lease for carId={} is not completed yet", carId);
            throw new IllegalArgumentException("Leaset er ikke afsluttet endnu");
        }
        car.setStatus("AVAILABLE");
        carRepository.update(car);
        logger.info("Successfully updated car status to AVAILABLE for carId={}", carId);
    }

    public boolean isLeaseCompleted(int carId) {
        List<RentalAgreement> rentalAgreements = findByCarId(carId);
        if (rentalAgreements.isEmpty()) {
            logger.error("Rental Agreement not found for carId={}", carId);
            return true;
        }
        LocalDate today = LocalDate.now();
        for (RentalAgreement lease : rentalAgreements) {
            if (lease.getActive() && lease.getEndDate().isAfter(today)) {
                logger.error("Lease for carId={} is already completed", carId);
                return false;
            }
        }
        logger.info("Successfully found lease for carId={}", carId);
        return true;
    }

}