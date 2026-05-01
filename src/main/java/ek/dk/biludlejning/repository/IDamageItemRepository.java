package ek.dk.biludlejning.repository;



import ek.dk.biludlejning.model.DamageItem;

import java.util.List;
import java.util.Optional;
public interface IDamageItemRepository {
    //create a car in the table
    void createDamageItem(DamageItem damageItem);

    //find car by X(data) and y(attribute) in table
    Optional<DamageItem> findByXY(String attribute, Object data);

    //update existing car in table
    void update(DamageItem damageItem);

    //delete car by ID in table
    int deleteById(int id);

    List<DamageItem> getDamageItemsByReportId(int reportId);
}
