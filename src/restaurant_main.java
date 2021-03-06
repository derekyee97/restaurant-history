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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class restaurant_main extends Application {
	public static void main(String[] args)
	{
		launch(args);
	}
	// GLOBAL VARIABLES
	BorderPane root = new BorderPane(); // will all GUI of the program
	VBox mainMenu = new VBox(30);
	VBox addVisit = new VBox(30);
	VBox login = new VBox(30);
	VBox register=new VBox(30); 
	VBox reviewedMenu=new VBox(30);
			
	Button backToMainB=new Button("Back to Main Menu");
	Stage primary; //used for when need File input 
	restaurant_dataBase_Connector connectDB = new restaurant_dataBase_Connector();
	ResultSet currUser;     
	String currEmail,currUserName;

	public void start(Stage primaryStage) throws Exception 
	{
		createLoginMenu(login);
		createAddVisitMenu(addVisit);
		root.setCenter(login);
		root.setPadding(new Insets(10)); 
		login.setAlignment(Pos.CENTER);
		Scene myScene = new Scene(root, 1600, 600);
		myScene.getStylesheets().add("./restaurant.css");
		primaryStage.setTitle("Restaurant History Application");
		primary=primaryStage;
		primaryStage.setScene(myScene);
		primaryStage.show();

		
	}

	public void createMainMenu(VBox start) 
	{
		Label welcome=new Label("Welcome to "+currUserName+"'s Restaurant History");;
		start.setAlignment(Pos.CENTER);
		Label selectL = new Label("Please select a button to perform the following actions");
		VBox startSelections = new VBox(10);
		startSelections.setAlignment(Pos.CENTER);
		Button viewB = new Button("View Reviewed Places/Pics");
		Button addB = new Button("Add new visit");
		
		startSelections.getChildren().addAll(viewB, addB);
		start.getChildren().addAll(welcome, selectL, startSelections);

		addB.setOnAction(event -> 
		{
			root.setCenter(addVisit);
			root.setTop(backToMainB);
		});
		viewB.setOnAction(event->
		{
			reviewedMenu=new VBox(30);
			createReviewedMenu(reviewedMenu);
			root.setCenter(reviewedMenu);
		});
		backToMainB.setOnAction(event->
		{
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
		menu.setHgap(10); 
		menu.setVgap(10);
		
		submitReview.setOnAction(event->
		{
			//grabbing info from user inputted fields
			try 
			{				
				String check="SELECT * FROM Reviews WHERE useremail='"+currEmail+"' AND restaurantname='"+restField.getText()+"'";
				ResultSet dupe=connectDB.query(check);
				if(dupe.next())
				{
					Alert dupeAlert=new Alert(AlertType.ERROR);
					dupeAlert.setContentText("Already reviewed");
					dupeAlert.setTitle("Duplicate Review");
					dupeAlert.showAndWait(); 
					restField.setText("Please enter the restaurant name here");
					commentField.setText("Max 500 chars");
					root.setCenter(mainMenu);
				}
				else
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
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		});
		backB.setOnAction(event->
		{
			root.setCenter(mainMenu);
		});
	}

	// creation of login allows user to enter their account information
	public void createLoginMenu(VBox login) 
	{
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
		Button submitLoginB = new Button("Submit");
		Button registerLoginB = new Button("Register");
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
			try 
			{
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
			} 
			catch (SQLException e) 
			{
				System.out.println("ERROR with sql");
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
		Button submitRegister=new Button("Submit");
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
				System.out.println("Error sql");
			}
			
		});
	}
	//creating page to get all reviews posted
	public void createReviewedMenu(VBox reviewedContainer)
	{
		Label reviewL=new Label("Your Reviews: "); 
		Button backMainB=new Button("Back to Main Menu");
		GridPane reviewHolder=new GridPane(); 
		reviewHolder.setHgap(10);reviewHolder.setVgap(10);
		reviewHolder.setPadding(new Insets(20));
		reviewHolder.setAlignment(Pos.CENTER);
		reviewHolder.setPrefWidth(50);
		try 
		{
			String queryUserReviews="Select * FROM reviews where userEmail= '"+currEmail+"'";
			ResultSet reviews=connectDB.query(queryUserReviews); 
			int counter=0; 
			int row=0;
			Button restaurantNameReview;   //creates new button for each review
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
				restaurantNameReview.setOnAction(event->
				{  //create review page and set root center to curr review
					createCurrReviewPage(((Button)event.getSource()).getText());
					root.setTop(null);
				});
			}
			
			reviewedContainer.getChildren().addAll(reviewL,reviewHolder);
			root.setTop(backMainB);
			
		} catch (SQLException e)
		{
			System.out.println("Error sql");
		}
		backMainB.setOnAction(event->
		{
			root.setCenter(mainMenu);
			root.setTop(null);
		});
		
	}
	public void createCurrReviewPage(String restaurantName)
	{
		VBox currReviewPage=new VBox(30); 
		currReviewPage=new VBox(30); 
		Button backToReviewsB=new Button("Back to Reviews menu"); 
		Button addImage=new Button("Add Image");
		Button removeB=new Button("Remove review"); 
		Button addPicture=new Button("Add Pictures"); 
		HBox buttonHolder=new HBox(10,backToReviewsB,removeB,addImage); 
		buttonHolder.setAlignment(Pos.CENTER);buttonHolder.setPadding(new Insets(10));
		GridPane reviewInfoHolder=new GridPane(); 
		reviewInfoHolder.setVgap(10);reviewInfoHolder.setHgap(10);
		reviewInfoHolder.setPadding(new Insets(10));
		Label restaurantL=new Label(currUserName+"'s review for: "+restaurantName);
		Label ratingScore=new Label("Rating Given: ");
		Label numScore=new Label();
		String sql="SELECT * FROM reviews WHERE reviews.userEmail='"+currEmail+"' AND reviews.restaurantName='"+restaurantName+"';";
		String removeSql="DELETE FROM Reviews WHERE Reviews.RestaurantName='"+restaurantName+"';";
		String removePicsSql="DELETE FROM food_pictures WHERE userEmail='"+currEmail+"' AND food_pictures.restaurantName='"+restaurantName+"'";
		Label textAreaL=new Label("Comments: ");
		TextArea comments=new TextArea(); comments.setDisable(true);
		comments.setWrapText(true);
		ResultSet result=connectDB.query(sql); 
		root.setBottom(buttonHolder);
		Label imagesUploadedL=new Label("Images uploaded by: "+currUserName);

		try
		{
			if(result.next()) 
			{
				numScore.setText(Integer.toString(result.getInt("rating")));
				comments.setText(result.getString("comments"));
				reviewInfoHolder.add(ratingScore, 0, 0);reviewInfoHolder.add(numScore, 1, 0);
				reviewInfoHolder.add(textAreaL, 0, 1);reviewInfoHolder.add(comments, 1, 1);
				
			}			
		}
		catch(SQLException e)
		{
			System.out.println("Error with sql statement");
		} 
		//getting pictures if any
		sql="SELECT * FROM food_pictures WHERE food_pictures.userEmail='"+currEmail+"' AND food_pictures.restaurantName='"+restaurantName+"';";
		ResultSet images=connectDB.query(sql);
		ImageView a=null;
		GridPane imageHolder=new GridPane(); 
		imageHolder.setVgap(10);imageHolder.setHgap(10);
		imageHolder.setPadding(new Insets(10));
		int col=0,row=0;
		int imageNum=0; 
		try
		{
			while(images.next())
			{
				InputStream is=images.getBinaryStream("image");
				//specific directy where folder stores all restaurant images
				OutputStream os=new FileOutputStream(new File("C:\\Users\\derek\\git\\restaurant-history\\src\\photos\\photo.jpg"));
				byte[] imageContent=new byte[1024];
				int size=0; 
				while((size=is.read(imageContent)) != -1)//read image in database, write content into os, -1 when is nothing left to read
				{
					os.write(imageContent,0,size);
				}
				os.close();
				is.close(); 
				//now got to display on an image view THIS IS LOCAL DIRECTORY
				Image pic=new Image("file:C:\\Users\\derek\\git\\restaurant-history\\src\\photos\\photo.jpg");
				//System.out.println("Image loading error? " + pic.exceptionProperty());

				a=new ImageView(pic);
				a.setFitWidth(100);a.setFitHeight(150);a.setPreserveRatio(true);
				imageHolder.add(a, col, row); //want 3 pictures a row
				col++;
				if(col%3==0)
				{
					col=0;
					row++; 
				}
			}
			
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("File not found");
		} catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			System.out.println("Error with sql statement");
			root.setCenter(mainMenu);
		}
		root.setCenter(currReviewPage);
		currReviewPage.getChildren().addAll(restaurantL,reviewInfoHolder,buttonHolder,imagesUploadedL,imageHolder);
		
		backToReviewsB.setOnAction(event->
		{
			root.setCenter(reviewedMenu);
			root.setTop(backToMainB);
			root.setBottom(null);
		});
		removeB.setOnAction(event->
		{
			connectDB.executeStatement(removeSql);
			connectDB.executeStatement(removePicsSql);
			System.out.println(removePicsSql);
			root.setBottom(null);
			reviewedMenu=new VBox(30);
			createReviewedMenu(reviewedMenu);
			root.setCenter(reviewedMenu);
		});
		addImage.setOnAction(event->
		{
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter JPGFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter PNGFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(JPGFilter,PNGFilter);
        	File imageFile=fileChooser.showOpenDialog(primary);
        	try 
        	{
				PreparedStatement ps=connectDB.connector.prepareStatement("INSERT INTO food_pictures (userEmail,restaurantName,image) VALUES(?,?,?)");
				ps.setString(1, currEmail);
				ps.setString(2, restaurantName);
				FileInputStream stream=new FileInputStream(imageFile);
				ps.setBinaryStream(3,stream,(int)imageFile.length());
				ps.executeUpdate();
				createCurrReviewPage(restaurantName);
			} 
        	catch (SQLException e) 
        	{
				System.out.println("Unable to delete"); 
			}
        	catch (FileNotFoundException e) 
        	{
				System.out.println("File not found");
			}
		});
	}
}