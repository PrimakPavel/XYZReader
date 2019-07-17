package com.example.xyzreader2.presentation.viewModels;

import android.text.Html;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.xyzreader2.App;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.utils.MyStringUtils;

import java.util.List;
import java.util.concurrent.Executor;

public class ArticleViewModel extends ViewModel {

    private Executor ioExecutor;
    private LiveData<ArticleEntity> articleLiveData;
    private LiveData<List<String>> bodyElementsLiveData;
    private static final int BODY_DIVIDER = 2000;
    private long currentId;

    ArticleViewModel(Executor executor, long id) {
        this.ioExecutor = executor;
        this.currentId = id;
    }

    public LiveData<ArticleEntity> getArticleById() {
        if (articleLiveData == null) {
            articleLiveData = App.dbRepo.loadArticleById(currentId);
            bodyElementsLiveData = Transformations.switchMap(articleLiveData, input -> {
                MutableLiveData<List<String>> bodyTempLiveData = new MutableLiveData<>();
                divideBodyString(input, bodyTempLiveData);
                return bodyTempLiveData;
            });
        }
        return articleLiveData;
    }

    @Nullable
    public LiveData<List<String>> getBodyElements() {
        return bodyElementsLiveData;
    }


    private void divideBodyString(ArticleEntity articleEntity, MutableLiveData<List<String>> bodyElementLiveData) {
        ioExecutor.execute(() -> {
            String bodyHtml = Html.fromHtml(articleEntity.getBody().replaceAll("(\r\n|\n)", "<br />")).toString();
            List<String> bodyElements = MyStringUtils.divideString(bodyHtml, BODY_DIVIDER);
            bodyElementLiveData.postValue(bodyElements);
        });
    }
}
