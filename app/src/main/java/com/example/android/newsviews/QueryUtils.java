package com.example.android.newsviews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * Helper methods related to requesting and receiving news data from guardian api.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public static String title="";
    public static String category="";
    public static String timedate="";
    public static String author="";
    public static Bitmap thumbnail = null;
    public static String description;

    public QueryUtils() {
    }

    public static ArrayList<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        Log.v("QueryUtils","fetchNewsData");

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<News> news = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return news;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

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
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
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


    private static ArrayList<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.

        ArrayList<News> news = new ArrayList<>();

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        try {

            JSONObject jsonObject = new JSONObject(newsJSON);

            if (jsonObject.has("response"))
            {
                JSONObject response = jsonObject.getJSONObject("response");

                if(response.has("results"))
                {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length();i++)
                    {
                        JSONObject jsonNews = results.getJSONObject(i);
                        if(jsonNews.has("webTitle")) {
                            title = jsonNews.getString("webTitle");
                        }
                        else {
                           title="no title";
                        }
                        if(jsonNews.has("sectionName")){
                            category = jsonNews.getString("sectionName");
                        }
                        else{
                            category="no category";
                        }
                        if(jsonNews.has("webPublicationDate")){
                            String time=jsonNews.getString("webPublicationDate");
                            String[] bits = time.split("T");
                            timedate =bits[0]+", "+bits[1].substring(0,bits[1].length()-1);
                        }
                        else{
                            timedate="no details";
                        }
                        if(jsonNews.has("tags")){
                            JSONArray tags = jsonNews.getJSONArray("tags");
                            for(int j=0;j<tags.length();j++){
                                JSONObject tagslist = tags.getJSONObject(j);
                                if(tagslist.has("webTitle")){
                                    author="";
                                    author+=tagslist.getString("webTitle")+" ";
                                }
                                else {
                                    author="no authors";
                                }
                            }
                        }
                        else{
                            author="no authors";
                        }

                        if(jsonNews.has("fields")){
                            JSONObject fields = jsonNews.getJSONObject("fields");
                            if(fields.has("trailText")){
                                description=fields.getString("trailText");
                            }
                            else{
                                description="no description";
                            }
                            if(fields.has("thumbnail")) {
                                thumbnail = fetchThumbnail(fields.getString("thumbnail"));
                                news.add(new News(title,category,timedate,description,author,thumbnail,0));
                            }
                            else{
                                news.add(new News(title,category,timedate,description,author,thumbnail,R.drawable.ic_launcher_foreground));
                            }

                        }
                        else{
                            news.add(new News(title,category,timedate,"no description",author,thumbnail,R.drawable.ic_launcher_foreground));
                        }
                    }
                }
            }
            else{
                return news;
            }
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the News JSON results", e);
        }

        return news;

    }

    public static Bitmap fetchThumbnail(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        thumbnail= null;
        try {
            thumbnail = makeHttpRequestForBitmap(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        Log.v("thumbnail", String.valueOf(thumbnail));
        return thumbnail;
    }

    //Method that makes HTTP request and returns a Bitmap as the response
    private static Bitmap makeHttpRequestForBitmap(URL url) throws IOException {
        Bitmap bitmapResponse=null;

        //If the URL is null, then return early.
        if (url == null) {
            return null;
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
                bitmapResponse = BitmapFactory.decodeStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return bitmapResponse;
    }
}