//Название пакета
package com.alex_aladdin.snowball;

//Импорт необходимых для проекта классов
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements DialogReset.ResetDialogListener {
    //Объявляем константу, отвечающую за длину пути пальца по экрану, необходимую для хода (в миллиметрах)
    private final static float TRACE_MM = 2;
    //Координаты соответственно нажатия и движения пальцем по экрану
    private float mX0, mY0, mX, mY;
    //Переменная, хранящая ссылку на игровое поле (объект класса GameView)
    private GameView mView;
    //Переменная, дающая доступ к игровым настройкам
    private SharedPreferences mSharedPreferences;
    //Реклама
    InterstitialAd mInterstitialAd;
    //Константы со значениями RequestCode вызываемых активностей
    public final static int REPORT_ACTIVITY = 0, START_ACTIVITY = 1;

    //Метод onCreate() вызывается, когда приложение создает и отображает разметку активности
    @Override //Метод переопределяется из базового класса
    protected void onCreate(Bundle savedInstanceState) {
        //Конструктор родительского класса, выполняющий необходимые операции для работы активности
        super.onCreate(savedInstanceState);
        //Альбомный режим
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Подключаем XML-файл, формирующий внешний вид активности
        setContentView(R.layout.activity_main);

        //Ф О Н О В А Я   К А Р Т И Н К А
        ImageView imageBackground = (ImageView)(findViewById(R.id.image_background));
        //Получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w1 = size.x;
        int h1 = size.y;
        //Загружаем из ресурсов картинку, сразу уменьшенную в число раз, кратное двум, но чтобы была не меньше размеров w1 и h1
        Bitmap bmBackground = decodeSampledBitmapFromResource(getResources(), R.drawable.background, w1, h1);
        //Получаем размеры загруженной картинки
        int w2 = bmBackground.getWidth();
        int h2 = bmBackground.getHeight();
        //Создаем новый Bitmap, масштабируя и обрезая исходную картинку под размеры экрана
        Bitmap bitmap = Bitmap.createBitmap(bmBackground, w2 - w1*h2/h1, 0, w1*h2/h1, h2);
        //Удаляем из памяти старый Bitmap
        bmBackground.recycle();
        //Помещаем Bitmap в компонент imageBackground
        imageBackground.setImageBitmap(bitmap);

        //М Е Н Ю
        //Получаем ссылки на все элементы меню
        final RelativeLayout layoutMenu = (RelativeLayout)findViewById(R.id.menu);
        final AutoFitTextView textLevel = (AutoFitTextView)findViewById(R.id.level_text);
        final TextView textLevelNumber = (TextView)findViewById(R.id.level_number);
        final AutoFitButton buttonRestart = (AutoFitButton)findViewById(R.id.button_restart);
        final Button buttonReset = (Button)findViewById(R.id.button_reset);
        final Button buttonAbout = (Button)findViewById(R.id.button_about);
        //Меню должно быть шириной в 1/3 высоты экрана, добиваемся этого с помощью отступов
        int padding = (w1 - h1 - h1/3) / 2;
        layoutMenu.setPadding(padding, 0, padding, 0);
        //Подключаем шрифты
        textLevel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        textLevelNumber.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        buttonRestart.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        buttonReset.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        buttonAbout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bulldozer.ttf"));
        //Ждем, когда textLevel выровняется по ширине меню, чтобы передать его размер шрифта в textLevelNumber
        textLevel.setSizeChangeListener(new AutoFitTextView.OnSizeChangeListener() {
            @Override
            public void onEvent() {
                //Получаем размер шрифта textLevel и преобразовываем из px в sp
                float size = textLevel.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                //Передаем полученный размер шрифта компоненту textLevelNumber
                textLevelNumber.setTextSize(size);
                //Убираем за ненадобностью слушатель событий
                textLevel.setSizeChangeListener(null);
            }
        });
        //Ждем, когда buttonRestart выровняется по ширине меню, чтобы передать её размер шрифта в buttonReset и buttonAbout
        buttonRestart.setSizeChangeListener(new AutoFitButton.OnSizeChangeListener() {
            @Override
            public void onEvent() {
                //Получаем размер шрифта buttonRestart и преобразовываем из px в sp
                float size = buttonRestart.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                //Передаем полученный размер шрифта остальным кнопкам
                buttonReset.setTextSize(size);
                buttonAbout.setTextSize(size);
                //Убираем за ненадобностью слушатель событий
                buttonRestart.setSizeChangeListener(null);
            }
        });

        //Р Е К Л А М А
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7537570926609688/5714443853");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        //О Б Р А Б О Т К А   К А С А Н И Й
        //Переводим trace из миллиметров в пиксели
        final float trace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, TRACE_MM, getResources().getDisplayMetrics());
        //Вешаем на GameView обработчик касаний OnTouchListener, который реализуется методом onTouch
        final GameView view = (GameView)findViewById(R.id.game_view);
        view.setOnTouchListener(new View.OnTouchListener() {
            //Метод должен вернуть true, если мы сами обработали событие, и false, если нет
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Если касание происходит, когда предыдущее движение ещё не закончилось, игнорируем событие
                if (!(view.mPlayer.getDirection().equals("none"))) return false;
                //Если касание происходит, когда очередь ходить не игроку, игнорируем событие
                if (view.mLogic.getTurn() != 3) return false;
                //Запоминаем координаты первого касания и сравниваем с координатами перемещения пальца
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX0 = event.getX();
                        mY0 = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mX = event.getX();
                        mY = event.getY();
                        //Если смещение по Y превышает смещение по X
                        if ((Math.abs(mX - mX0) < trace) && (Math.abs(mY - mY0) > trace)) {
                            if (mY > mY0) view.mPlayer.setDirection("down");
                            if (mY < mY0) view.mPlayer.setDirection("up");
                            //Уводим стартовые координаты, чтобы следующий приказ вышел только при повторном касании
                            mX0 = -trace;
                            mY0 = -trace;

                            if (view.mPlayer.move()) {
                                //Если движение в выбранном направлении возможно, перерисовываем игровое поле
                                v.invalidate();
                            } else {
                                //Если невозможно, ничего не делаем и возвращаем очередь хода игроку
                                view.mLogic.setTurn(3);
                            }
                        }
                        //Если смещение по X превышает смещение по Y
                        if ((Math.abs(mX - mX0) > trace) && (Math.abs(mY - mY0) < trace)) {
                            if (mX > mX0) view.mPlayer.setDirection("right");
                            if (mX < mX0) view.mPlayer.setDirection("left");
                            //Уводим стартовые координаты, чтобы следующий приказ вышел только при повторном касании
                            mX0 = -trace;
                            mY0 = -trace;

                            if (view.mPlayer.move()) {
                                //Если движение в выбранном направлении возможно, перерисовываем игровое поле
                                v.invalidate();
                            } else {
                                //Если невозможно, ничего не делаем и возвращаем очередь хода игроку
                                view.mLogic.setTurn(3);
                            }
                        }
                        break;
                }
                return true;
            }
        });

        //Сохраняем ссылку на игровое поле
        mView = view;

        //З А Г Р У З К А   У Р О В Н Я
        //Инициализируем переменную, дающую доступ к игровым настройкам (это достаточно сделать один раз при открытии)
        mSharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        //Передаем методу load() в GameView значение текущего уровня, взятое из настроек, либо 1 по умолчанию
        int level = mSharedPreferences.getInt("current_level", 1);
        mView.load(level);
        //Отображем значение level в соответствующем текстовом поле
        textLevelNumber.setText(String.valueOf(level));
        //Если это первый уровень, загружаем StartActivity
        if (level == 1) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivityForResult(intent, START_ACTIVITY);
        }
    }

    public void onClickRestart(View view) {
        //Передаем методу load() в GameView значение текущего уровня, взятое из настроек, либо 1 по умолчанию
        int level = mSharedPreferences.getInt("current_level", 1);
        mView.load(level);
    }

    public void onClickReset(View view) {
        //Вызываем диалоговое окно
        FragmentManager manager = getSupportFragmentManager();
        DialogReset dialogReset = new DialogReset();
        dialogReset.show(manager, "dialog");
    }

    public void onClickAbout(View view) {
        //Вызываем окно ABOUT
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    //Если игрок выбрал YES в диалоге DialogReset
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Сохраняем в настройки значение 1
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("current_level", 1);
        editor.apply();
        //Загружаем первый уровень
        mView.load(1);
        //Отображаем новое значение уровня в соответствующем текстовом поле
        final TextView textLevelNumber = (TextView)findViewById(R.id.level_number);
        textLevelNumber.setText("1");
        //Загружаем StartActivity
        Intent intent = new Intent(this, StartActivity.class);
        startActivityForResult(intent, START_ACTIVITY);
    }

    //Когда закрывается одна из активностей ReportActivity или StartActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //После закрытия ReportActivity
        if (requestCode == REPORT_ACTIVITY) {
            //Передаем методу load() в GameView значение текущего уровня, взятое из настроек, либо 1 по умолчанию
            int level = mSharedPreferences.getInt("current_level", 1);
            mView.load(level);
            //Отображем значение level в соответствующем текстовом поле
            final TextView textLevelNumber = (TextView) findViewById(R.id.level_number);
            textLevelNumber.setText(String.valueOf(level));

            //В случае проигрыша выводим рекламу
            if (data.getStringExtra("status").equals("LOSE")) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        }

        //После закрытия StartActivity
        if (requestCode == START_ACTIVITY) {
            //Запускаем движение игрока вправо
            mView.mPlayer.setDirection("right");
            mView.invalidate();
        }
    }

    //Загрузка рекламы
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("316431A1463845B55ABCD3C8809EEB2D")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    //Вспомогательный метод для decodeSampledBitmapFromResource, рассчитывающий значение коэффициента inSampleSize
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //Высота и ширина оригинального изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            //Вычисляем наибольшее значение inSampleSize, являющееся степенью двойки, такое чтобы при этом одновременно высота и ширина
            //итоговой картинки были больше, чем reqHeight и reqWidth
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //Метод, получающий из ресурсов сразу уменьшенное изображение (в кратное двум число раз), чтобы оно было не меньше заданных размеров
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        //Сначала декодируем с inJustDecodeBounds = true, чтобы узнать размеры оригинала
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //Рассчитываем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Теперь декодируем изображение, сразу уменьшенное в inSampleSize раз
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}