package com.example.xyzreader2.presentation.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xyzreader2.App;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.data.pojo.ArticleItem;
import com.example.xyzreader2.remote.Config;
import com.example.xyzreader2.remote.RemoteEndpointUtil;
import com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class MainViewModel extends ViewModel {
    private Executor ioExecutor = App.appExecutors.networkIO();
    private MutableLiveData<Boolean> connectionErrorLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getConnectionErrorData() {
        return connectionErrorLiveData;
    }

    public void initDbFromNetworkData() {
        connectionErrorLiveData.setValue(false);
        ioExecutor.execute(() -> {
            if (App.dbRepo.loadArticleCount() == 0) {
                try {
                    saveAllToDb(loadDataFromNetwork());
                } catch (IOException e) {
                    connectionErrorLiveData.postValue(true);
                }
            }
        });
    }

    public void updateDbFromNetworkData() {
        connectionErrorLiveData.setValue(false);
        ioExecutor.execute(() -> {
            App.dbRepo.clearTableSync();
            try {
                saveAllToDb(loadDataFromNetwork());
            } catch (IOException e) {
                connectionErrorLiveData.postValue(true);
            }
        });
    }

    private void saveAllToDb(List<ArticleItem> recipes) {
        if (recipes != null && recipes.size() > 0) {
            App.dbRepo.insertAllArticles(ArticleItemToArticleEntityConverter.convertList(recipes));
        }
    }

    private List<ArticleItem> loadDataFromNetwork() throws IOException {
        return RemoteEndpointUtil.fetchArticles(Config.BASE_URL);
    }

    public LiveData<List<ArticleEntity>> getAllArticles() {
        return App.dbRepo.loadAllArticles();
    }

    public LiveData<ArticleEntity> getArticleById(long id) {
        return App.dbRepo.loadArticleById(id);
    }
}
