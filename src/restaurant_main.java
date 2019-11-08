import javafx.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class restaurant_main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	// GLOBAL VARIABLES
	BorderPane root = new BorderPane(); // will hold everything of our program
	VBox mainMenu = new VBox(30);
	VBox addVisit = new VBox(30);
	VBox login = new VBox(30);
	VBox register=new VBox(30); 
	VBox reviewedMenu=new VBox(30);
	VBox currReviewPage=new VBox(30); 
	VBox addPicture=new VBox(30); 
	VBox editReview=new VBox(30); 
	Button submitLoginB = new Button("Submit");
	Button registerLoginB = new Button("Register");
	Button submitRegister=new Button("Submit");
	Button backToMainB=new Button("Back to Main Menu");
	restaurant_dataBase_Connector connectDB = new restaurant_dataBase_Connector();
	private ResultSet currUser;
	private String currEmail,currUserName;

	public void start(Stage primaryStage) throws Exception {
		createLoginMenu(login);
		
		createAddVisitMenu(addVisit);

		root.setCenter(login);
		root.setPadding(new Insets(10)); // set borders within root sectionsn
		login.setAlignment(Pos.CENTER);
		Scene myScene = new Scene(root, 1200, 400);

		primaryStage.setScene(myScene);
		primaryStage.setTitle("Restaurant History");
		// primaryStage.setFullScreen(true);
		primaryStage.show();

		// BUTTON ACTION EVENTS

	}

	public void createMainMenu(VBox start) {
		Label welcome=new Label("Welcome to "+currUserName+"'s Restaurant History");;
		start.setAlignment(Pos.CENTER);
		Label selectL = new Label("Please select a button to perform the following actions");
		VBox startSelections = new VBox(10);
		startSelections.setAlignment(Pos.CENTER);
		Button viewB = new Button("View Reviewed Places/Pics");
		Button addB = new Button("Add new visit");
		Button addPic = new Button("Add food pic");

		startSelections.getChildren().addAll(viewB, addB, addPic);
		start.getChildren().addAll(welcome, selectL, startSelections);

		addB.setOnAction(event -> {
			root.setCenter(addVisit);
			root.setTop(backToMainB);
		});
		viewB.setOnAction(event->{
			reviewedMenu=new VBox(30);
			createReviewedMenu(reviewedMenu);
			root.setCenter(reviewedMenu);
		});
		backToMainB.setOnAction(event->{
			root.setCenter(mainMenu);
			root.setTop(null);
		});
	}

	// menu for creating new restaurant reviews/comments
	public void createAddVisitMenu(VBox visit) 
	{
		GridPane menu = new GridPane();
		HBox radioBoxes = new HBox(10);
		Label restL = new Label("Restaurant Name: ");
		TextField restField = new TextField("Please enter the restaurant name here");

		Label rateL = new Label("Please check a rating where 1 is the worst, 5 is the best: ");
		Button backB=new Button("Cancel"); 
		RadioButton one = new RadioButton("1");
		RadioButton two = new RadioButton("2");
		RadioButton three = new RadioButton("3");
		RadioButton four = new RadioButton("4");
		RadioButton five = new RadioButton("5");
		ToggleGroup radioBoxTog = new ToggleGroup();
		int[] rating= {0}; 
		one.setToggleGroup(radioBoxTog);
		one.setOnAction(e->{rating[0]=1;});
		two.setToggleGroup(radioBoxTog);
		two.setOnAction(e->{rating[0]=2;});
		three.setToggleGroup(radioBoxTog);
		three.setOnAction(e->{rating[0]=3;});
		four.setToggleGroup(radioBoxTog);
		four.setOnAction(e->{rating[0]=4;});
		five.setToggleGroup(radioBoxTog);
		five.setOnAction(e->{rating[0]=5;});  
		       
		
		radioBoxes.getChildren().addAll(one, two, three, four, five);

		Label commentL = new Label("Please enter comments: ");
		Label charCount=new Label("0/500"); 
		TextArea commentField = new TextArea("Max 500 chars");
		VBox commentFieldBox=new VBox(commentField,charCount);
		charCount.textProperty().bind(Bindings.length(commentField.textProperty()).asString("String Length: %d"+"/500")); //will show char length of text area as type
		commentField.setWrapText(true);  //alows text to go to new line when reach edge
		commentField.setTextFormatter(new TextFormatter<String>(change ->  //set max char limit to 500
         change.getControlNewText().length() <= 500 ? change : null));

		menu.add(restL, 0, 0);
		menu.add(restField, 1, 0);
		menu.add(rateL, 0, 1);
		menu.add(radioBoxes, 1, 1);
		menu.add(commentL, 0, 2);
		menu.add(commentFieldBox, 1, 2);
		menu.setPadding(new Insets(10));
		
		Button submitReview=new Button("Submit"); 
		HBox submitBox=new HBox(10,submitReview); 
		
		
		
		submitBox.setAlignment(Pos.CENTER);submitBox.setPadding(new Insets(20));
		visit.getChildren().addAll(menu,submitBox);
		menu.setAlignment(Pos.CENTER);
		visit.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(20));
		menu.setHgap(10); // horizontal gap in pixels
		menu.setVgap(10);
		
		submitReview.setOnAction(event->{
			try 
			{

				
				String sql="INSERT INTO reviews(userEmail,Rating,Comments,Tags,restaurantName) VALUES ('"+currEmail+"',"+
							rating[0]+",'"+commentField.getText()+"','"+"','"+restField.getText()+"');";
				connectDB.executeStatement(sql);
				
				Alert successA=new Alert(AlertType.INFORMATION);
				successA.setTitle("Success");successA.setContentText("The review was successfully uploaded!"); 
				successA.showAndWait(); 
				restField.setText("Please enter the restaurant name here");
				commentField.setText("Max 500 chars");
				root.setCenter(mainMenu);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		});
		backB.setOnAction(event->{
			root.setCenter(mainMenu);
		});
	}

	// creation of login allows user to enter their account information
	public void createLoginMenu(VBox login) {
		login.setAlignment(Pos.CENTER);
		Label welcomeL = new Label("Welcome to the restaurant history tracker");
		GridPane loginHolder = new GridPane();
		loginHolder.setHgap(10);
		loginHolder.setVgap(10);
		loginHolder.setPadding(new Insets(20));
		loginHolder.setAlignment(Pos.CENTER);

		Label loginL = new Label("Please enter the required information");
		Label userL = new Label("Please enter your email here: ");
		TextField userField = new TextField("enter email");
		Label passL = new Label("Please enter your password here: ");
		PasswordField passField = new PasswordField();
		passField.setPromptText("enter password");
		loginHolder.add(userL, 0, 0);
		loginHolder.add(userField, 1, 0);
		loginHolder.add(passL, 0, 1);
		loginHolder.add(passField, 1, 1);

		HBox loginButtons = new HBox(10);
		loginButtons.setAlignment(Pos.CENTER);
		submitLoginB.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		registerLoginB.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		final Pane buttonGap = new Pane();
		buttonGap.setMinSize(5, 1);
		loginButtons.getChildren().addAll(submitLoginB, buttonGap, registerLoginB);
		login.getChildren().addAll(welcomeL, loginL, loginHolder, loginButtons);

		// EVENT HANDLERS FOR LOGIN BUTTONS
		submitLoginB.setOnAction(event -> {
			String sqlStatement = "Select * from login where  email='" + userField.getText() + "' and password='"  //check if info is in database 
					+ passField.getText() + "'";
			
			currUser = connectDB.query(sqlStatement);
			try {
				if (currUser.next()) 
				{
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Login successfull");                   //if found in database then account is good 
					alert.setContentText("LOGIN SUCCESSFULL...");
					alert.showAndWait();
					currEmail=currUser.getString("email");
					currUserName=currUser.getString("firstName");
					createMainMenu(mainMenu);                
					root.setCenter(mainMenu);
				} 
				else //otherwise we let user know account not in DB
				{ 
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Login unsuccessfull");
					alert.setContentText("Username or password is incorrect. Try again.");
					alert.setHeaderText("UNSUCCESSFULL");
					alert.showAndWait();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		registerLoginB.setOnAction(event->
		{
			createRegisterMenu(register);
			root.setCenter(register);
			
			
		});
		
	}
	//creates menu for creating new account 
	public void createRegisterMenu(VBox register)
	{
		Label registerL=new Label("Please enter the required information below, and hit the submit button.");
		GridPane registerBox=new GridPane();
		registerBox.setVgap(10);registerBox.setHgap(10);
		registerBox.setPadding(new Insets(10));
		Label emailL=new Label("Enter email address: ");
		TextField emailBox=new TextField("email...");
		Label passL=new Label("Enter password: ");
		PasswordField passBox=new PasswordField();
		passBox.setPromptText("password...");
		Label firstNameL=new Label("Enter first name: ");
		TextField firstNameBox=new TextField("first name...");
		Label lastNameBoxL= new Label("Enter last name: ");
		TextField lastNameBox=new TextField("last name...");
		registerBox.add(emailL, 0, 0);registerBox.add(emailBox, 1, 0);
		registerBox.add(passL, 0, 1);registerBox.add(passBox, 1, 1);
		registerBox.add(firstNameL, 0, 2);registerBox.add(firstNameBox, 1, 2);
		registerBox.add(lastNameBoxL, 0, 3);registerBox.add(lastNameBox, 1, 3);
		register.getChildren().addAll(registerL,registerBox,submitRegister);
		
		submitRegister.setOnAction(event->  //when submit pressed
		{
			String sql="Select email FROM login WHERE email='"+emailBox.getText()+"'"; //check if email exists in account
			ResultSet result=connectDB.query(sql); 
			
			try
			{
				if(result.next()==false) //if it does not then we let user create account
				{
					sql="INSERT INTO login(email,password,firstName,lastName) VALUES ('"+emailBox.getText()+"','"+passBox.getText()+"','"+firstNameBox.getText()+"','"+lastNameBox.getText()+"');";
					
					//connectDB.executeStatement(query);
					connectDB.executeStatement(sql);
					Alert success=new Alert(AlertType.INFORMATION);
					success.setContentText("Account creation successfull");
					success.showAndWait(); 
					root.setCenter(login);
				}
				else  //otherwise we reject email and no account created
				{
					Alert exists=new Alert(AlertType.INFORMATION);
					exists.setContentText("Email address is already in use!");
					exists.showAndWait();
				}
			} catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
	}
	public void createReviewedMenu(VBox reviewedContainer)
	{
		Label reviewL=new Label("Your Reviews: "); 
		Button backMainB=new Button("Back to Main Menu");
		GridPane reviewHolder=new GridPane(); 
		reviewHolder.setHgap(10);reviewHolder.setVgap(10);
		reviewHolder.setPadding(new Insets(20));
		reviewHolder.setAlignment(Pos.CENTER);
		
		try {
			String queryUserReviews="Select * FROM reviews where userEmail= '"+currEmail+"'";
			ResultSet reviews=connectDB.query(queryUserReviews); 
			int counter=0; 
			int row=0;
			Button restaurantNameReview;
			while(reviews.next()) 
			{
				restaurantNameReview=new Button(reviews.getString("restaurantName"));
				
				reviewHolder.add(restaurantNameReview, row, counter);
				counter++;
				if(counter==3)
				{
					row++;
					counter=0; 
				}
				restaurantNameReview.setOnAction(event->{  //create review page and set root center to curr review
					createCurrReviewPage(((Button)event.getSource()).getText());
				});
			}
			
			reviewedContainer.getChildren().addAll(reviewL,reviewHolder);
			root.setTop(backMainB);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		backMainB.setOnAction(event->{
			root.setCenter(mainMenu);
			root.setTop(null);
		});
		
	}
	public void createCurrReviewPage(String restaurantName)
	{
		//System.out.println("HI "+restaurantName);
		currReviewPage=new VBox(30); 
		Button backToReviewsB=new Button("Back to Reviews menu"); 
		Button editB=new Button("Edit"); 
		Button addImage=new Button("Add Image");
		Button removeB=new Button("Remove review"); 
		Button addPicture=new Button("Add Pictures"); 
		HBox buttonHolder=new HBox(10,backToReviewsB,editB,addImage); 
		buttonHolder.setAlignment(Pos.CENTER);buttonHolder.setPadding(new Insets(10));
		GridPane reviewInfoHolder=new GridPane(); 
		reviewInfoHolder.setVgap(10);reviewInfoHolder.setHgap(10);
		reviewInfoHolder.setPadding(new Insets(10));
		Label restaurantL=new Label(currUserName+"'s review for: "+restaurantName);
		Label ratingScore=new Label("Rating Given: ");
		Label numScore=new Label();
		String sql="SELECT * FROM reviews WHERE reviews.userEmail='"+currEmail+"' AND reviews.restaurantName='"+restaurantName+"';";
		Label textAreaL=new Label("Comments: ");
		TextArea comments=new TextArea(); comments.setDisable(true);
		comments.setWrapText(true);
		ResultSet result=connectDB.query(sql); 
		String removeSql="DELETE FROM Reviews WHERE Reviews.RestaurantName='"+restaurantName+"';";
		root.setBottom(buttonHolder);
		
		try
		{
			if(result.next()) 
			{
				numScore.setText(Integer.toString(result.getInt("rating")));
				comments.setText(result.getString("comments"));
				reviewInfoHolder.add(ratingScore, 0, 0);reviewInfoHolder.add(numScore, 1, 0);
				reviewInfoHolder.add(textAreaL, 0, 1);reviewInfoHolder.add(comments, 1, 1);
				currReviewPage.getChildren().addAll(restaurantL,reviewInfoHolder,buttonHolder);
				root.setCenter(currReviewPage);
			}
			
		}
		
		catch(SQLException e)
		{
			e.printStackTrace(); 
		}
		backToReviewsB.setOnAction(event->{
			root.setCenter(reviewedMenu);
			root.setBottom(null);
		});
		removeB.setOnAction(event->{
			connectDB.executeStatement(removeSql);
			root.setBottom(null);
			reviewedMenu=new VBox(30);
			createReviewedMenu(reviewedMenu);
			root.setCenter(reviewedMenu);
		});
		editB.setOnAction(event->{ //naviage to edit menu
			createEditMenu(restaurantName);
		});
	}
	public void createEditMenu(String restaurant)
	{
		editReview=new VBox(30); 
		GridPane menu = new GridPane();
		HBox radioBoxes = new HBox(10);
		Label restL = new Label("Restaurant Name: ");
		TextField restField = new TextField("Please enter the restaurant name here");

		Label rateL = new Label("Please check a rating where 1 is the worst, 5 is the best: ");
		Button backB=new Button("Cancel"); 
		RadioButton one = new RadioButton("1");
		RadioButton two = new RadioButton("2");
		RadioButton three = new RadioButton("3");
		RadioButton four = new RadioButton("4");
		RadioButton five = new RadioButton("5");
		ToggleGroup radioBoxTog = new ToggleGroup();
		int[] rating= {0}; 
		one.setToggleGroup(radioBoxTog);
		one.setOnAction(e->{rating[0]=1;});
		two.setToggleGroup(radioBoxTog);
		two.setOnAction(e->{rating[0]=2;});
		three.setToggleGroup(radioBoxTog);
		three.setOnAction(e->{rating[0]=3;});
		four.setToggleGroup(radioBoxTog);
		four.setOnAction(e->{rating[0]=4;});
		five.setToggleGroup(radioBoxTog);
		five.setOnAction(e->{rating[0]=5;});  
		       
		
		radioBoxes.getChildren().addAll(one, two, three, four, five);

		Label commentL = new Label("Please enter comments: ");
		Label charCount=new Label("0/500"); 
		TextArea commentField = new TextArea("Max 500 chars");
		VBox commentFieldBox=new VBox(commentField,charCount);
		charCount.textProperty().bind(Bindings.length(commentField.textProperty()).asString("String Length: %d"+"/500")); //will show char length of text area as type
		commentField.setWrapText(true);  //alows text to go to new line when reach edge
		commentField.setTextFormatter(new TextFormatter<String>(change ->  //set max char limit to 500
         change.getControlNewText().length() <= 500 ? change : null));

		menu.add(restL, 0, 0);
		menu.add(restField, 1, 0);
		menu.add(rateL, 0, 1);
		menu.add(radioBoxes, 1, 1);
		menu.add(commentL, 0, 2);
		menu.add(commentFieldBox, 1, 2);
		menu.setPadding(new Insets(10));
		
		Button submitReview=new Button("Submit"); 
		HBox submitBox=new HBox(10,submitReview); 
		editReview.getChildren().addAll(menu,submitBox);
		root.setCenter(editReview);
		
		
		
		
	}
	
	
}