package com.example.alarm4;

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

public class DialogActivity extends AppCompatActivity {
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringtone = launchRingtone();
        showAlarmWindow();
    }

    private void showAlarmWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this, R.style.AlertDialogCustomStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alarm_window, null);
        MathProblem mathproblem = MathProblem.generate(4);

        TextView textView = dialogView.findViewById(R.id.math_problem);
        textView.setText(mathproblem.expression);

        EditText editText = dialogView.findViewById(R.id.answer_math_problem);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        builder.setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("Выключить", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = editText.getText().toString().trim();
                if (validateAnswer(answer, mathproblem)) {
                    stopAlarm();
                    alertDialog.dismiss();
                } else if (answer.equals("")) {
                    Toast.makeText(getApplicationContext(), "Решите пример!", Toast.LENGTH_SHORT).show();
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
