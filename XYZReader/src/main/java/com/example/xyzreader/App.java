package com.example.xyzreader;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.xyzreader.data.db.AppDatabase;
import com.example.xyzreader.data.db.repo.DbRepo;
import com.example.xyzreader.data.db.repo.DbRepoImpl;
import com.example.xyzreader.utils.AppExecutors;

import timber.log.Timber;

public class App extends Application {
    public static final String CHANNEL_ID = "XYZReaderChannel";
    public static final String CHANNEL_NAME = "XYZReader App Channel";
    public static AppExecutors appExecutors;
    public static DbRepo dbRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        createNotificationChannel();

        appExecutors = new AppExecutors();
        dbRepo = new DbRepoImpl(AppDatabase.getInstance(getApplicationContext()), appExecutors.diskIO());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
