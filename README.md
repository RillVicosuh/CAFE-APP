# Cafe App

This is a simple CLI application built in Java. It allows users to interact with a fictional cafe, allowing them to view and modify orders, and providing additional functionality for employees and managers.

## Setup

To run the application, follow these steps:

1. Make sure you have Java installed on your system.
2. Compile the Java files using the command: `javac *.java`
3. Run the application using the command: `java Cafe`

## Features

The app offers a variety of features including:

1. Login as a customer, employee, or manager.
2. View and modify orders.
3. Update non-paid orders to paid orders (exclusive to employees and managers).
4. View non-paid orders from the past 24 hours (exclusive to employees and managers).

## Code Structure

The code is structured into four main parts:

1. The `Cafe` class: This is the main class that runs the application. It has several static methods to handle user interactions and database transactions.
2. The `userType` method: This method is used to determine the type of user (customer, employee, or manager) based on their login information.
3. The `FindItem` and `FindPrice` methods: These methods are used to search for items in the database and retrieve their prices.
4. The `UpdateOrder` and `OrderIDisFalse` methods: These methods handle the process of updating orders, including adding and removing items, canceling orders, and changing orders' payment status.

## Important Methods

### userType
This method returns the type of user (customer, employee, or manager) based on their login information.

### FindItem
This method checks if an item exists in the database. It returns true if the item exists, false otherwise.

### FindPrice
This method retrieves the price of a specified item from the database.

### OrderIDisFalse
This method checks if a given order is not paid. It returns true if the order is not paid, false otherwise.

### UpdateOrder
This method is used to update an order. It provides a range of functionalities including:
- Adding items to the order
- Deleting items from the order
- Canceling the whole order
- Changing the order's payment status from non-paid to paid

### PlaceOrder
This method is used to place a new order. It prompts the user to input the items they want to order and adds these items to the database as a new order with the status set as non-paid.

### userProfileUpdate
This method is used by customers to update their profile information. The user can change their details like name, password, etc. The changes are updated in the database.

### managerProfileUpdate
This method is similar to `userProfileUpdate`, but it's specifically for managers. It allows managers to update their profile information.

### managerMenuUpdate
This method allows managers to update the cafe's menu. They can add new items, remove existing items, or modify the details of items.

### browseType
This method allows users to browse the menu by the type of item. The user inputs a type, and the method retrieves and displays all items of that type.

### browseName
This method is similar to `browseType`, but it allows users to search for items by name. The user inputs a name, and the method retrieves and displays all items that match the name.

## Error Handling

The application includes basic error handling. It catches exceptions that might occur during database transactions and prints the error message to the console.

## Limitations

1. The application only runs in a console window. There's no graphical user interface (GUI).
2. It's currently configured to work with a specific database schema. Any changes in the schema would require corresponding changes in the code.

## Future Work

Potential improvements and additions could include:

1. Expanding the `PlaceOrder` functionality to include special requests or modifications to the order.
2. Adding more detailed error messages and validations to the `userProfileUpdate` and `managerProfileUpdate` methods to ensure data integrity.
3. Enhancing the `managerMenuUpdate` function to allow bulk updates or to schedule menu changes in advance.
4. Implementing a more flexible and powerful search functionality in the `browseType` and `browseName` methods, such as allowing search by multiple criteria or adding autocomplete suggestions.
5. Developing a GUI to improve user experience, improving error handling, and implementing a more flexible design to handle changes in the database schema.
