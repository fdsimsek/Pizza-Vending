//Firat Deniz Simsek No: 3079410
package application;

//Standard Javafx imports
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

//imports for reading/writing files
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

//Imports for concurrency. (One-off Task)
import javafx.concurrent.Task;

//Imports for Geometry
import javafx.geometry.HPos;
import javafx.geometry.Insets;

//Imports for Components
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;

//Image support
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//Imports for layout.
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class PizzaVending extends Application {
	// declare components that require class
	Label lblMenuHeader, lblOrder, lblTotal, lblComplete, lblMade, lblName;
	
	ImageView pizza;

	ListView lvPizzaList;
	ListView lvOrderList;

	HashMap<String, Double> hmap = new HashMap<String, Double>();

	Button btnAdd, btnRemove, btnClear, btnPay, btnAlertClose;

	double totalCost = 0;

	ProgressBar progBar;
	ProgressIndicator progInd;	
	// the One-off Task
	Task<Void> task;

	//Constructor - instantiate variables
	public PizzaVending() {
		lblMenuHeader = new Label("Pizza Vending Machine - Create Order");
		lblComplete = new Label("Order Complete");
		lblOrder = new Label("");
		lblTotal = new Label("Total: €0.00 ");
		lblMade = new Label("Made by");
		lblName = new Label("Firat Deniz Simsek");


		lvPizzaList = new ListView();
		lvOrderList = new ListView();

		btnAdd = new Button("Add to Order");
		btnRemove = new Button("Remove from Order");
		btnClear = new Button("Clear Order");
		btnPay = new Button("Pay");
		btnAlertClose = new Button("Close");
		
		progInd = new ProgressIndicator(0);
		progBar= new ProgressBar(0);
		
		progInd.setScaleX(1.1);
		progInd.setScaleY(1.1);
		progBar.setStyle("-fx-accent: green;");
		
		progBar.setVisible(false);
		lblComplete.setVisible(false);
		
		pizza = new ImageView();

	}// Constructor

	@Override
	public void init() {

		// Event handling...

		btnAdd.setOnAction(event -> {
			// get the pizza selected
			if (lvPizzaList.getSelectionModel().getSelectedIndex() != -1) {
				AddItemToList(lvPizzaList.getSelectionModel().getSelectedItem().toString());
				// display order status
				lblOrder.setText("Adding to Order..");
			} else {
				// display no pizza selected
				lblOrder.setVisible(true);
				lblOrder.setText("No pizza selected from menu.");
			}

		});// btnAdd
		btnRemove.setOnAction(event -> {
			// get the pizza selected
			if (lvOrderList.getSelectionModel().getSelectedIndex() != -1) {
				String selectedItem = lvOrderList.getSelectionModel().getSelectedItem().toString();
				RemoveItemFromList(selectedItem);
				// display removed order
				lblOrder.setText(selectedItem  + " Removed");
			} else {
				// display no pizza selected
				lblOrder.setVisible(true);
				lblOrder.setText("No pizza selected to remove.");

			}
		});// btnRemove

		btnClear.setOnAction(event -> {
			lblOrder.setText("Order cleared");
			ClearList();
		});// btnClear

		btnPay.setOnAction(event -> {
			if (lvPizzaList.getSelectionModel().getSelectedIndex() != -1) {
			lblOrder.setText("Follow instruction for payment.");
			payDialog();
			} else {
				// display no pizza selected
				lblOrder.setVisible(true);
				lblOrder.setText("No pizza selected from menu.");
			}
		});// btnPay
	}

	// method to add order into orderlist
	private void AddItemToList(String name) {
		lblComplete.setVisible(false);
		lblOrder.setVisible(true);
		// add order
		lvOrderList.getItems().add(name);
		// increase this order's price from total
		totalCost += hmap.get(name);
		// display new total price
		lblTotal.setText("Total: €" + totalCost);
	}

	// method to remove order into orderlist
	private void RemoveItemFromList(String name) {
		lblComplete.setVisible(false);
		lblOrder.setVisible(true);
		// this int to select orders into Orderlist
		final int selectedIndex = lvOrderList.getSelectionModel().getSelectedIndex();
		// remove order
		lvOrderList.getItems().remove(selectedIndex);
		// decrease this order's price from total
		totalCost -= hmap.get(name);
		// display new total price
		lblTotal.setText("Total: €" + totalCost);

	}

	// method to clear order into orderlist
	private void ClearList() {
		lblComplete.setVisible(false);
		lblOrder.setVisible(true);
		// clear order list
		lvOrderList.getItems().clear();
		// total cost = 0
		totalCost = 0.00;
		// display new total price
		lblTotal.setText("Total: €0.00");
	}

	// Show payment dialog method
	private void payDialog() {
		lblComplete.setVisible(false);
		lblOrder.setVisible(true);
		// use an alert for this dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Payment");
		alert.setHeaderText("Please confirm to Pay.");
		alert.setContentText("Total: €" + totalCost);
		
		// optional: add a custom img to our alert
		Image img = new Image("./Assests/pay.png"); // !!!
		ImageView imView = new ImageView(img);
		//setting image
		alert.setGraphic(imView);
		alert.setResizable(true);
		// define something to OK button into alert box
		Optional<ButtonType> result = alert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			progBar.setVisible(true);
			
			preparingOrder();
			//clear total cost and orderlist after payment complete
			ClearList();
			System.out.println("Ok has been clicked.");
			System.out.println(totalCost);
		}

	
	}// payDialog()

	// method to read the orderlist file and populate the lv
	private void readPizzaList(String contactsFile) {
		// read in names from the coontacts file & populate the lv
		try {
			String line; // store lines form the file
			FileReader fr = new FileReader(contactsFile);
			BufferedReader buf = new BufferedReader(fr);

			// BufferedReader buf = new BufferedReader (new FileReader(contactsFile));

			// iterate thru file, read 1 line at a time
			while ((line = buf.readLine()) != null) {

				// convert to second element string to double
				hmap.put(line.split(":")[0], Double.parseDouble(line.split(":")[1]));
				// add just the pizza names to our lv
				lvPizzaList.getItems().add(line.split(":")[0]);
			} // while

			buf.close();

		} catch (IOException ioe) {
			System.out.println("No file found called: " + contactsFile);
		}
	}// readPizzaList()

	// method to preparing orders after payment
	private void preparingOrder() {
		lblOrder.setText("Preparing your order...");
		task = new Task<Void>() {
			@Override
			public Void call() {
			
				// the functionality for the thread
				final long max = 100000000;
				// loop the simulate a long task
				for(long i =1; i <= max; i++) {
					
					// update the prog of the task
					updateProgress(i,max);
				}//for
				lblOrder.setVisible(false);
				progBar.setVisible(false);
				lblComplete.setVisible(true);
				return null;
			}//call
			
		};//task
	
		//update the progress of the prog  indicator
		// by binding it to the task progress
		progBar.progressProperty().bind(task.progressProperty());
	
		// now start the thread
		new Thread(task).start();

		
	}
	
	@Override
	public void start(Stage primaryStage) {
		// set the title
		primaryStage.setTitle("Add to your order and press Pay to bake pizza");
		
		//Add an icon.
		primaryStage.getIcons().add(new Image("./Assests/pizza.png"));

		// set the width and height.
		primaryStage.setHeight(500);
		primaryStage.setWidth(780);

		// Set up a layout (bpMain)
		BorderPane bpMain = new BorderPane();

		// Grid Pane for Left side
		GridPane gpleft = new GridPane();
		// Grid Pane for Right side
		GridPane gpright = new GridPane();
		// Grid Pane for Center
		GridPane gpcenter = new GridPane();

		// Define image
		Image img = new Image("./Assests/chef.png"); // !!!
		pizza = new ImageView(img);
		
		// Image size
		pizza.setFitWidth(160);
		pizza.setFitHeight(200);
		
		// Add components to the layout
		bpMain.setTop(lblMenuHeader);
		bpMain.setLeft(gpleft);
		bpMain.setRight(gpright);
		bpMain.setCenter(gpcenter);

		// Set gap and padding
		gpleft.setHgap(10); // space between colms
		gpleft.setVgap(10); // space between rows
		gpleft.setPadding(new Insets(10));

		gpright.setHgap(10); // space between colms
		gpright.setVgap(10); // space between rows
		gpright.setPadding(new Insets(10));

		gpcenter.setHgap(10); // space between colms
		gpcenter.setVgap(10); // space between rows
		gpcenter.setPadding(new Insets(20));

		// padding for vbMain
		bpMain.setPadding(new Insets(10));

		// Components of left side
		gpleft.add(lvPizzaList, 0, 0);
		gpleft.add(lblOrder, 0, 1);
		gpleft.add(progBar, 0, 1);
		gpleft.add(lblComplete, 0, 1);
		gpleft.setHalignment(progBar, HPos.RIGHT);

		// Compoents of right side
		gpright.add(lvOrderList, 0, 0);
		gpright.add(btnPay, 0, 1);
		gpright.add(lblTotal, 0, 1);
		gpright.setHalignment(lblTotal, HPos.CENTER);
		gpright.setHalignment(btnPay, HPos.RIGHT);
		// Components of center
		gpcenter.add(btnAdd, 0, 0);
		gpcenter.add(btnRemove, 0, 1);
		gpcenter.add(btnClear, 0, 2);
		gpcenter.add(pizza, 0, 3);
		gpcenter.add(lblMade, 0, 4);
		gpcenter.add(lblName, 0, 5);
		gpcenter.setHalignment(lblMade, HPos.CENTER);
		gpcenter.setHalignment(lblName, HPos.CENTER);
		gpcenter.setHalignment(pizza, HPos.CENTER);
		gpcenter.setHalignment(btnRemove, HPos.CENTER);
		gpcenter.setHalignment(btnClear, HPos.CENTER);
		gpcenter.setHalignment(btnAdd, HPos.CENTER);
		// Create a scene
		Scene s = new Scene(bpMain); // take in main container

		// Set the scene
		primaryStage.setScene(s);

		// populate the listview with pizza names
		readPizzaList("./Assests/orderlist.csv");
		
		// Apply a style using a style sheet 
		s.getStylesheets().add("style_pizzaVending.css");

		// Show the primary stage
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
