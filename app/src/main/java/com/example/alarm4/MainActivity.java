package com.example.alarm4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Alarm> alarms;
    private LinearLayout alarmsLayout;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarms = new ArrayList<>();
        alarmsLayout = findViewById(R.id.alarmsLayout);
    }

    public void openTimePickerDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите время будильника");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        builder.setView(dialogView);

        final EditText hourEditText = dialogView.findViewById(R.id.hourEditText);
        final EditText minuteEditText = dialogView.findViewById(R.id.minuteEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = Integer.parseInt(hourEditText.getText().toString());
                int minute = Integer.parseInt(minuteEditText.getText().toString());

                Alarm alarm = new Alarm(hour, minute);

                alarms.add(alarm);
                addAlarmView(alarm);
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void addAlarmView(final Alarm alarm) {
        LinearLayout alarmLayout = new LinearLayout(this);
        alarmLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        alarmLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView alarmTextView = new TextView(this);
        alarmTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        alarmTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button deleteButton = new Button(this);
        deleteButton.setText("Удалить");
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.remove(alarm);
                alarmsLayout.removeView((View) v.getParent());
            }
        });

        alarmLayout.addView(alarmTextView);
        alarmLayout.addView(deleteButton);

        alarmsLayout.addView(alarmLayout);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        startAlarm(alarm);
    }

    private void startAlarm(Alarm alarm) {
        // Установка задачи будильника для срабатывания в указанное время
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    private void showAlarmWindow() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.dialog_alarm, null);

        TextView alarmTimeTextView = dialogView.findViewById(R.id.alarmTimeTextView);
        alarmTimeTextView.setText("Время сработало!");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class AlarmService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Отображение окна с временем
            showAlarmWindow();

            // Воспроизведение звука
            playDefaultNotificationSound();

            return START_NOT_STICKY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void playDefaultNotificationSound() {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            ((Ringtone) ringtone).play();
        }
    }
}