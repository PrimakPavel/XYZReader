package com.example.xyzreader2.remote;

import com.example.xyzreader2.data.pojo.ArticleItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteEndpointUtil {

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
