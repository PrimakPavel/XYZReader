package com.example.xyzreader2;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.xyzreader2.databinding.ActivityMainBinding;
import com.example.xyzreader2.presentation.viewModels.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements ShowSnackBarListener {
    private MainViewModel mMainViewModel;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (savedInstanceState == null) {
            mMainViewModel.prepareDbFromNetworkData(false);
        }
        mMainViewModel.getConnectionErrorData().observe(this, aBoolean -> {
            if (aBoolean) {
                showConnectionErrorSnackBar();
            }
        });
        mMainViewModel.getConnectionLoadingData().observe(this, isLoading -> {
            if (isLoading) {
                mBinding.mainProgressBar.setVisibility(View.VISIBLE);
            } else {
                mBinding.mainProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showSnack(int messageRes) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), messageRes, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showConnectionErrorSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.error_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.snack_bar_retry, view -> {
            snackbar.dismiss();
            mMainViewModel.prepareDbFromNetworkData(true);

        }).setActionTextColor(getResources().getColor(R.color.colorAccent)).show();
    }


}
