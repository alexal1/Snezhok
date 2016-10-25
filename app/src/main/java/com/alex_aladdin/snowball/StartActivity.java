package com.alex_aladdin.snowball;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {
    //Объявляем константу, отвечающую за длину пути пальца по экрану, необходимую для хода (в миллиметрах)
    private final static float TRACE_MM = 20;
    //Координаты соответственно нажатия и движения пальцем по экрану
    private float mX0, mY0, mX, mY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Альбомный режим
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Подключаем XML-файл, формирующий внешний вид активности
        setContentView(R.layout.activity_start);

        //Ф О Н О В А Я   К А Р Т И Н К А
        ImageView imageStart = (ImageView)(findViewById(R.id.image_start));
        //Получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w1 = size.x;
        int h1 = size.y;
        //Загружаем из ресурсов картинку, сразу уменьшенную в число раз, кратное двум, но чтобы была не меньше размеров w1 и h1
        Bitmap bmStart = MainActivity.decodeSampledBitmapFromResource(getResources(), R.drawable.start, w1, h1);
        //Получаем размеры загруженной картинки
        int w2 = bmStart.getWidth();
        int h2 = bmStart.getHeight();
        //Создаем новый Bitmap, масштабируя и обрезая исходную картинку под размеры экрана
        Bitmap bitmap = Bitmap.createBitmap(bmStart, w2 - w1*h2/h1, 0, w1*h2/h1, h2);
        //Удаляем из памяти старый Bitmap
        bmStart.recycle();
        //Помещаем Bitmap в компонент imageStart
        imageStart.setImageBitmap(bitmap);

        //О Б Р А Б О Т К А   К А С А Н И Й
        //Переводим trace из миллиметров в пиксели
        final float trace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, TRACE_MM, getResources().getDisplayMetrics());
        //Вешаем на ImageView обработчик касаний OnTouchListener, который реализуется методом onTouch
        imageStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Запоминаем координаты первого касания и сравниваем с координатами перемещения пальца
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX0 = event.getX();
                        mY0 = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mX = event.getX();
                        mY = event.getY();

                        //Если смещение по X превышает смещение по Y, причем движение слева направо, закрываем активность
                        if ((Math.abs(mX - mX0) > trace) && (Math.abs(mY - mY0) < trace) && (mX > mX0))
                            finish();

                        break;
                }
                return true;
            }
        });
    }
}