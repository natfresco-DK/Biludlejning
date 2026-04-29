package ek.dk.biludlejning.repository;



import ek.dk.biludlejning.model.DamageItem;

import java.util.Optional;
public interface IDamageItemRepository {
    //create a car in the table
    void createCar(DamageItem damageItem);

    //find car by X(data) and y(attribute) in table
    Optional<DamageItem> findByXY(String attribute, Object data);

    //update existing car in table
    void update(DamageItem damageItem);

    //delete car by ID in table
    int deleteById(int id);

}
