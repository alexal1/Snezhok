package com.alex_aladdin.snowball;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Альбомный режим
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Подключаем XML-файл, формирующий внешний вид активности
        setContentView(R.layout.activity_about);
        //Подключаем шрифты
        TextView textLong = (TextView)findViewById(R.id.long_text);
        TextView textCaption = (TextView)findViewById(R.id.caption_text);
        textLong.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        textCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
    }

    public void onScreenClick(View view) {
        finish();
    }

    public void onLinkClick(View view) {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException exception) {
            //На случай если не установлен Play Store
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}