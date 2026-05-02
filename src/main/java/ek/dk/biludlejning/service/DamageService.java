package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.DamageItem;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.IDamageItemRepository;
import ek.dk.biludlejning.repository.IDamageReportRepository;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DamageService {

    private final IDamageReportRepository damageReportRepository;
    private final ICarRepository carRepository;
    private final IRentalAgreementRepository rentalAgreementRepository;
    private final IDamageItemRepository damageItemRepository;

    public DamageService(IDamageReportRepository damageReportRepository,
                         ICarRepository carRepository,
                         IRentalAgreementRepository rentalAgreementRepository, IDamageItemRepository damageItemRepository) {
        this.damageReportRepository = damageReportRepository;
        this.carRepository = carRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.damageItemRepository = damageItemRepository;
    }

    @Transactional
    public Optional<String> createDamageReport(DamageReport damageReport, int userId) {

        Optional<String> validationError = validateDamageReport(damageReport);
        if (validationError.isPresent()) {
            return validationError;
        }

        damageReport.setRegisteredBy(userId);
        damageReport.setReportDate(LocalDate.now());

        damageReportRepository.createDamageReport(damageReport);
        damageReportRepository.setCarToMaintenance(damageReport);

        return Optional.empty();
    }

    public List<DamageReport> getAllDamageReports(){
        return damageReportRepository.getAllDamageReports();
    }

    private Optional<String> validateDamageReport(DamageReport damageReport) {
        try {
            if (damageReport.getRentalAgreementId() == 0) {
                return Optional.of("Lejeaftale skal vælges");
            }

            Optional<RentalAgreement> rentalAgreement =
                    rentalAgreementRepository.findByXY("agreement_id", damageReport.getRentalAgreementId());

            if (rentalAgreement.isEmpty()) {
                return Optional.of("Lejeaftalen blev ikke fundet");
            }

            Optional<Car> carOptional =
                    carRepository.findByXY("car_id", rentalAgreement.get().getCar());

            if (carOptional.isEmpty()) {
                return Optional.of("Bilen blev ikke fundet");
            }

            Car car = carOptional.get();

            if (!"RETURNED".equalsIgnoreCase(car.getStatus())) {
                return Optional.of("Bilen skal have status 'RETURNED'");
            }

            return Optional.empty();

        } catch (Exception e) {
            return Optional.of("Der opstod en fejl: " + e.getMessage());
        }
    }

    public Optional<String> createDamageItem(DamageItem damageItem) {
        if (damageItem.getReportId() == 0) {
            return Optional.of("Skaderapport skal vælges");
        }

        if (damageItem.getDescription() == null || damageItem.getDescription().isBlank()) {
            return Optional.of("Beskrivelse skal udfyldes");
        }

        if (damageItem.getPrice() < 0) {
            return Optional.of("Pris må ikke være negativ");
        }

        damageItemRepository.createDamageItem(damageItem);
        return Optional.empty();
    }

    public DamageReport getDamageReportById(int reportId) {
        return damageReportRepository.getDamageReportById(reportId);
    }

    public List<DamageItem> getDamageItemsByReportId(int reportId) {
        return damageItemRepository.getDamageItemsByReportId(reportId);
    }
}


