package com.example.xyzreader.data.db.repo;

import androidx.lifecycle.LiveData;

import com.example.xyzreader.data.db.ArticleEntity;

import java.util.List;

public interface DbRepo {

    void insertArticle(ArticleEntity article);

    void insertAllArticles(List<ArticleEntity> articles);

    void updateArticle(ArticleEntity article);

    void deleteArticle(ArticleEntity article);

    void clearTableSync();

    LiveData<List<ArticleEntity>> loadAllArticles();

    LiveData<ArticleEntity> loadArticleById(long articleId);

    Integer loadArticleCount();
}
