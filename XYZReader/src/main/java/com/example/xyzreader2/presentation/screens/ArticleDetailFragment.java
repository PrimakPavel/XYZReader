package com.example.xyzreader2.presentation.screens;


import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader2.App;
import com.example.xyzreader2.R;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader2.presentation.adapter.ArticleTextBodyAdapter;
import com.example.xyzreader2.presentation.viewModels.ArticleViewModel;
import com.example.xyzreader2.presentation.viewModels.ArticleViewModelFactory;
import com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter.START_OF_EPOCH;


public class ArticleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public static final String SAVE_INSTANCE_BODY_LIST_STATE = "bodyListSate";
    private Parcelable listState;
    private ArticleViewModel mArticleViewModel;
    private FragmentArticleDetailBinding mBinding;
    private ArticleTextBodyAdapter mAdapterBody;

    private long mItemId;


    static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
        if (getActivity() != null) {
            ArticleViewModelFactory factory = new ArticleViewModelFactory(App.appExecutors.networkIO(), mItemId);
            mArticleViewModel = ViewModelProviders.of(this, factory).get(ArticleViewModel.class);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(SAVE_INSTANCE_BODY_LIST_STATE);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_detail, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareBodyRecycler();
        mArticleViewModel.getArticleById().observe(this, articleEntity -> {
            if (articleEntity != null) {
                bindViews(articleEntity);
                mBinding.getRoot().setVisibility(View.VISIBLE);
            } else {
                mBinding.getRoot().setVisibility(View.GONE);
            }
        });
        if (mArticleViewModel.getBodyElements() != null) {
            mArticleViewModel.getBodyElements().observe(this, bodyElements -> {
                mAdapterBody.updateList(bodyElements);
                if (listState != null && mBinding.bodyRecycler.getLayoutManager() != null) {
                    mBinding.bodyRecycler.getLayoutManager().onRestoreInstanceState(listState);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mArticleViewModel != null) {
            mArticleViewModel.getArticleById().removeObservers(this);
            if (mArticleViewModel.getBodyElements() != null) {
                mArticleViewModel.getBodyElements().removeObservers(this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mBinding.bodyRecycler.getLayoutManager() != null) {
            outState.putParcelable(SAVE_INSTANCE_BODY_LIST_STATE, mBinding.bodyRecycler.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    private void bindViews(ArticleEntity articleEntity) {
        if (articleEntity == null || mBinding == null) {
            return;
        }
        mBinding.collapsingToolbarLayout.setTitle(articleEntity.getTitle());
        Date publishedDate = new Date(articleEntity.getPublishedDate());
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            mBinding.articleByline.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + articleEntity.getAuthor()
                            + "</font>"));

        } else {
            // If date is before 1902, just show the string
            mBinding.articleByline.setText(Html.fromHtml(
                    ArticleItemToArticleEntityConverter.OUTPUT_FORMAT.format(publishedDate) + " by <font color='#ffffff'>"
                            + articleEntity.getAuthor()
                            + "</font>"));

        }
        Picasso.get()
                .load(articleEntity.getPhotoUrl())
                .into(mBinding.photo);

    }

    private void prepareBodyRecycler() {
        mAdapterBody = new ArticleTextBodyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBinding.bodyRecycler.setLayoutManager(layoutManager);
        mBinding.bodyRecycler.setHasFixedSize(true);
        mBinding.bodyRecycler.setAdapter(mAdapterBody);
    }
}
