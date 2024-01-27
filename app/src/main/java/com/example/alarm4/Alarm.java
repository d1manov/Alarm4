package com.example.alarm4;

import com.example.alarm4.room.DbAlarm;

public class Alarm {

    private int code;
    private int hour;
    private int minute;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static Alarm build(DbAlarm alarm) {
        Alarm a = new Alarm(alarm.hour, alarm.minute);
        a.setCode(alarm.code);
        return a;
    }
}
