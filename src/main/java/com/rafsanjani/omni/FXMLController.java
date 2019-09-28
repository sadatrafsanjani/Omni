package com.rafsanjani.omni;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class FXMLController implements Initializable {

    @FXML
    private TextField movieNameField;
    @FXML
    private Button searchButton;
    @FXML
    private ImageView posterView;

    private String movieName;
    private final String API_KEY = "apiKey=911687d9";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void fetchPoster(String url) {

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            InputStream inputStream = response.body().byteStream();
            Image image = new Image(inputStream);
            posterView.setImage(image);
        } catch (Exception e) {
        }

    }

    public String fetchFeed() {

        String BASE_URL = "http://www.omdbapi.com/?t=" + movieName + "&" + API_KEY;

        Request request = new Request.Builder().url(BASE_URL).build();
        String feed = null;

        try {

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            feed = response.body().string();

        } catch (IOException e) {

            System.out.println(e.getMessage());
        }

        return feed;
    }

    @FXML
    public void searchAction(ActionEvent event) {

        sanitizeInput();

        if ((!movieName.isEmpty()) && (movieName.length() > 0)) {

            searchMovie();
        }
        
        movieNameField.setText("");
    }

    private void sanitizeInput() {

        movieName = movieNameField.getText().trim();
        movieNameField.setDisable(true);
        searchButton.setDisable(true);
        movieName = movieName.replace(" ", "+");
    }

    private void searchMovie() {

        Task task = new Task() {

            @Override
            protected Void call() {

                final String json = fetchFeed();

                JSONObject object = new JSONObject(json);

                String posterUrl = (String) object.get("Poster");

                fetchPoster(posterUrl);

                for (String key : object.keySet()) {

                    System.out.println(key + ":" + object.get(key));
                }

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        movieNameField.setDisable(false);
                        searchButton.setDisable(false);
                    }
                });

                return null;
            }

        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
