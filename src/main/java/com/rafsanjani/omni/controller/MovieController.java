package com.rafsanjani.omni.controller;

import com.rafsanjani.omni.util.MovieFeed;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class MovieController implements Initializable {

    @FXML
    private TextField movieNameField;
    @FXML
    private Button searchButton, refreshButton;
    @FXML
    private ImageView posterView;

    private Map<String, String> map = new LinkedHashMap<>();
    private Image image;

    @FXML
    private Label label1, label2, label3, label4, label5,
            label6, label7, label8, label9, label10,
            label11, label12, label13, label14, label15,
            label16, label17, label18;

    private String movieName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        enterKeySearchListener();
    }

    @FXML
    public void searchAction(ActionEvent event) {

        initiateTitleSearch();
    }

    private void initiateTitleSearch() {

        getInput();
        switchOffControls();

        if ((!movieName.isEmpty()) && (movieName.length() > 0)) {

            searchMovie();
        }
    }

    private void getInput() {

        movieName = movieNameField.getText().trim();
        movieName = movieName.replace(" ", "+");
    }

    private void switchOffControls() {

        movieNameField.setDisable(true);
        searchButton.setDisable(true);
    }

    private void switchOnControls() {

        movieNameField.setDisable(false);
        searchButton.setDisable(false);
    }

    private void searchMovie() {

        Task task = new Task() {

            @Override
            protected void done() {

                switchOnControls();
            }

            @Override
            protected void succeeded() {

                setInfoLabels();
            }

            @Override
            protected void failed() {

                System.out.println("FAILED!");
            }

            @Override
            protected Void call() {

                JSONObject object = new JSONObject(MovieFeed.fetchMovieFeed(movieName));
                image = MovieFeed.fetchMoviePoster(object.get("Poster").toString());

                for (String key : object.keySet()) {

                    map.put(key, object.get(key).toString());

                    System.out.println(key + ":" + object.get(key));
                }

                getScores(map.get("Ratings"));

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void refreshView(ActionEvent event) {

        refreshButton.setDisable(true);

        label1.setText("");
        label2.setText("");
        label3.setText("");
        label4.setText("");
        label5.setText("");
        label6.setText("");
        label7.setText("");
        label8.setText("");
        label9.setText("");
        label10.setText("");
        label11.setText("");
        label12.setText("");
        label13.setText("");
        label14.setText("");
        label15.setText("");
        label16.setText("");
        label17.setText("");
        label18.setText("");
        posterView.setImage(null);

        refreshButton.setDisable(false);
    }

    private void enterKeySearchListener() {
        movieNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().equals(KeyCode.ENTER)) {

                    initiateTitleSearch();
                }
            }
        });
    }

    private void setInfoLabels() {

        label1.setText("Title: " + map.get("Title") + " (" + map.get("Year") + ")");
        label2.setText("Runtime: " + map.get("Runtime"));
        label3.setText("Actors: " + map.get("Actors"));
        label4.setText("Language: " + map.get("Language"));
        label5.setText("Director: " + map.get("Director"));
        label6.setText("Rotten Tomatoes: " + map.get("Rotten Tomatoes"));
        label7.setText("IMDB: " + map.get("Internet Movie Database"));
        label8.setText("Metacritic: " + map.get("Metacritic"));
        label9.setText("Aggregated Score: " + map.get("Aggregated Score").substring(0, 4) + "/100");
        label10.setText("Rated: " + map.get("Rated"));
        label11.setText("Released: " + map.get("Released"));
        label12.setText("Production: " + map.get("Production"));
        label13.setText("Country: " + map.get("Country"));
        label14.setText("Genre: " + map.get("Genre"));
        label15.setText("Writer: " + map.get("Writer"));
        label16.setText("Plot: " + map.get("Plot"));
        label17.setText("Box Office: " + map.get("BoxOffice"));
        label18.setText("Country: " + map.get("Country"));
        posterView.setImage(image);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), posterView);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        movieNameField.setText("");
    }

    private void getScores(String scores) {

        JSONArray array = new JSONArray(scores);

        for (int i = 0; i < array.length(); i++) {

            JSONObject object = array.getJSONObject(i);
            map.put(object.getString("Source"), object.getString("Value"));
        }

        map.put("Aggregated Score", String.valueOf(calculateScores()));

    }

    private double calculateScores() {

        double score, score1 = 0.0, score2 = 0.0, score3 = 0.0, divider = 0.0;

        String imdb = map.get("Internet Movie Database").substring(0, map.get("Internet Movie Database").indexOf("/"));
        String rottenTomatoes = map.get("Rotten Tomatoes").substring(0, map.get("Rotten Tomatoes").indexOf("%"));
        String metacritic = map.get("Metacritic").substring(0, map.get("Metacritic").indexOf("/"));

        if (!imdb.isEmpty() && imdb.length() > 0) {

            score1 = Float.valueOf(imdb) * 10;
            divider++;
        }

        if (!rottenTomatoes.isEmpty() && rottenTomatoes.length() > 0) {

            score2 = Float.valueOf(rottenTomatoes);
            divider++;
        }

        if (!metacritic.isEmpty() && metacritic.length() > 0) {

            score3 = Float.valueOf(metacritic);
            divider++;
        }

        score = (score1 + score2 + score3) / divider;

        return score;
    }
}
