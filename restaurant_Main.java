package restaurant_history;
import javafx.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage; 

public class restaurant_Main extends Application
{
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	BorderPane root=new BorderPane(); //will hold everything of our program
	VBox startMenu=new VBox();
	VBox addVisit=new VBox(); 
	public void start(Stage primaryStage) throws Exception 
	{
		
		createStartMenu(startMenu); 
		
		createAddVisitMenu(addVisit);
		
		root.setCenter(startMenu);
		root.setPadding(new Insets(10)); //set borders within root sectionsn
		startMenu.setAlignment(Pos.CENTER);
		Scene myScene=new Scene(root,400,400); 
		primaryStage.setScene(myScene);
		primaryStage.setTitle("Derek's Restaurant History");
		primaryStage.show();
		
		
		
	}
	public void createStartMenu(VBox start)
	{
		Label welcome=new Label("Welcome to Derek's Restaurant History"); 
		Label selectL=new Label("Please select a button to perform the following actions"); 
		VBox startSelections=new VBox(); 
		startSelections.setAlignment(Pos.CENTER);
		Button viewB=new Button("View History"); 
		Button addB=new Button("Add new visit"); 
		Button addPic=new Button("Add food pic"); 
		
		startSelections.getChildren().addAll(viewB,addB,addPic); 
		start.getChildren().addAll(welcome,selectL,startSelections); 
		addB.setOnAction(event->{
			root.setCenter(addVisit); 
		});
	}
	public void createAddVisitMenu(VBox visit)
	{
		GridPane menu=new GridPane(); 
		//Box restName=new HBox(); 
		Label restL=new Label("Restaurant Name: ");
		TextField restField=new TextField("Please enter the restaurant name here"); 
		//restName.getChildren().addAll(restL,restField);
		//HBox ratingBox=new HBox();
		Label rateL=new Label("Please enter a rating: ");
		TextField rateField=new TextField("1 being bad, 5 being good"); 
		//ratingBox.getChildren().addAll(rateL,rateField); 
		//HBox commentBox=new HBox(); 
		Label commentL=new Label("Please enter comments: "); 
		TextArea commentField=new TextArea();
		//commentBox.getChildren().addAll(commentL,commentField); 
		menu.add(restL, 0, 0);menu.add(restField, 1, 0);
		menu.add(rateL, 0, 1);menu.add(rateField, 1, 1);
		menu.add(commentL, 0, 2);menu.add(commentField, 1, 2);
		menu.setPadding(new Insets(10));
		visit.getChildren().add(menu);
		menu.setAlignment(Pos.CENTER);
		
	}
}
