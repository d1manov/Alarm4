package com.example.alarm4;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        dao = AlarmApp.getInstance().getDatabase().alarmDao();
        alarmsLayout = findViewById(R.id.alarmsLayout);

        dao.getAll().stream().map(Alarm::build).forEach(this::addAlarmView);
    }

    public void openTimePickerDialog(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);

        final EditText hourEditText = dialogView.findViewById(R.id.hourEditText);
        final EditText minuteEditText = dialogView.findViewById(R.id.minuteEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = findViewById(R.id.title);

        builder.setCustomTitle(title)
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", (d, which) -> d.dismiss())
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        dialog.show();

        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(R.color.back);

        Button positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hour_st = hourEditText.getText().toString();
                String minute_st = minuteEditText.getText().toString();

                if (!hour_st.isEmpty() && !minute_st.isEmpty()) {
                    int hour = Integer.parseInt(hourEditText.getText().toString());
                    int minute = Integer.parseInt(minuteEditText.getText().toString());

                    Alarm alarm = new Alarm(hour, minute);

                    if (alarm.validate()) {
                        Alarm alarmZ = startAlarm(alarm);
                        dao.insert(DbAlarm.build(alarmZ));
                        addAlarmView(alarmZ);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Введите корректные данные!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Заполните пустые поля!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @SuppressLint({"ScheduleExactAlarm", "DefaultLocale"})
    private void addAlarmView(final Alarm alarm) {

        RelativeLayout alarmLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 1;
        alarmLayout.setLayoutParams(params);

        // параметры ширины и высоты
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

        //шрифты
        Typeface alarmText_face = ResourcesCompat.getFont(this, R.font.blitzel);
        Typeface deleteButton_face = ResourcesCompat.getFont(this, R.font.kinetika);

        alarmTextView.setTypeface(alarmText_face);
        alarmTextView.setTextSize(45);
        alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.white));

        Button deleteButton = new Button(new ContextThemeWrapper(this, androidx.appcompat.R.style.Widget_AppCompat_Button_Borderless));
        deleteButton.setText("Удалить");
        deleteButton.setTypeface(deleteButton_face);
        deleteButton.setTextSize(10);
        deleteButton.setBackground(null);
        deleteButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        // параметры размещения, чтобы кнопка удаления выравнивалась по правому краю и по центру вертикально
        RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
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

    @SuppressLint("ScheduleExactAlarm")
    private Alarm startAlarm(Alarm alarm) {

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

        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarAlarm.getTimeInMillis(), DateUtils.DAY_IN_MILLIS, makePi(requestCode));
        // alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm.getTimeInMillis(), makePi(requestCode));

        boolean test = alarmManager.canScheduleExactAlarms();

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendarAlarm.getTimeInMillis(), makePi(requestCode));
        alarmManager.setAlarmClock(alarmClockInfo, makePi(requestCode));

        alarm.setCode(requestCode);
        return alarm;
    }
}