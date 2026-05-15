package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.DamageItem;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.IDamageItemRepository;
import ek.dk.biludlejning.repository.IDamageReportRepository;
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
public class DamageService {

    private final IDamageReportRepository damageReportRepository;
    private final ICarRepository carRepository;
    private final IRentalAgreementRepository rentalAgreementRepository;
    private final IDamageItemRepository damageItemRepository;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(DamageService.class);


    public DamageService(IDamageReportRepository damageReportRepository,
                         ICarRepository carRepository,
                         IRentalAgreementRepository rentalAgreementRepository, IDamageItemRepository damageItemRepository, UserService userService) {
        this.damageReportRepository = damageReportRepository;
        this.carRepository = carRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.damageItemRepository = damageItemRepository;
        this.userService = userService;
    }

    @Transactional
    public Optional<String> createDamageReport(DamageReport damageReport, int userId) {

        Optional<String> validationError = validateDamageReport(damageReport);
        if (validationError.isPresent()) {
            return validationError;
        }

        damageReport.setRegisteredBy(userId);
        damageReport.setReportDate(LocalDate.now());
        damageReport.setCost(0);

        damageReportRepository.createDamageReport(damageReport);
        damageReportRepository.setCarToMaintenance(damageReport);

        logger.info("Creating damage report for damage report: {}", damageReport);
        return Optional.empty();
    }

    public List<DamageReport> getAllDamageReports(){
        logger.info("Fetching all damage reports. Total: {}", damageReportRepository.getAllDamageReports().size());
        List<DamageReport> reports = damageReportRepository.getAllDamageReports();

        for (DamageReport report : reports) {
            double totalCost = 0;
            List<DamageItem> damageItems = damageItemRepository.getDamageItemsByReportId(report.getDamgeReportId());
            for (DamageItem item : damageItems) {
                totalCost += item.getPrice();
            }
            report.setCost(totalCost);
        }

        return reports;
    }

    public List<DamageReport> getFilteredDamageReports(Integer reportId,
                                                        LocalDate returnDate,
                                                        LocalDate reportDate,
                                                        Double cost,
                                                        Integer odometer,
                                                        Integer rentalAgreementId,
                                                        String registeredByUsername) {
        logger.info("Filtering damage reports with criteria - reportId: {}, returnDate: {}, reportDate: {}, cost: {}, odometer: {}, rentalAgreementId: {}, registeredBy: {}",
                reportId, returnDate, reportDate, cost,odometer, rentalAgreementId, registeredByUsername);

        List<DamageReport> reports = damageReportRepository.getFilteredDamageReports(
                reportId, returnDate, reportDate, cost, odometer, rentalAgreementId);

        for (DamageReport report : reports) {
            userService.findById(report.getRegisteredBy()).
                    ifPresent(user -> report.setRegisteredByUsername(user.getUsername()));

            double totalCost = 0;
            List<DamageItem> damageItems = damageItemRepository.getDamageItemsByReportId(report.getDamgeReportId());
            for (DamageItem item : damageItems) {
                totalCost += item.getPrice();
            }
            report.setCost(totalCost);
        }

        if (registeredByUsername != null && !registeredByUsername.isBlank()) {
            List<DamageReport> filteredReports = new ArrayList<>();

            for (DamageReport report : reports) {
                if (report.getRegisteredByUsername() != null &&
                        report.getRegisteredByUsername()
                                .toLowerCase()
                                .contains(registeredByUsername.trim().toLowerCase())) {
                    filteredReports.add(report);
                }
            }

            reports = filteredReports;
        }
        return reports;
    }

    private Optional<String> validateDamageReport(DamageReport damageReport) {
        try {
            if (damageReport.getRentalAgreementId() == 0) {
                logger.warn("Rental Agreement ID is missing for damage report: {}", damageReport);
                return Optional.of("Lejeaftale skal vælges");
            }

            Optional<RentalAgreement> rentalAgreement =
                    rentalAgreementRepository.findByXY("agreement_id", damageReport.getRentalAgreementId());

            if (rentalAgreement.isEmpty()) {
                logger.warn("Rental Agreement not found for damage report: {}", damageReport);
                return Optional.of("Lejeaftalen blev ikke fundet");
            }

            Optional<Car> carOptional =
                    carRepository.findByXY("car_id", rentalAgreement.get().getCar());

            if (carOptional.isEmpty()) {
                logger.warn("Car not found for damage report: {}", damageReport);
                return Optional.of("Bilen blev ikke fundet");
            }

            Car car = carOptional.get();

            if (!"RETURNED".equalsIgnoreCase(car.getStatus())) {
                logger.warn("Car status is not RETURNED for damage report: {}. Current status: {}", damageReport, car.getStatus());
                return Optional.of("Bilen skal have status 'RETURNED'");
            }

            return Optional.empty();

        } catch (Exception e) {
            logger.error("Exception occurred while fetching damage reports: {}", e.getMessage());
            return Optional.of("Der opstod en fejl: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<String> createDamageItem(DamageItem damageItem) {
        if (damageItem.getReportId() == 0) {
            logger.warn("Damage item ID is missing for damage report: {}", damageItem);
            return Optional.of("Skaderapport skal vælges");

        }

        if (damageItem.getDescription() == null || damageItem.getDescription().isBlank()) {
            logger.warn("Description is missing for damage report: {}", damageItem);
            return Optional.of("Beskrivelse skal udfyldes");
        }

        if (damageItem.getPrice() < 0) {
            logger.warn("Price is missing or lower than 1 for damage report: {}", damageItem);
            return Optional.of("Pris må ikke være negativ");
        }

        damageItemRepository.createDamageItem(damageItem);
        damageReportRepository.updateCost(damageItem.getReportId());
        return Optional.empty();
    }

    public DamageReport getDamageReportById(int reportId) {
        logger.info("Fetching damage report by ID: {}", reportId);
        DamageReport report = damageReportRepository.getDamageReportById(reportId);

        double totalCost = 0;

        List<DamageItem> damageItems = damageItemRepository.getDamageItemsByReportId(reportId);
        for (DamageItem item : damageItems) {
            totalCost += item.getPrice();
        }

        report.setCost(totalCost);

        return report;
    }

    public List<DamageItem> getDamageItemsByReportId(int reportId) {
        logger.info("Fetching damage items by ID: {}. Total: {}", reportId, damageItemRepository.getDamageItemsByReportId(reportId).size());
        return damageItemRepository.getDamageItemsByReportId(reportId);
    }
}


