package org.example.kitchenorganizer.notification;

import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.User;

public class Notification implements Notify {
    private User currentUser;
    private String lowQuantityMessage = "Low Quantity Message";

    public Notification(User u) {
        currentUser = u;
    }
//    public User getCurrentUser() {
//        return currentUser;
//    }
//    public String getLowQuantityMessage() {
//        return lowQuantityMessage;
//    }

    public void checkUserInventoryQuantity(Food f) {
        if (f.getQuantity() < f.getMinQuantity()) {
            notifyUser(lowQuantityMessage + " Item: " + f.getName());
        }
    }

    @Override
    public void notifyUser(String message) {
        System.out.println(message);
    }
}
