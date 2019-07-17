package com.example.xyzreader2.presentation.viewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.xyzreader2.App;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.data.pojo.ArticleItem;
import com.example.xyzreader2.data.remote.Config;
import com.example.xyzreader2.data.remote.RemoteEndpointUtil;
import com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter;
import com.example.xyzreader2.utils.NetConnectionUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class MainViewModel extends AndroidViewModel {
    private final Executor ioExecutor = App.appExecutors.networkIO();
    private final MutableLiveData<Boolean> connectionErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connectionLoadingLiveData = new MutableLiveData<>();
    private LiveData<List<ArticleEntity>> articlesLiveData;
    private final Context mContext;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
    }

    public LiveData<Boolean> getConnectionErrorData() {
        return connectionErrorLiveData;
    }

    public LiveData<Boolean> getConnectionLoadingData() {
        return connectionLoadingLiveData;
    }

    public void prepareDbFromNetworkData(boolean isUpdate) {
        connectionErrorLiveData.setValue(false);
        connectionLoadingLiveData.setValue(true);
        ioExecutor.execute(() -> {
            if (isUpdate) {
                App.dbRepo.clearTableSync();
            }
            if (App.dbRepo.loadArticleCount() == 0) {
                //Check Internet connection
                if (!NetConnectionUtil.isNetConnection(mContext)) {
                    connectionErrorLiveData.postValue(true);
                    connectionLoadingLiveData.postValue(false);
                } else {
                    //Save all data to DB
                    try {
                        saveAllToDb(loadDataFromNetwork());
                    } catch (IOException e) {
                        connectionErrorLiveData.postValue(true);
                    } finally {
                        connectionLoadingLiveData.postValue(false);
                    }
                }
            }
            connectionLoadingLiveData.postValue(false);
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
        if (articlesLiveData == null) {
            articlesLiveData = App.dbRepo.loadAllArticles();
        }
        return articlesLiveData;
    }


    public LiveData<ArticleEntity> getArticleById(long id) {
        return App.dbRepo.loadArticleById(id);
    }

}
