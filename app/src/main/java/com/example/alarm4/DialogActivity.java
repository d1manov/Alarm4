package com.example.alarm4;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DialogActivity extends AppCompatActivity {
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringtone = launchRingtone();
        showAlarmWindow();
    }

//    @Override
//    protected void onResume() {
//        String[] array = {"com.example.alarm4"};
//        DevicePolicyManager dpm = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
//        dpm.setLockTaskPackages(getComponentName(), array);
//        super.onResume();
//        this.startLockTask();
//    }

    private void showAlarmWindow() {
        EditText answerEditText = new EditText(DialogActivity.this);
        answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        MathProblem mathproblem = MathProblem.generate(4);

        AlertDialog alertDialog = new AlertDialog.Builder(DialogActivity.this)
                .setView(answerEditText)
                .setPositiveButton("Выключить", null)
                .setTitle("Сработал будильник")
                .setMessage("Решите пример: " + mathproblem.expression)
                .show();

        // Получение корневого представления диалога
        View dialogView = alertDialog.getWindow().getDecorView();

        // Установка цвета фона на корневом представлении диалога
        dialogView.setBackgroundColor(ContextCompat.getColor(DialogActivity.this, R.color.back));

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = answerEditText.getText().toString().trim();
                if (validateAnswer(answer, mathproblem)) {
                    stopAlarm();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Неправильно!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void stopAlarm() {
        ringtone.stop();
        this.finish();
    }

    public Ringtone launchRingtone() {
        // Воспроизведение звука будильника
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
        return ringtone;
    }

    private boolean validateAnswer(String answer, MathProblem problem) {
        int result;
        try {
            result = Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            return false;
        }

        return result == problem.answer;
    }
}
