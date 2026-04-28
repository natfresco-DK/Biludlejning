package ek.dk.biludlejning.model;

public class DamageItem {

    protected int damageId;
    protected String description;
    protected double price;

    public DamageItem() {}

    public DamageItem(int damageId, String description, double price) {
        this.damageId = damageId;
        this.description = description;
        this.price = price;
    }

    public int getDamageId() {
        return damageId;
    }
    public void setDamageId(int damageId) {}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



}
