Project Milestone 4 – Implementation
Due April 10, 2024 @ 6:00 PM

Requirements:
Take the work you did in Project Milestone 3 and make it functional. The purpose of this milestone is to
get your application into a working and fully functional state. Implement the features that you specified
earlier and ensure that they operate as expected. Go back to Project Milestone 1 and list out the
functional and non-functional requirements you specified there. Make any adjustments as necessary
based on changes in your requirements since then. Then implement the revised set of 6 or more
functional requirements and ensure your application satisfies the 4 non-functional requirements.

Deliverables:
- A README.md or README.txt file that answers the following questions:
- How is the code structured? Where are the classes that satisfy the “Code Implementation” requirements?
- How does one run the application?
- Zip your project folder which should contain all of your code, including the gradle build files and the README and submit the zip file to D2L.

Deliverables:
- A README.md or README.txt file specifying the following:
- Each functional requirement that this version of the application implements. There should be at least 6 functional requirements. For each of these requirements, explain how one can test that the program satisfies that requirement.
- Each non-functional requirement. There should be at least four non-functional requirements. For each of these requirements explain how you have ensured that it is met in your application.
- Zip your project folder which should contain all of your code, including the gradle build files and the README and submit the zip file to D2L.

*****************************************************************************************************
Update Milestone 3 README based on above instructions and updated code in project:

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

*****************************************************************************************************
Milestone 4: A README.md or README.txt file specifying the following:

1. Each functional requirement that this version of the application implements. There
should be at least 6 functional requirements. For each of these requirements, explain
how one can test that the program satisfies that requirement.

2. Each non-functional requirement. There should be at least four non-functional
requirements. For each of these requirements explain how you have ensured that it is
met in your application.
