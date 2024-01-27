package com.example.alarm4.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM DbAlarm")
    List<DbAlarm> getAll();

    @Insert
    void insert(DbAlarm alarm);

    @Delete
    void delete(DbAlarm alarm);
}
