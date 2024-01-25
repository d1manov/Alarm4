package com.example.alarm4;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchRingtone();
        showAlarmWindow();
    }

    private void showAlarmWindow() {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(DialogActivity.this);
        alertDialog.setTitle("Сработал будильник");
        alertDialog.setMessage("Решите пример: " + generateMathProblem());
        final EditText answerEditText = new EditText(DialogActivity.this);
        alertDialog.setView(answerEditText);

        alertDialog.setPositiveButton("Выключить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = answerEditText.getText().toString().trim();
                if (validateAnswer(answer)) {
                    stopAlarm();
                } else {
                    // TODO
                }
            }
        });

        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void stopAlarm() {
        // TODO
    }

    public void launchRingtone() {
        // Воспроизведение звука будильника
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
    }

    private String generateMathProblem() {
        Random random = new Random();
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;
        int operator = random.nextInt(3); // 0: addition, 1: subtraction, 2: multiplication
        String problem;

        switch (operator) {
            case 0:
                problem = num1 + " + " + num2 + " = ?";
                break;
            case 1:
                problem = num1 + " - " + num2 + " = ?";
                break;
            case 2:
                problem = num1 + " * " + num2 + " = ?";
                break;
            default:
                problem = "";
        }

        return problem;
    }

    private boolean validateAnswer(String answer) {
        try {
            int result = Integer.parseInt(answer);
            // Проверка правильности ответа
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
