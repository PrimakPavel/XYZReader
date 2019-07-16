package com.example.xyzreader2.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM articles")
    LiveData<List<ArticleEntity>> loadAllArticles();

    @Query("SELECT* FROM articles WHERE id = :articleId")
    LiveData<ArticleEntity> loadArticleById(long articleId);

    @Query("SELECT COUNT(id) FROM articles")
    Integer getRowCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(ArticleEntity article);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllArticles(List<ArticleEntity> articles);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateArticle(ArticleEntity article);

    @Delete
    void deleteArticle(ArticleEntity article);

    @Query("DELETE FROM articles")
    void clearTable();
}
