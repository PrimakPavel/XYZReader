package com.example.xyzreader.data.db.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.xyzreader.data.db.AppDatabase;
import com.example.xyzreader.data.db.ArticleEntity;

import java.util.List;
import java.util.concurrent.Executor;

public class DbRepoImpl implements DbRepo {

    private final AppDatabase mDb;
    private final Executor diskIO;

    public DbRepoImpl(@NonNull AppDatabase appDatabase, @NonNull Executor discIOExecutor) {
        mDb = appDatabase;
        diskIO = discIOExecutor;
    }

    @Override
    public void insertArticle(final ArticleEntity article) {
        diskIO.execute(() -> mDb.articleDao().insertArticle(article));
    }

    @Override
    public void insertAllArticles(List<ArticleEntity> articleEntities) {
        diskIO.execute(() -> mDb.articleDao().insertAllArticles(articleEntities));
    }

    @Override
    public void updateArticle(ArticleEntity article) {
        diskIO.execute(() -> mDb.articleDao().updateArticle(article));
    }

    @Override
    public void deleteArticle(ArticleEntity article) {
        diskIO.execute(() -> mDb.articleDao().deleteArticle(article));
    }

    @Override
    public void clearTableSync() {
       mDb.articleDao().clearTable();
    }

    @Override
    public LiveData<List<ArticleEntity>> loadAllArticles() {
        return mDb.articleDao().loadAllArticles();
    }

    @Override
    public LiveData<ArticleEntity> loadArticleById(long articleId) {
        return mDb.articleDao().loadArticleById(articleId);
    }

    @Override
    public Integer loadArticleCount() {
        return mDb.articleDao().getRowCount();
    }
}
