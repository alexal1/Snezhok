package com.alex_aladdin.snowball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

//Объявляем публичный (доступный из любого другого кода проекта) класс GameView, наследующий от View
public class GameView extends View{
    //Объявляем переменные для отрисовки фона
    private Bitmap bmEmpty, bmWall, bmSnowy, bmSpiny, bmCrack, bmHole;
    private Paint mPaint;
    private Rect mRectSrc, mRectDst;
    private Canvas mCanvas;
    //Объекты для подсветки положения игрока и направлений движения врагов
    private Bitmap bmPlayer, bmRightArrow, bmUpArrow, bmLeftArrow, bmDownArrow;
    //Карта
    public Map mMap;
    //Снежки
    public Snowball mPlayer;
    public Snowball mEnemy[] = new Snowball[3];
    private Bitmap bmSnowball;
    //Игровая логика
    public Logic mLogic;
    //Переменная, хранящая ссылку на контекст, полученный из MainActivity
    private Context mContext;

    //Конструктор класса
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Сохраняем ссылку на контекст для использования в методе load()
        mContext = context;

        //Получаем объекты для отрисовки фона
        bmEmpty = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        bmWall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        bmSnowy = BitmapFactory.decodeResource(getResources(), R.drawable.snowy);
        bmSpiny = BitmapFactory.decodeResource(getResources(), R.drawable.spiny);
        bmCrack = BitmapFactory.decodeResource(getResources(), R.drawable.crack);
        bmHole = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectSrc = new Rect();
        mRectDst = new Rect();

