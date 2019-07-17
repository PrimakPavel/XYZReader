package com.example.xyzreader2.presentation.screens;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.xyzreader2.R;
import com.example.xyzreader2.data.db.ArticleEntity;
import com.example.xyzreader2.databinding.FragmentArticlesDetailPagerBinding;
import com.example.xyzreader2.presentation.viewModels.MainViewModel;

import java.util.List;


public class ArticlesDetailPagerFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";
    private static final String SAVE_INSTANCE_SELECTED_ITEM_ID = "saveInstanceSelectedItemId";
    private long mStartedItemId;

    private long mSelectedItemId;

    private List<ArticleEntity> mArticles;

    private MyPagerAdapter mPagerAdapter;

    private FragmentArticlesDetailPagerBinding mBinding;
    private MainViewModel mMainViewModel;


    public static ArticlesDetailPagerFragment newInstance(long itemId) {
        ArticlesDetailPagerFragment fragment = new ArticlesDetailPagerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            mStartedItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mSelectedItemId = mStartedItemId;
        } else {
            mSelectedItemId = savedInstanceState.getLong(SAVE_INSTANCE_SELECTED_ITEM_ID, mStartedItemId);
        }
        // Inflate the layout for this fragment
        if (getActivity() != null) {
            mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_articles_detail_pager, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());

        mBinding.pager.setAdapter(mPagerAdapter);

        mBinding.pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mArticles != null && position < mArticles.size()) {
                    mSelectedItemId = mArticles.get(position).getId();
                }
            }
        });

        mBinding.shareFab.setOnClickListener(view1 -> {
            if (getActivity() != null)
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
        });


        mMainViewModel.getAllArticles().observe(this, articleEntities -> {
            if (articleEntities != null) {
                mArticles = articleEntities;
                mPagerAdapter.notifyDataSetChanged();
                int position = 0;
                for (int i = 0; i < mArticles.size(); i++) {
                    ArticleEntity currentArticle = mArticles.get(i);
                    if (currentArticle.getId() == mSelectedItemId) {
                        position = i;
                        break;
                    }
                }
                mBinding.pager.setCurrentItem(position, false);
            }
        });

        mBinding.appBar.setNavigationOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SAVE_INSTANCE_SELECTED_ITEM_ID, mSelectedItemId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainViewModel.getAllArticles().removeObservers(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding.pager.clearOnPageChangeListeners();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            if (mArticles.size() > position) {
                ArticleEntity currentArticleEntity = mArticles.get(position);
                return ArticleDetailFragment.newInstance(currentArticleEntity.getId());
            }
            return null;
        }

        @Override
        public int getCount() {
            return (mArticles != null) ? mArticles.size() : 0;
        }
    }

}
