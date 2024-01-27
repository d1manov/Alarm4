package com.example.alarm4;

import android.app.Application;

import androidx.room.Room;

import com.example.alarm4.room.AppDatabase;

public class AlarmApp extends Application {

    public static AlarmApp instance;

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static AlarmApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