        //Получаем объекты для подсветки
        bmPlayer = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        bmRightArrow = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
        bmUpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow);
        bmLeftArrow = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
        bmDownArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down_arrow);

        //Получаем объект рисунка снежка
        bmSnowball = BitmapFactory.decodeResource(getResources(), R.drawable.snowball);
    }

    //Переопределяем метод View.onDraw() -- рисование на холсте
    //protected -- метод доступен самому классу и его наследникам, а также всем в пределах этого пакета
    @Override
    protected void onDraw(Canvas canvas) {
        int i, j;
        //Сохраняем ссылку на канву для использования в методе illumination()
        mCanvas = canvas;
        //Рисуем фон
        canvas.scale(canvas.getWidth() / (float) (Map.TILES_ABR * Map.TILE_SIZE), canvas.getHeight() / (float) (Map.TILES_ABR * Map.TILE_SIZE));
        mRectSrc.set(0, 0, bmEmpty.getWidth(), bmEmpty.getHeight());
        for (i = 0; i < Map.TILES_ABR; i++)
            for (j = 0; j < Map.TILES_ABR; j++) {
                mRectDst.set(i*Map.TILE_SIZE, j*Map.TILE_SIZE, (i + 1)*Map.TILE_SIZE, (j + 1)*Map.TILE_SIZE);
                //В зависимости от типа плитки (определяется по координатам левого верхнего угла) выводим соответствующую картинку
                switch (mMap.getTileType(i*Map.TILE_SIZE, j*Map.TILE_SIZE)) {
                    case "EMPTY_TILE":
                        canvas.drawBitmap(bmEmpty, mRectSrc, mRectDst, mPaint);
                        break;
                    case "SNOWY_TILE":
                        canvas.drawBitmap(bmSnowy, mRectSrc, mRectDst, mPaint);
                        break;
                    case "WALL_TILE":
                        canvas.drawBitmap(bmWall, mRectSrc, mRectDst, mPaint);
                        break;
                    case "SPINY_TILE":
                        canvas.drawBitmap(bmSpiny, mRectSrc, mRectDst, mPaint);
                        break;
                    case "CRACK_TILE":
                        canvas.drawBitmap(bmCrack, mRectSrc, mRectDst, mPaint);
                        break;
                    case "HOLE_TILE":
                        canvas.drawBitmap(bmHole, mRectSrc, mRectDst, mPaint);
                        break;
                }
            }

        mPlayer.draw(canvas, bmSnowball);
        mEnemy[0].draw(canvas, bmSnowball);
        mEnemy[1].draw(canvas, bmSnowball);
        mEnemy[2].draw(canvas, bmSnowball);
        if ((mPlayer.move()) || (mEnemy[0].move()) || (mEnemy[1].move()) || (mEnemy[2].move())) {
            //Запускаем движение кажого из снежков, и если хоть один из них сдвигается, перерисовываем канву
            invalidate();
        } else
            //Если ни одно движение не выполняется, включаем подсветку игрока и направлений движения врагов
            illumination();
    }

    //Метод, возвращающий объект наименьшего снежка, который находится в клетке с данными координатами, либо null
    //Этот метод используется при движении снежков, чтобы реагировать на другие снежки на карте
    //Возвращать наименьший нужно, чтобы было ясно, есть ли на клетке снежка другой снежок (большего размера другой быть не может)
    public Snowball isSnowball(int x, int y) {
        Snowball snowball = null;
        int size = Map.TILE_SIZE*2; //Берем исходное значение с запасом

        for (int i = 0; i <= 2; i++)
            if ((mEnemy[i].getX() == x)&&(mEnemy[i].getY() == y))
                if (mEnemy[i].getSize() < size) {
                    size = mEnemy[i].getSize();
                    snowball = mEnemy[i];
                }

        if ((mPlayer.getX() == x)&&(mPlayer.getY() == y))
            if(mPlayer.getSize() < size) return mPlayer;

        return snowball;
    }

    //Метод, рисующий подсветку положения игрока и направлений движения врагов
    //Вызывается, когда все снежки останавливаются
    private void illumination() {
        int x, y;
        //Сначала реализуем подсветку игрока
        x = mPlayer.getX();
        y = mPlayer.getY();
        mRectSrc.set(0, 0, bmPlayer.getWidth(), bmPlayer.getHeight());
        mRectDst.set(x, y, x + Map.TILE_SIZE, y + Map.TILE_SIZE);
        mCanvas.drawBitmap(bmPlayer, mRectSrc, mRectDst, mPaint);
        //Чтобы подсветка не перекрывала снежок игрока, если он очень большой, рисуем его ещё раз поверх
        mPlayer.draw(mCanvas, bmSnowball);

        //Теперь реализуем подсветку направлений движения для каждого врага
        for (int i = 0; i <= 2; i++) {
            //Проверяем, жив ли рассматриваемый снежок
            if (!mEnemy[i].isAlive()) continue;

            x = mEnemy[i].getX();
            y = mEnemy[i].getY();
            switch (mEnemy[i].getDirection()) {
                case "right":
                    mRectDst.set(x + Map.TILE_SIZE, y, x + 2*Map.TILE_SIZE, y + Map.TILE_SIZE);
                    mCanvas.drawBitmap(bmRightArrow, mRectSrc, mRectDst, mPaint);
                    break;
                case "up":
                    mRectDst.set(x, y - Map.TILE_SIZE, x + Map.TILE_SIZE, y);
                    mCanvas.drawBitmap(bmUpArrow, mRectSrc, mRectDst, mPaint);
                    break;
                case "left":
                    mRectDst.set(x - Map.TILE_SIZE, y, x, y + Map.TILE_SIZE);
                    mCanvas.drawBitmap(bmLeftArrow, mRectSrc, mRectDst, mPaint);
                    break;
                case "down":
                    mRectDst.set(x, y + Map.TILE_SIZE, x + Map.TILE_SIZE, y + 2 * Map.TILE_SIZE);
                    mCanvas.drawBitmap(bmDownArrow, mRectSrc, mRectDst, mPaint);
                    break;
            }
        }
    }

    //Метод, создающий необходимые для каждого нового уровня объекты
    public void load(int level) {
        //Получаем объект карты для заданного уровня
        mMap = new Map(this, mContext, level);

        //Получаем объекты снежков
        mPlayer = new Snowball(this);
        mPlayer.init();
        for (int i = 0; i <= 2; i++) {
            mEnemy[i] = new Snowball(this);
            mEnemy[i].init();
        }

        //Получаем объект класса, реализующего игровую логику
        mLogic = new Logic(this);

        //Перерисовываем канву
        invalidate();
    }

    //Метод, вызываемый при победе игрока
    public void win() {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.putExtra("status", "WIN");
        //Вызываем активность ReportActivity с возвратом результата (в MainActivity) и значением RequestCode, равным константе REPORT_ACTIVITY
        ((Activity)mContext).startActivityForResult(intent, MainActivity.REPORT_ACTIVITY);
    }

    //Метод, вызываемый при проигрыше игрока
    public void lose() {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.putExtra("status", "LOSE");
        //Вызываем активность ReportActivity с возвратом результата (в MainActivity) и значением RequestCode, равным константе REPORT_ACTIVITY
        ((Activity)mContext).startActivityForResult(intent, MainActivity.REPORT_ACTIVITY);
    }

    //Переопределяем метод View.onMeasure() -- делаем GameView квадратным
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int length;
        //Вызываем метод onMeasure класса GameView, чтобы рассчитать размеры компонента стандартным образом
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Получаем рассчитанную высоту
        length = getMeasuredHeight();
        //Теперь задаем новый размер
        setMeasuredDimension(length, length);
    }
}