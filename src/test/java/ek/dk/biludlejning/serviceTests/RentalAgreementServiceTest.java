package ek.dk.biludlejning.serviceTests;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.service.CustomerService;
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

    @Mock
    private CustomerService customerService;

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
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, null);

        assertThat(result).isEmpty();
        assertTrue(rentalAgreement.getActive());
        assertEquals(99, rentalAgreement.getCreatedBy());

        verifyNoInteractions(customerService);
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(any(Car.class));
        verify(rentalAgreementRepository, times(1)).createRentalAgreement(rentalAgreement);
        verifyNoMoreInteractions(carRepository, rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldCreateNewCustomerAndUseItsId_whenNewCustomerProvidedAndLeaseDataValid() {
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        Customer inputCustomer = new Customer();
        inputCustomer.setFirstName("Test");

        Customer createdCustomer = new Customer();
        createdCustomer.setCustomerId(123);
        when(customerService.createCustomer(inputCustomer)).thenReturn(createdCustomer);

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, inputCustomer);

        assertThat(result).isEmpty();
        assertEquals(123, rentalAgreement.getCustomer());
        assertTrue(rentalAgreement.getActive());
        assertEquals(99, rentalAgreement.getCreatedBy());

        verify(customerService, times(1)).createCustomer(inputCustomer);
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(any(Car.class));
        verify(rentalAgreementRepository, times(1)).createRentalAgreement(rentalAgreement);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenCustomerIsMissing() {
        rentalAgreement.setCustomer(0);

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Kunde skal være valgt");

        verifyNoInteractions(customerService);
        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenCarIsMissing() {
        rentalAgreement.setCar(0);

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Bil skal være valgt");

        verifyNoInteractions(customerService);
        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldReturnValidationError_whenEndDateIsBeforeStartDate() {
        rentalAgreement.setStartDate(LocalDate.now().plusDays(10));
        rentalAgreement.setEndDate(LocalDate.now().plusDays(5));

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Slutdato kan ikke være før startdato");

        verifyNoInteractions(customerService);
        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void createRentalAgreement_shouldNotCreateCustomer_whenValidationFailsEvenIfNewCustomerProvided() {
        rentalAgreement.setCar(0); // invalid lease data

        Customer inputCustomer = new Customer();
        inputCustomer.setFirstName("Test");

        Optional<String> result = rentalAgreementService.createRentalAgreement(rentalAgreement, 99, inputCustomer);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Bil skal være valgt");

        verifyNoInteractions(customerService);
        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalAgreementRepository);
    }

    @Test
    void setCarAsRented_shouldUpdateCarStatusToRented_whenCarIsAvailable() {
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        rentalAgreementService.setCarAsRented(2);

        assertEquals("RENTED", car.getStatus());
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(car);
    }

    @Test
    void setCarAsRented_shouldThrowException_whenCarNotFound() {
        when(carRepository.findByXY("car_id", 999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsRented(999));
        verify(carRepository, times(1)).findByXY("car_id", 999);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsRented_shouldThrowException_whenCarIsNotAvailable() {
        car.setStatus("RENTED");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsRented(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldUpdateCarStatusToAvailable_whenLeaseIsCompleted() {
        car.setStatus("RENTED");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of());

        rentalAgreementService.setCarAsReturned(2);

        assertEquals("AVAILABLE", car.getStatus());
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, times(1)).update(car);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenCarNotFound() {
        when(carRepository.findByXY("car_id", 999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(999));
        verify(carRepository, times(1)).findByXY("car_id", 999);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenCarIsNotRented() {
        car.setStatus("AVAILABLE");
        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));

        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void setCarAsReturned_shouldThrowException_whenLeaseIsNotCompleted() {
        car.setStatus("RENTED");
        RentalAgreement activeLease = new RentalAgreement();
        activeLease.setActive(true);
        activeLease.setEndDate(LocalDate.now().plusDays(5));

        when(carRepository.findByXY("car_id", 2)).thenReturn(Optional.of(car));
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(activeLease));

        assertThrows(IllegalArgumentException.class, () -> rentalAgreementService.setCarAsReturned(2));
        verify(carRepository, times(1)).findByXY("car_id", 2);
        verify(carRepository, never()).update(any());
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenNoActiveLeasesExist() {
        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of());

        boolean result = rentalAgreementService.isLeaseCompleted(2);

        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnFalse_whenActiveLeasePeriodNotEnded() {
        RentalAgreement activeLease = new RentalAgreement();
        activeLease.setActive(true);
        activeLease.setEndDate(LocalDate.now().plusDays(5));

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(activeLease));

        boolean result = rentalAgreementService.isLeaseCompleted(2);

        assertFalse(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenAllLeasePeriodsPassed() {
        RentalAgreement completedLease = new RentalAgreement();
        completedLease.setActive(true);
        completedLease.setEndDate(LocalDate.now().minusDays(1));

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(completedLease));

        boolean result = rentalAgreementService.isLeaseCompleted(2);

        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }

    @Test
    void isLeaseCompleted_shouldReturnTrue_whenLeaseIsInactive() {
        RentalAgreement inactiveLease = new RentalAgreement();
        inactiveLease.setActive(false);
        inactiveLease.setEndDate(LocalDate.now().plusDays(5));

        when(rentalAgreementRepository.findByCarId(2)).thenReturn(List.of(inactiveLease));

        boolean result = rentalAgreementService.isLeaseCompleted(2);

        assertTrue(result);
        verify(rentalAgreementRepository, times(1)).findByCarId(2);
    }
}