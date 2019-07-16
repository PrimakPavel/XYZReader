package com.example.xyzreader.remote;

import com.example.xyzreader.data.pojo.ArticleItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class RemoteEndpointUtil {


    public static JSONArray fetchJsonArray() {
        String itemsJson = null;
        try {
            itemsJson = fetchPlainText(Config.BASE_URL);
        } catch (IOException e) {
            Timber.e("Error fetching items JSON%s", e.getMessage());
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            Object val = tokener.nextValue();
            if (!(val instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            return (JSONArray) val;
        } catch (JSONException e) {
            Timber.e("Error parsing items JSON%s", e.getMessage());
        }

        return null;
    }

    public static String fetchPlainText(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static List<ArticleItem> fetchArticles(URL url) throws IOException {
        List<ArticleItem> resultArticles = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if (response.body() != null) {
            try {
                String bodyStr = response.body().string();
                ArticleItem[] recipeItems = new Gson().fromJson(bodyStr, ArticleItem[].class);
                if (recipeItems != null) {
                    resultArticles = Arrays.asList(recipeItems);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return resultArticles;
    }
}
