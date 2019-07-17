package com.example.xyzreader2.presentation.screens;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.xyzreader2.R;
import com.example.xyzreader2.databinding.FragmentArticleListBinding;
import com.example.xyzreader2.presentation.adapter.ArticleListAdapter;
import com.example.xyzreader2.presentation.adapter.ArticleListClickListener;
import com.example.xyzreader2.presentation.viewModels.MainViewModel;

import java.util.ArrayList;

import static com.example.xyzreader2.presentation.screens.ArticleDetailFragment.ARG_ITEM_ID;


public class ArticleListFragment extends Fragment implements ArticleListClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FragmentArticleListBinding mBinding;
    private MainViewModel mMainViewModel;
    private ArticleListAdapter mAdapter;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            setHasOptionsMenu(true);
            mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false);
        prepareActionBar();
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            mMainViewModel.prepareDbFromNetworkData(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        prepareRecyclerArticles();
        mMainViewModel.getAllArticles().observe(this, articleEntities -> {
            if (articleEntities != null && articleEntities.size() > 0) {
                mAdapter.updateList(articleEntities);
                mBinding.swipeRefreshLayout.setRefreshing(false);
                mBinding.errorMessage.setVisibility(View.GONE);
            }
        });
        mMainViewModel.getConnectionErrorData().observe(this, aBoolean -> {
            if (aBoolean) {
                mBinding.swipeRefreshLayout.setRefreshing(false);
                mAdapter.updateList(new ArrayList<>());
                mBinding.errorMessage.setText(R.string.error_empty_articles);
                mBinding.errorMessage.setVisibility(View.VISIBLE);
            }
        });
        prepareSwipeRefreshLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMainViewModel != null) {
            mMainViewModel.getConnectionErrorData().removeObservers(this);
            mMainViewModel.getAllArticles().removeObservers(this);
        }
    }

    private void prepareRecyclerArticles() {
        mAdapter = new ArticleListAdapter(this);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(sglm);
        mAdapter.setHasStableIds(true);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onArticleItemClick(long articleId, ImageView imageView) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, articleId);
        mNavController.navigate(R.id.actionArticlesDetailPagerFragment, args);
    }

    @Override
    public void onRefresh() {
        mMainViewModel.prepareDbFromNetworkData(true);
    }

    private void prepareSwipeRefreshLayout() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void prepareActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(mBinding.toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }
}
