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
TODO: Update Milestone 3's README for Milestone 4 based on above instructions and updated code in project:

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

- Login: The application will launch, and you will be presented with a login screen. Either create an account or log in to an existing account.

- Main Page: Upon successful login, you will be directed to the main page of the application.
Here, you can view food items and perform actions based on the application's features.

*****************************************************************************************************
Milestone 4: A README.md or README.txt file specifying the following:

1. Each functional requirement that this version of the application implements. There
should be at least 6 functional requirements. For each of these requirements, explain
how one can test that the program satisfies that requirement.

- Expiration Date Watcher: The app allows users to add an expiration
date to each food item that can be used to alert the user.
Foods change color based on their availability. Red foods are expired and yellow
foods have a low quantity.
Test: One can test by ensuring the food color is correct when adding a new food, changing the expiration date,
or changing the quantity of food. 

- Food Stock: The app allows users to increase/decrease quantity, delete, and update
the foods in their inventory.
Test: One can test by changing the food quantity, expiration date, min quantity, and deleting the food
to ensure the food updates properly.

- Alerts: The app displays a pop-up telling users which foods are running low
or have expired. Users can choose to check individual collections or all inventories.
Test: One can test by checking that the results for the "Check Current Collection's Inventory" and
The "Check All Inventory" buttons display the correct results.

- Search and Sort: Users can search for specific foods in their inventory. Users can also sort
the display order based on either name or expiration.
Test: One can test by changing the value in the "Sort By" combo box and ensuring the foods are sorted properly.
Search can be tested by typing something into the search bar, clicking 'Search',
and ensuring all foods containing the characters entered are displayed.

- Add New Food Feature: Users will be able to add new types of foods into the app’s database.
Test: One can test by using the "Add New Item" button to ensure new foods are added properly.

- Multiple Kitchens: Users can create multiple kitchens in the app so they can keep track
of items in multiple kitchens.
Test: One can test by using the "Add Collection" and "Remove Collection" buttons and checking that collections are added and removed properly.
Also, ensure that the "Select Kitchen Collection" combo box changes collections properly.

2. Each non-functional requirement. There should be at least four non-functional
requirements. For each of these requirements explain how you have ensured that it is
met in your application.

- Data Integrity (Ensure data is recorded exactly as intended.): Our app stores expiration dates of foods. Users enter the expiration date as the number of days until expiration, but in the database, it's stored as a date to ensure the expiration date is always up to date. When the user retrieves from the database, the date is turned into and displayed as the number of days until the expiration date. Additionally, the user cannot enter a negative number for the expiration date, a non-positive number for quantity, or a negative number for minimum quantity. We also ensure that the user cannot enter the incorrect data type for fields.
  
- Volume Test Run: The app displays a limited number of food objects at a time to ensure that large quantities of food objects do not significantly impact performance. We have tested the app with thousands of randomly generated foods in multiple collections to ensure that the app performance is acceptable.

- Usability: All buttons, text fields, and combo boxes are clearly labeled, and all features that need explaining are explained in the help dialog after pressing the 'Help' button. Additionally, whenever an error occurs, there's an explanation for why the error occurred, and what the user can do to prevent the error from occurring again.
  
- Portability: Our app can run on multiple operating systems including Windows, MacOS, and Linux with no modifications so it is portable.
