package com.example.xyzreader2.presentation.screens;


import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.transition.TransitionInflater;
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
import com.example.xyzreader2.presentation.viewModels.MainViewModel;
import com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter;
import com.example.xyzreader2.utils.MyStringUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import static com.example.xyzreader2.utils.ArticleItemToArticleEntityConverter.START_OF_EPOCH;


public class ArticleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public static final int BODY_DIVIDER = 2000;

    private MainViewModel mMainViewModel;
    private FragmentArticleDetailBinding mBinding;
    private ArticleTextBodyAdapter mAdapterBody;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_detail, container, false);
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
        prepareBodyRecycler();
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

        App.appExecutors.networkIO().execute(() -> {
            String bodyHtml = Html.fromHtml(articleEntity.getBody().replaceAll("(\r\n|\n)", "<br />")).toString();
            List<String> bodyElements = MyStringUtils.divideString(bodyHtml, BODY_DIVIDER);
            mBinding.bodyRecycler.post(() -> mAdapterBody.updateList(bodyElements));
        });


        Picasso.get()
                .load(articleEntity.getPhotoUrl())
                .into(mBinding.photo, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError(Exception e) {
                        startPostponedEnterTransition();
                    }

                });
    }

    private void prepareBodyRecycler() {
        mAdapterBody = new ArticleTextBodyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBinding.bodyRecycler.setLayoutManager(layoutManager);
        mBinding.bodyRecycler.setHasFixedSize(true);
        mBinding.bodyRecycler.setAdapter(mAdapterBody);
    }

}
