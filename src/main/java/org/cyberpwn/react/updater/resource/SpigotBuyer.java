package org.cyberpwn.react.updater.resource;

import java.util.Date;
import org.cyberpwn.react.updater.user.SpigotUser;

/**
 * Spigot resource buyer
 */
public class SpigotBuyer extends SpigotUser implements Buyer {
    private Date purchaseDate = null;
    private String currency = "";
    private double price = -1;

    public void setPurchaseDate(Date date) {
        this.purchaseDate = date;
    }

    public void setPurchasePrice(double price) {
        this.price = price;
    }

    public void setPurchaseCurrency(String currency) {
        this.currency = currency;
    }

    public Date getPurchaseDateTime() {
        return purchaseDate;
    }

    public String getPurchaseCurrency() {
        return currency;
    }

    public double getPurchasePrice() {
        return price;
    }

    public boolean addedByAuthor() {
        return price == -1;
    }
}
