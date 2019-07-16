package com.example.xyzreader.presentation.screens;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.xyzreader.R;
import com.example.xyzreader.data.db.ArticleEntity;
import com.example.xyzreader.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader.presentation.viewModels.MainViewModel;
import com.example.xyzreader.utils.ArticleItemToArticleEntityConverter;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static com.example.xyzreader.utils.ArticleItemToArticleEntityConverter.START_OF_EPOCH;


public class ArticleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";

    private MainViewModel mMainViewModel;
    private FragmentArticleDetailBinding mBinding;

    private long mItemId;


    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_detail, container, false);
        mBinding.appBar.setNavigationOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel.getArticleById(mItemId).observe(this, articleEntity -> {
            if (articleEntity != null) {
                bindViews(articleEntity);
                mBinding.getRoot().setVisibility(View.VISIBLE);
            } else {
                mBinding.getRoot().setVisibility(View.GONE);
            }
        });
    }

    private void bindViews(ArticleEntity articleEntity) {
        if (articleEntity == null || mBinding == null) {
            return;
        }
        mBinding.webView.setVerticalScrollBarEnabled(false);
        mBinding.webView.setHorizontalScrollBarEnabled(false);
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });

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
        mBinding.webView.loadData(articleEntity.getBody().replaceAll("(\r\n|\n)", "<br />"), "text/html; charset=utf-8", "utf-8");

        Picasso.get()
                .load(articleEntity.getPhotoUrl())
                .into(mBinding.photo);
    }

}
