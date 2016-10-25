package com.alex_aladdin.snowball;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Альбомный режим
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Подключаем XML-файл, формирующий внешний вид активности
        setContentView(R.layout.activity_report);

        //Получаем ссылки на текстовые поля
        TextView title = (TextView)findViewById(R.id.textTitle);
        TextView tap = (TextView)findViewById(R.id.textTap);
        //Подключаем свой шрифт
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));

        //Мигающий текст
        ObjectAnimator animator = ObjectAnimator.ofFloat(tap, "alpha", 1.0f, 0.0f);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        //Создаем намерение, которое будет возвращено в MainActivity в качестве результата
        Intent answerIntent = new Intent();

        //Выполняем действие в зависимости от значения, полученного из MainActivity
        String status = getIntent().getExtras().getString("status");
        if (status != null)
            switch (status) {
                case "WIN":
                    //Получаем из настроек текущее значение уровня, до которого дошел игрок
                    SharedPreferences sp = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
                    int level = sp.getInt("current_level", 1);
                    //Проверяем, существует ли карта для следующего уровня
                    try {
                        InputStream stream = getAssets().open("level" + (level + 1) + ".txt");
                        stream.close();
                        level++;
                    } catch (IOException e) {
                        Log.i("Report", "Ошибка при проверке существования следующего уровня: " + e);
                        //Выводим соответствующий текст на экран
                        title.setText(R.string.report_game_over);
                        tap.setText(R.string.report_tap_game_over);
                        //Записываем соответствующее значение в answerIntent
                        answerIntent.putExtra("status", "GAME OVER");
                        break;
                    }
                    //Сохраняем в настройки новое значение
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("current_level", level);
                    editor.apply();
                    //Выводим соответствующий текст на экран
                    title.setText(R.string.report_win);
                    //Записываем соответствующее значение в answerIntent
                    answerIntent.putExtra("status", "WIN");
                    break;
                case "LOSE":
                    //Выводим соответствующий текст на экран
                    title.setText(R.string.report_lose);
                    //Записываем соответствующее значение в answerIntent
                    answerIntent.putExtra("status", "LOSE");
                    break;
            }

        setResult(RESULT_OK, answerIntent);
    }

    //По клику на экране закрываем активность
    public void onClick(View view) {
        finish();
    }
}