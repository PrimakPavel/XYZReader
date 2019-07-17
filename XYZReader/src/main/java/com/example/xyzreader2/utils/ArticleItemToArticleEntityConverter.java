package com.example.xyzreader2.utils;

import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.data.pojo.ArticleItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ArticleItemToArticleEntityConverter {
    // Most time functions can only handle 1902 - 2037
    public static final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    public static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.ENGLISH);

    private static ArticleEntity convert(ArticleItem articleItem) {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(articleItem.getId());
        articleEntity.setTitle(articleItem.getTitle());
        articleEntity.setAuthor(articleItem.getAuthor());
        articleEntity.setBody(articleItem.getBody());
        articleEntity.setThumbUrl(articleItem.getThumbUrl());
        articleEntity.setPhotoUrl(articleItem.getPhotoUrl());
        articleEntity.setAspectRatio(articleItem.getAspectRatio());
        try {
            Date date = DATE_FORMAT.parse(articleItem.getPublishedDate());
            articleEntity.setPublishedDate(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return articleEntity;
    }

    public static List<ArticleEntity> convertList(List<ArticleItem> articleItems) {
        List<ArticleEntity> resultList = new ArrayList<>();
        for (ArticleItem articleItem : articleItems) {
            resultList.add(convert(articleItem));
        }
        return resultList;
    }
}
