package ek.dk.biludlejning.serviceTests;

import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.service.CarService;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalAgreementServiceTest {

    @Mock
    private IRentalAgreementRepository rentalAgreementRepository;

    @Mock
    private CarService carService;

    @InjectMocks
    private RentalAgreementService rentalAgreementService;

    private RentalAgreement rentalAgreement;

    @BeforeEach
    void setUp() {
        rentalAgreement = new RentalAgreement();
        rentalAgreement.setCustomer(1);
        rentalAgreement.setCar(2);
        rentalAgreement.setStartDate(LocalDate.now());
        rentalAgreement.setEndDate(LocalDate.now().plusDays(7));
        rentalAgreement.setDownpayment(1000);
        rentalAgreement.setMonthly_payment(5000);
        rentalAgreement.setMaxKm(20000);
    }

    @Test
    void createRentalAgreement_shouldReturnEmptyAndCreateAgreement_whenDataIsValid() {
        // Act
        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99);

        // Assert
        assertThat(result).isEmpty();
        assertTrue(rentalAgreement.getActive());
        assertEquals(99, rentalAgreement.getCreatedBy());

        verify(carService, times(1)).setCarAsRented(2);
        verify(rentalAgreementRepository, times(1)).createRentalAgreement(rentalAgreement);
        verifyNoMoreInteractions(carService, rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenCustomerIsMissing() {
        // Arrange
        rentalAgreement.setCustomer(0);

        // Act
        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Kunde skal være valgt");

        verifyNoInteractions(carService);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenCarIsMissing() {
        // Arrange
        rentalAgreement.setCar(0);

        // Act
        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Bil skal være valgt");

        verifyNoInteractions(carService);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenEndDateIsBeforeStartDate() {
        // Arrange
        rentalAgreement.setStartDate(LocalDate.now().plusDays(10));
        rentalAgreement.setEndDate(LocalDate.now().plusDays(5));

        // Act
        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Slutdato kan ikke være før startdato");

        verifyNoInteractions(carService);
        verifyNoInteractions(rentalAgreementRepository);
    }
}