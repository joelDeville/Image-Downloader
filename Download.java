import javafx.application.Application;

import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;

import javafx.concurrent.Task;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class Download extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		//creating VBox and children to add
		VBox box = new VBox(15);
		box.setPadding(new Insets(10));
		TextField entry = new TextField();
		
		Text direction = new Text("Enter URL of an image then press 'Enter'");
		Text display = new Text("Display recently downloaded image");
		
		Button displayButton = new Button("Display");
		
		box.getChildren().addAll(direction, entry, display, displayButton);
		
		//enter is hit, and url is sent to downloader, thread is started, progress is updated
		entry.setOnAction((ActionEvent e) ->
		{
			String input = entry.getText();
			
			Task<Void> task = new Downloader(input);
			Thread thread = new Thread(task);
			
			ProgressBar progress = new ProgressBar();
			progress.progressProperty().bind(task.progressProperty());
			
			//ensuring there aren't multiple copies of the progress bar
			if(box.getChildren().size() > 4)
			{
				box.getChildren().remove(4);
				box.getChildren().add(progress);
			}
			
			else
				box.getChildren().add(progress);
			
			progress.setPrefWidth(200);
			
			//button activates once a URL has been submitted, shows most recent image in a new window
			displayButton.setOnAction((ActionEvent a) ->
			{
				try
				{
					FileInputStream data = new FileInputStream(new File("DownloadedFile" + Downloader.getExt()));
					ImageView img = new ImageView(new Image(data));
					
					VBox b = new VBox(15);
					b.getChildren().add(img);
					Scene sc = new Scene(b);
					
					Stage st = new Stage();
					st.setScene(sc);
					st.setTitle("Image Display");
					st.show();
				}
				
				catch (FileNotFoundException error)
				{
					error.printStackTrace();
				}
			});
			
			thread.start();
			entry.clear();
		});
		
		//customizing main stage
		box.setPrefWidth(600);
		box.setPrefHeight(200);
		
		box.setStyle("-fx-background-color: #f7f7f7");
		displayButton.setStyle("-fx-background-color: #eeeeee");
		direction.setStyle("-fx-font: normal 14px 'serif'");
		display.setStyle("-fx-font: normal 14px 'serif'");
		
		Scene scene = new Scene(box);
		stage.setScene(scene);
		stage.setTitle("Image Downloader");
		stage.show();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}