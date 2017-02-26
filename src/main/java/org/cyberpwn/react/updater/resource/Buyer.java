package org.cyberpwn.react.updater.resource;

import java.util.Date;
import org.cyberpwn.react.updater.user.User;

/**
 * Premium resource Buyer
 *
 * @author Maxim Van de Wynckel
 */
public interface Buyer extends User {
    /**
     * Get purchase date time
     *
     * @return purchase date
     */
    Date getPurchaseDateTime();

    /**
     * Get the currency it was bought with
     *
     * @return currency
     */
    String getPurchaseCurrency();

    /**
     * Get the price the buyer paid for
     *
     * @return purchase price
     */
    double getPurchasePrice();

    /**
     * Check if the buyer was actually added by the buyer
     *
     * @return added by buyer or not
     */
    boolean addedByAuthor();
}
