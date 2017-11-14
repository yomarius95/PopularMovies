package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    static ArrayList<Movie> fetchMoviesData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Return the {@link ArrayList<Movies>}
        return extractMoviesFromJson(jsonResponse);
    }

    static ArrayList<Review> fetchReviewsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return extractReviewsFromJson(jsonResponse);
    }

    static ArrayList<Trailer> fetchTrailerData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return extractTrailersFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link ArrayList <Movie>} object by parsing out information
     * about the movies from the input movieJSON string.
     */
    private static ArrayList<Movie> extractMoviesFromJson(String movieJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(movieJSON);
            if (jsonObject.optJSONArray("results") != null) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.getJSONObject(i);
                    String id = movie.getString("id");
                    String title = movie.getString("title");
                    String posterUrl = movie.getString("poster_path");
                    String synopsis = movie.getString("overview");
                    String rating = movie.getString("vote_average");
                    String releaseDate = movie.getString("release_date");

                    movies.add(new Movie(id, title, posterUrl, synopsis, rating, releaseDate));
                }
            }
            return movies;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }
        return null;
    }

    private static ArrayList<Review> extractReviewsFromJson(String reviewJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }

        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(reviewJSON);
            if (jsonObject.optJSONArray("results") != null) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject review = results.getJSONObject(i);
                    String author = review.getString("author");
                    String content = review.getString("content");
                    String url = review.getString("url");

                    reviews.add(new Review(author, content, url));
                }
            }
            return reviews;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }
        return null;
    }

    private static ArrayList<Trailer> extractTrailersFromJson(String trailerJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(trailerJSON)) {
            return null;
        }

        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(trailerJSON);
            if (jsonObject.optJSONArray("results") != null) {
                JSONArray results = jsonObject.getJSONArray("results");
                int maxTrailers;
                if(results.length() > 3) {
                    maxTrailers = 3;
                } else {
                    maxTrailers = results.length();
                }
                for (int i = 0; i < maxTrailers; i++) {
                    JSONObject trailer = results.getJSONObject(i);
                    String name = trailer.getString("name");
                    String key = trailer.getString("key");

                    trailers.add(new Trailer(name, key));
                }
            }
            return trailers;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }
        return null;
    }
}
