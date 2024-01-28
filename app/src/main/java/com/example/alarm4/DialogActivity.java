package com.example.alarm4;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DialogActivity extends AppCompatActivity {
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringtone = launchRingtone();
        showAlarmWindow();
    }

    private void showAlarmWindow() {
        EditText answerEditText = new EditText(DialogActivity.this);
        answerEditText.setTextColor(ContextCompat.getColor(DialogActivity.this, R.color.white));
        answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        MathProblem mathproblem = MathProblem.generate(4);

        AlertDialog alertDialog = new AlertDialog.Builder(DialogActivity.this, R.style.AlertDialogCustomStyle)
                .setCancelable(false)
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
