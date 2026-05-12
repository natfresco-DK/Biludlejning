package ek.dk.biludlejning.repository;


import ek.dk.biludlejning.model.Car;

import java.util.List;
import java.util.Optional;
public interface ICarRepository {
    //create a car in the table
    void createCar(Car car);

    //find car by X(data) and y(attribute) in table
    Optional<Car> findByXY( String attribute, Object data);

    //update existing car in table
    void update(Car car);

    List<Car> findAvailableCars();

    List<Car> getAllCars();

    List<Car> findReturnedCars();

    int findAllRentedCars();

    List<Car> findCarsFiltered(Integer carId,
                               String regNr,
                               String vin,
                               String brand,
                               String carModel,
                               String location,
                               Integer odometerMin,
                               Integer odometerMax,
                               String carDescription,
                               String status,
                               Boolean active);

    //delete car by ID in table
    int deleteById(int id);

    void updateCarStatus(int carId, String status);
}
