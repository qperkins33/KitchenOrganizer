package org.example.kitchenorganizer;

public class Notification implements Notify{
    private User currentUser;
    private String lowQuantityMessage = "Low Quantity Message";

    public Notification(User u) {
        currentUser = u;
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public String getLowQuantityMessage() {
        return lowQuantityMessage;
    }
    public void checkUserInventoryQuantity(Food f) {
        if (f.getQuantity() < f.getMinQuantity()) {
            //Do something to send a notification to the user about low quantity
        }
    }
}
