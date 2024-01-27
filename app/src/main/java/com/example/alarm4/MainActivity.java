package com.example.alarm4;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.alarm4.room.AlarmDao;
import com.example.alarm4.room.DbAlarm;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private LinearLayout alarmsLayout;
    private AlarmManager alarmManager;
    private AlarmDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        dao = AlarmApp.getInstance().getDatabase().alarmDao();
        alarmsLayout = findViewById(R.id.alarmsLayout);

        dao.getAll().stream().map(Alarm::build).forEach(this::addAlarmView);
    }

    public void openTimePickerDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите время будильника");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        builder.setView(dialogView);

        final EditText hourEditText = dialogView.findViewById(R.id.hourEditText);
        final EditText minuteEditText = dialogView.findViewById(R.id.minuteEditText);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int hour = Integer.parseInt(hourEditText.getText().toString());
            int minute = Integer.parseInt(minuteEditText.getText().toString());

            Alarm alarmZ = startAlarm(new Alarm(hour, minute));

            dao.insert(DbAlarm.build(alarmZ));
            addAlarmView(alarmZ);
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint({"ScheduleExactAlarm", "DefaultLocale"})
    private void addAlarmView(final Alarm alarm) {

        RelativeLayout alarmLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 1;
        alarmLayout.setLayoutParams(params);

        // параметры ширины и высоты MATCH_PARENT и WRAP_CONTENT
        TextView alarmTextView = new TextView(this);
        alarmTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        alarmTextView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));

        // параметры размещения, чтобы текстовое представление выравнивалось по центру вертикально
        RelativeLayout.LayoutParams textViewParams = (RelativeLayout.LayoutParams) alarmTextView.getLayoutParams();
        textViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        alarmTextView.setLayoutParams(textViewParams);

        Typeface face = ResourcesCompat.getFont(this, R.font.blitzel);
        alarmTextView.setTypeface(face);
        alarmTextView.setTextSize(70);
        alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.white));

        Button deleteButton = new Button(new ContextThemeWrapper(this, R.style.Button_Transparent));
        deleteButton.setText("Удалить");
        deleteButton.setTextSize(15);
        deleteButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        // параметры размещения, чтобы кнопка удаления выравнивалась по правому краю и по центру вертикально
        RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        deleteButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        deleteButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        deleteButton.setLayoutParams(deleteButtonParams);

        deleteButton.setTextColor(ContextCompat.getColor(this, R.color.white));

        deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.back));
        deleteButton.setOnClickListener(v -> {
            alarmManager.cancel(makePi(alarm.getCode()));
            dao.delete(DbAlarm.build(alarm));
            alarmsLayout.removeView((View) v.getParent());
        });

        alarmLayout.addView(alarmTextView);
        alarmLayout.addView(deleteButton);

        alarmsLayout.addView(alarmLayout);
    }

    private PendingIntent makePi(int code) {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(getApplicationContext(), code, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private Alarm startAlarm(Alarm alarm) {
        // Установка задачи будильника для срабатывания в указанное время

        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.setTimeInMillis(System.currentTimeMillis());
        calendarAlarm.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendarAlarm.set(Calendar.MINUTE, alarm.getMinute());
        calendarAlarm.set(Calendar.SECOND, 0);
        calendarAlarm.set(Calendar.MILLISECOND, 0);

        if (calendarAlarm.getTimeInMillis() <= System.currentTimeMillis()) {
            calendarAlarm.add(Calendar.DAY_OF_MONTH, 1);
        }

        int requestCode = (int) System.currentTimeMillis();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarAlarm.getTimeInMillis(), DateUtils.DAY_IN_MILLIS, makePi(requestCode));

        alarm.setCode(requestCode);
        return alarm;
    }

}