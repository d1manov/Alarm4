package com.example.alarm4.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.alarm4.Alarm;

@Entity
public class DbAlarm {
    @PrimaryKey
    public int code;

    public int hour;

    public int minute;

    public DbAlarm(int code, int hour, int minute) {
        this.code = code;
        this.hour = hour;
        this.minute = minute;
    }

    public static DbAlarm build(Alarm alarm) {
        return new DbAlarm(
                alarm.getCode(),
                alarm.getHour(),
                alarm.getMinute()
        );
    }
}