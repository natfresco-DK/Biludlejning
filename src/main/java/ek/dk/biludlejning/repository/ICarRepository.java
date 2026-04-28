package ek.dk.biludlejning.repository;


import ek.dk.biludlejning.model.Car;

import java.util.Optional;

public interface ICarRepository {
    //create a car in the table
    void createCar(Car car);

    //find car by X(data) and y(attribute) in table
    Optional<Car> findByXY( String attribute, Object data);

    //update existing car in table
    void update(Car car);

    //delete car by ID in table
    void deleteById(int id);

}
