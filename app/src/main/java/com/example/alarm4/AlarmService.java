package com.example.alarm4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Код для выполнения задачи будильника
        // Например, запустите активность или отправьте уведомление
        return START_NOT_STICKY;
    }
}