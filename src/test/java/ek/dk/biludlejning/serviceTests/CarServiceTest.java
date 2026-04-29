package ek.dk.biludlejning.serviceTests;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.service.CarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private ICarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void getAvailableCars_returnsRepositoryList() {
        // Arrange
        Car c1 = new Car();
        c1.setCarId(1);
        c1.setBrand("Toyota");
        c1.setModel("Corolla");
        c1.setRegNr("ABC123");
        c1.setStatus("AVAILABLE");
        c1.setActive(true);

        Car c2 = new Car();
        c2.setCarId(2);
        c2.setBrand("Ford");
        c2.setModel("Focus");
        c2.setRegNr("DEF456");
        c2.setStatus("AVAILABLE");
        c2.setActive(true);

        List<Car> repoList = new ArrayList<>();
        repoList.add(c1);
        repoList.add(c2);

        when(carRepository.findAvailableCars()).thenReturn(repoList);

        // Act
        List<Car> result = carService.getAvailableCars();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(repoList);

        verify(carRepository, times(1)).findAvailableCars();
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void getAvailableCars_returnsEmptyList_whenNoneAvailable() {
        // Arrange
        when(carRepository.findAvailableCars()).thenReturn(new ArrayList<>());

        // Act
        List<Car> result = carService.getAvailableCars();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(carRepository, times(1)).findAvailableCars();
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void setCarAsRented_updatesStatusToRented_whenCarIsAvailable() {
        // Arrange
        Car car = new Car();
        car.setCarId(1);
        car.setStatus("AVAILABLE");
        car.setActive(true);

        when(carRepository.findByXY("car_id", 1)).thenReturn(Optional.of(car));

        // Act
        carService.setCarAsRented(1);

        // Assert
        assertThat(car.getStatus()).isEqualTo("RENTED");

        verify(carRepository, times(1)).findByXY("car_id", 1);
        verify(carRepository, times(1)).update(car);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void setCarAsRented_throwsException_whenCarDoesNotExist() {
        // Arrange
        when(carRepository.findByXY("car_id", 1)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> carService.setCarAsRented(1)
        );

        verify(carRepository, times(1)).findByXY("car_id", 1);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void setCarAsRented_throwsException_whenCarIsNotAvailable() {
        // Arrange
        Car car = new Car();
        car.setCarId(1);
        car.setStatus("RENTED");
        car.setActive(true);

        when(carRepository.findByXY("car_id", 1)).thenReturn(java.util.Optional.of(car));

        // Act & Assert
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> carService.setCarAsRented(1)
        );

        verify(carRepository, times(1)).findByXY("car_id", 1);
        verifyNoMoreInteractions(carRepository);
    }
}