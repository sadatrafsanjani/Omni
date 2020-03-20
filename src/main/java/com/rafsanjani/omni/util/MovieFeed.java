package com.rafsanjani.omni.util;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieFeed {

    private static OkHttpClient client = new OkHttpClient();

    public static String fetchMovieFeed(String movieName) {

        String BASE_URL = "http://www.omdbapi.com/?t=" + movieName + "&plot=full" + "&" + "apiKey=911687d9";

        Request request = new Request.Builder().url(BASE_URL).build();
        String feed = "";

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

    public static Image fetchMoviePoster(String url) {

        Request request = new Request.Builder().url(url).build();

        Image image = null;

        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                InputStream inputStream = response.body().byteStream();
                image = new Image(inputStream);
            }

        } catch (IOException e) {
        }

        return image;
    }

}
