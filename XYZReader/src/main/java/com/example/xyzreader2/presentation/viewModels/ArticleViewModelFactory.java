package com.example.xyzreader2.presentation.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

public class ArticleViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Executor mExecutor;
    private long mId;

    public ArticleViewModelFactory(Executor executor, long id) {
        this.mId = id;
        this.mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            return (T) new ArticleViewModel(mExecutor,mId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");



    }
}
