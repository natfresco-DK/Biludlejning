package ek.dk.biludlejning.serviceTests;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.service.RentalAgreementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalAgreementServiceTest {

    @Mock
    private IRentalAgreementRepository rentalAgreementRepository;

    @Mock
    private ICarRepository carRepository;

    @InjectMocks
    private RentalAgreementService rentalAgreementService;

    private RentalAgreement rentalAgreement;
    private Car car;

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

        car = new Car();
        car.setCarId(2);
        car.setStatus("AVAILABLE");
    }

    @Test
    void createRentalAgreement_shouldReturnEmptyAndCreateAgreement_whenDataIsValid() {
        // Arrange
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        // Act
        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99);

        // Assert
        assertThat(result).isEmpty();
        assertTrue(rentalAgreement.getActive());
        assertEquals(99, rentalAgreement.getCreatedBy());

        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(any(Car.class));
        verify(rentalAgreementRepository, times(1)).createRentalAgreement(rentalAgreement);
        verifyNoMoreInteractions(carRepository, rentalAgreementRepository);
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

        verifyNoInteractions(carRepository);
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

        verifyNoInteractions(carRepository);
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

        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void setCarAsRented_shouldUpdateCarStatusToRented_whenCarIsAvailable() {
        // Arrange
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        // Act
        rentalAgreementService.setCarAsRented(2);

        // Assert
        assertEquals("RENTED", car.getStatus());
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(car);
    }

    @Test
    void setCarAsRented_shouldThrowException_whenCarNotFound() {
        // Arrange
        when(carRepository.findByXY("car_id", 999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsRented(999));
        verify(carRepository, times(1)).findByXY("car_id", 999);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsRented_shouldThrowException_whenCarIsNotAvailable() {
        // Arrange
        car.setStatus("RENTED");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsRented(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldUpdateCarStatusToAvailable_whenLeaseIsCompleted() {
        // Arrange
        car.setStatus("RENTED");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of()); // No active leases

        // Act
        rentalAgreementService.setCarAsReturned(2);

        // Assert
        assertEquals("AVAILABLE", car.getStatus());
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(car);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenCarNotFound() {
        // Arrange
        when(carRepository.findByXY("car_id", 999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(999));
        verify(carRepository, times(1)).findByXY("car_id", 999);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenCarIsNotRented() {
        // Arrange
        car.setStatus("AVAILABLE");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenLeaseIsNotCompleted() {
        // Arrange
        car.setStatus("RENTED");
        RentalAgreement activeLease = new RentalAgreement();
        activeLease.setActive(true);
        activeLease.setEndDate(LocalDate.now().plusDays(5)); // Future date

        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(activeLease));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenNoActiveLeasesExist() {
        // Arrange
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of());

        // Act
        boolean result = rentalAgreementService.isLeaseCompleted(2);

        // Assert
        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnFalse_whenActiveLeasePeriodNotEnded() {
        // Arrange
        RentalAgreement activeLease = new RentalAgreement();
        activeLease.setActive(true);
        activeLease.setEndDate(LocalDate.now().plusDays(5)); // Future date

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(activeLease));

        // Act
        boolean result = rentalAgreementService.isLeaseCompleted(2);

        // Assert
        assertFalse(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenAllLeasePeriodsPassed() {
        // Arrange
        RentalAgreement completedLease = new RentalAgreement();
        completedLease.setActive(true);
        completedLease.setEndDate(LocalDate.now().minusDays(1)); // Past date

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(completedLease));

        // Act
        boolean result = rentalAgreementService.isLeaseCompleted(2);

        // Assert
        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenLeaseIsInactive() {
        // Arrange
        RentalAgreement inactiveLease = new RentalAgreement();
        inactiveLease.setActive(false);
        inactiveLease.setEndDate(LocalDate.now().plusDays(5)); // Even though end date is in future

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(inactiveLease));

        // Act
        boolean result = rentalAgreementService.isLeaseCompleted(2);

        // Assert
        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }
}