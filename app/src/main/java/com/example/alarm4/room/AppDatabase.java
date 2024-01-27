package com.example.alarm4.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DbAlarm.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}

