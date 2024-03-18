Project Milestone 3 – Implementation
Due March 20, 2024 @ 6:00 PM

How is the code structured?

The code is structured into several packages, each containing classes related to specific
functionalities of the kitchen organizer application.

- org.example.kitchenorganizer.classes: This package contains classes representing various entities
in the kitchen organizer application, such as Food, FoodCollection, InventoryItem, and User.
These classes permit to manage food items, user inventory...

- org.example.kitchenorganizer.notification: This package includes the Notification class,
which implements the Notify interface. It handles notifications related to low food quantity in the user's inventory.

- org.example.kitchenorganizer.login: This package contains the LoginController class, which manages the user
authentication process and login UI. It interacts with the FXML files for the login form.

- org.example.kitchenorganizer.mainpage: This package contains the MainPageController class, which manages the display process of the main page and handles user inputs. It interacts with the FXML files for the main page.

- org.example.kitchenorganizer: This package contains the main classes and controllers for the application.
The App class serves as the entry point for the JavaFX application. The MainPageController class manages the main page UI
and displays food items from the user's inventory.


Where are the classes that satisfy the “Code Implementation” requirements?

- Food.java
- FoodCollection.java
- InventoryItem.java
- User.java
- Notification.java
- LoginController.java


How does one run the application?

1. Extract the Zip File: First, extract the zip file to a location on your computer where you want to work with the project.

2. Open the Project in Your IDE: Open your preferred Integrated Development Environment (IDE) such as IntelliJ IDEA.

3. Import the Project: Import the project into your IDE

4. Build the Project: Once the project is imported, your IDE should automatically start downloading the required
dependencies specified in the Gradle build files. Wait for the build process to complete.

5. Run the Application: After the build process is finished, you should be able to run the application.
Run the App class, which serves as the entry point for the JavaFX application.

- Login: The application will launch, and you will be presented with a login screen.
Enter the correct username and password (in this case, "user" and "pass") and click the login button.

- Main Page: Upon successful login, you will be directed to the main page of the application.
Here, you can view food items and perform actions based on the application's features.
