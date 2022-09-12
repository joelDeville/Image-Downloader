import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Downloader extends Application {
    // main runner method for GUI nodes and handling user interaction
    @Override
    public void start(Stage stage) throws Exception {
        // creating display box, text field for URLs, and ProgressBar for progress
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        TextField entry = new TextField();
        ProgressBar progress = new ProgressBar();
        progress.setPrefWidth(200);

        // text directions for the end user
        Text direction = new Text("Enter URL of an image then press 'Enter'");
        Text display = new Text("Display recently downloaded image");

        // button to display most recent download (if present)
        Button displayButton = new Button("Display");
        box.getChildren().addAll(direction, entry, display, displayButton, progress);

        // upon user hitting entry, URL is sent to a thread for downloading
        entry.setOnAction(enter -> {
            // getting url from user and setting progress back to zero
            String input = entry.getText();
            progress.setProgress(0);

            // if input is filled with spaces or nothing, inform user
            if (input.matches("[ ]*")) {
                Alert info = new Alert(AlertType.INFORMATION, "Enter a URL");
                info.showAndWait();
            }

            // creating new task for downloading
            DownloadTask task = new DownloadTask(input, progress);
            Thread thread = new Thread(task);

            // begins downloading the image and clears URL entry for next item
            thread.start();
            entry.clear();

            // button activates after hitting enter
            displayButton.setOnAction(click -> {
                try {
                    File localFile = new File("download" + task.getExtension());
                    FileInputStream downloadFile = new FileInputStream(localFile);
                    ImageView img = new ImageView(new Image(downloadFile));

                    VBox displayBox = new VBox(15);
                    displayBox.getChildren().add(img);
                    Scene imageScene = new Scene(displayBox);

                    // making new window to appear with downloaded image
                    Stage downloadStage = new Stage();
                    downloadStage.setScene(imageScene);
                    downloadStage.setTitle("Image Display");
                    downloadStage.show();
                } catch (FileNotFoundException e) {
                    // showing error to user if file does not exist
                    Alert error = new Alert(AlertType.ERROR, e.getMessage());
                    error.showAndWait();
                }
            });
        });

        // customizing main stage with dimensions
        box.setPrefWidth(600);
        box.setPrefHeight(200);

        // adding custom CSS to spruce up scene
        box.setStyle("-fx-background-color: #f7f7f7");
        displayButton.setStyle("-fx-background-color: #eeeeee");
        direction.setStyle("-fx-font: normal 14px 'serif'");
        display.setStyle("-fx-font: normal 14px 'serif'");

        // showing interface for application
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle("Image Downloader");
        stage.show();
    }

    // represents a download task for a URL
    private class DownloadTask implements Runnable {
        // instance variables
        private String url;
        private ProgressBar bar;
        private String extension;

        // constructs a new task
        public DownloadTask(String url, ProgressBar bar) {
            this.url = url;
            this.bar = bar;
        }

        // downloads a URL from the internet and stores it in local file with correct extension
        @Override
        public void run() {
            try {
                // open URL and access data stream
                InputStream dataStream = new URL(url).openStream();

                // finding extension of image
                final int EXTENSION_LENGTH = 4;
                final int LAST_PERIOD = url.lastIndexOf(".");
                extension = url.substring(LAST_PERIOD, LAST_PERIOD + EXTENSION_LENGTH);

                // determining rough size of file
                final int SIZE = dataStream.available();
                final int BUFFER = 2048;

                // copying all data from internet to local file
                FileOutputStream output = new FileOutputStream("download" + extension);
                long totalRead = 0;
                int read = 0;
                byte[] buf = new byte[BUFFER];
                while ((read = dataStream.read(buf)) != -1) {
                    output.write(buf, 0, read);
                    totalRead += read;
                    long finalVar = totalRead;

                    // updating progress bar to reflect total amount read
                    Platform.runLater(() -> {
                        double progress = (double) finalVar / SIZE;
                        bar.setProgress(progress);
                    });
                }
                // closing resources
                output.close();
                dataStream.close();
            } catch (IOException e) {
                // showing user an error if something goes wrong with download
                Platform.runLater(() -> {
                    Alert error = new Alert(AlertType.ERROR, e.getMessage());
                    error.showAndWait();
                });
            }
        }

        // returns extension for this task
        public String getExtension() {
            return extension;
        }
    }
}
