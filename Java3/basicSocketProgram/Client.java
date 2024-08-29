// Homework 7: GUI + OOP + Threads + Network + Database programming
// Course: CIS 357
// Due date: August 15, 2024
// Name: Lukas A. White
// Instructor: Il-Hyung Cho
// Program description: This is the client class that communicates with a surver to access
// and use an sql database. This class inputs things like item code and quantity into a
// javaFX GUI display to help guide the user on what to do.

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;


/**
 * JavaFX application for connecting to a server, managing sales, and displaying receipts.
 * @author LukasWhite
 * @version 8/11/2024
 */
public class Client extends Application {
	
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private double totalAmount = 0.0;
    private StringBuilder receiptBuilder = new StringBuilder();

    
    /**
     * Initializes the application and sets up the connection screen.
     * This is needed for connections for other IP addresses
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client");

        // Creates the connection screen
        Label ipLabel = new Label("Server IP Address:");
        TextField ipField = new TextField("127.0.0.1"); // Not giving you my IP address
        Button connectButton = new Button("Connect");

        VBox connectionLayout = new VBox(10, ipLabel, ipField, connectButton);
        Scene connectionScene = new Scene(connectionLayout, 300, 150);

        // Set the event handler for the connect button
        connectButton.setOnAction(e -> {
            String ip = ipField.getText();
            if (connectToServer(ip)) {
                showWelcomeScreen(primaryStage);
            }
        });

        primaryStage.setScene(connectionScene);
        primaryStage.show();
    }
    
    
    /**
     * Main 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    
    /**
     * Connects to the server with the given IP address.
     * @param ip The server IP address.
     * @return True if the connection is successful, false otherwise.
     */
    private boolean connectToServer(String ip) {
        try {
            socket = new Socket(ip, 8001);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // how did this work first try???
            System.out.println("Connected to server at " + ip);
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Connection Error", "Unable to connect to the server.");
            return false;
        }
    }

    
    /**
     * Displays the welcome screen with a button to start a new sale.
     * @param primaryStage The primary stage for this application.
     */
    private void showWelcomeScreen(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome To White's Shop");
        Button newSaleButton = new Button("New Sale");

        VBox welcomeLayout = new VBox(20, welcomeLabel, newSaleButton);
        welcomeLayout.setAlignment(javafx.geometry.Pos.CENTER);
        Scene welcomeScene = new Scene(welcomeLayout, 400, 200);

        newSaleButton.setOnAction(e -> showMainScreen(primaryStage));

        primaryStage.setScene(welcomeScene);
    }

    /**
     * Displays the main sales screen with components to add items and pay.
     * @param primaryStage The primary stage for this application.
     */
    private void showMainScreen(Stage primaryStage) {
    	
        // Fun GUI, could not get FXML file working, too lazy.
        Label itemCodeLabel = new Label("Item Code:");
        TextField itemCodeField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();
        Button addButton = new Button("Add Item");
        Button payButton = new Button("Pay");
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(javafx.scene.text.Font.font("Courier New"));

        // cannot centeralize just the buttons... oh well
        
        VBox mainLayout = new VBox(10, itemCodeLabel, itemCodeField, quantityLabel, quantityField, addButton, receiptArea, payButton);
        Scene mainScene = new Scene(mainLayout, 500, 300);

        // Add button, please fix glitches tomorrow, no item is found
        addButton.setOnAction(e -> {
            String itemCode = itemCodeField.getText();
            try {
            	
            	
            	
                int quantity = Integer.parseInt(quantityField.getText());
                addItemToReceipt(itemCode, quantity, receiptArea);
                
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Quantity must be a number.");
            }
        });

        // pay button
        payButton.setOnAction(e -> {
            showReceiptWindow(primaryStage);
        });

        primaryStage.setScene(mainScene);
    }

    
    /**
     * Adds an item to the receipt and updates the total amount.
     * @param itemCode The code of the item.
     * @param quantity The quantity of the item.
     * @param receiptArea The text area where the receipt is displayed.
     */
    private void addItemToReceipt(String itemCode, int quantity, TextArea receiptArea) {
        try {
        	// multiple items cannot be stored???
            // Send item code and quantity to the server
            out.writeObject(itemCode);
            out.flush();  // Ensure the object is sent immediately
            out.writeObject(quantity);
            out.flush();  // Ensure the object is sent immediately

            // Receive response from the server
            Object response = in.readObject();

            // makes sure that the server's subclass is set up properly
            if (response instanceof ProductSpec) {
                ProductSpec productSpec = (ProductSpec) response;
                double subTotal = productSpec.getPrice() * quantity;
                double total = subTotal;

                // Apply 6% sales tax if the item code does not start with 'E' or 'e'
                // cannot mess up the tax again
                if (!itemCode.toUpperCase().startsWith("E")) {
                    total += subTotal * 0.06;
                }

                totalAmount += total;

                // formatting hurts me brain. I am a pirate, me treasure ahoy.
                String line = String.format("%s, %s, %d, $%.2f, $%.2f\n",
                        productSpec.getName(), productSpec.getDescription(), quantity, subTotal, total);
                receiptBuilder.append(line);
                receiptArea.appendText(line);
                
            } else if (response instanceof String) {
                String message = (String) response;
                
                // Checks the input error. Needed to solve the issue of
                // me setting the SQL file up incorrectly
                if (message.equals("Invalid item code.")) {
                    receiptArea.appendText("Invalid item code.\n");
                } else {
                    receiptArea.appendText("Unknown error occurred.\n");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add item."); // I hate seeing this error!!!!!1
        }
    }

    
    /**
     * Shows a new window with the receipt and the total amount.
     * @param primaryStage The primary stage for this application.
     */
    private void showReceiptWindow(Stage primaryStage) {
        // Create the receipt window GUI
        Stage receiptStage = new Stage();
        receiptStage.setTitle("Receipt");

        Label thankYouLabel = new Label("Thanks for your payment!");
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(javafx.scene.text.Font.font("Courier New"));
        receiptArea.setText(receiptBuilder.toString() + String.format("Total: $%.2f", totalAmount));

        VBox receiptLayout = new VBox(10, thankYouLabel, receiptArea);
        receiptLayout.setPadding(new javafx.geometry.Insets(10));
        Scene receiptScene = new Scene(receiptLayout, 400, 300);

        receiptStage.setScene(receiptScene);
        receiptStage.show();

        // Resets so no order's carry over, we are a proper company. No fraud
        totalAmount = 0.0;
        receiptBuilder.setLength(0);

        // back to the welcome screen
        receiptStage.setOnHiding(e -> showWelcomeScreen(primaryStage));
    }

    /**
     * Displays an alert with a specified title and message.
     * @param title The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


