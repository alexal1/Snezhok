package com.alex_aladdin.snowball;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

//Не объявляем класс как public, поскольку он должен быть видимым только внутри пакета
class Snowball {
    //Объявляем константу, отвечающую за скорость анимации
    private final static int STEP = 4;

    private GameView mView;
    private Paint mPaint;
    private Rect mRectSrc, mRectDst;
    private int mX, mY, mSize;
    private String mDirection;
    private Boolean mIsAlive;

    //Конструктор для снежка
    //Параметр view -- компонент, в котором используется снежок
    Snowball(GameView view) {
        mView = view;
        mRectSrc = new Rect();
        mRectDst = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    //Начальная позиция, размер и направление движения
    void init() {
        mX = mView.mMap.getStartPosX(this);
        mY = mView.mMap.getStartPosY(this);
        mSize = 4;
        mDirection = "none";
        //Снежок при создании считается живым, если при загрузке карты для него указана клетка
        //В противном случае объект Map возвращает отрицательные исходные координаты
        mIsAlive = ((mX >= 0)&&(mY >= 0));
    }

    //Рисуем снежок
    void draw(Canvas canvas, Bitmap bitmap) {
        mRectSrc.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mRectDst.set(mX + (Map.TILE_SIZE - mSize) / 2, mY + (Map.TILE_SIZE - mSize) / 2,
                mX + (Map.TILE_SIZE + mSize) / 2, mY + (Map.TILE_SIZE + mSize) / 2);
        canvas.drawBitmap(bitmap, mRectSrc, mRectDst, mPaint);
    }

    //Задание направлния дальнейшего движения
    void setDirection(String direction) {
        this.mDirection = direction;
    }

    //Возвращает текущее направление движения
    String getDirection() { return this.mDirection; }

    //Увеличивает размер снежка
    private void grow() { mSize += 2; }

    //Движение на единицу в направлении, заданном переменной mDirection
    //Возвращает true, если было совершено какое-либо перемещение, и false, если нет
    boolean move() {
        //Если мы уже погибли, то не двигаемся
        if (!mIsAlive) return false;
        //Если не наша очередь, то не двигаемся
        for (int i = 0; i <= 2; i++)
            if ((mView.mLogic.getTurn() == i)&&(this != mView.mEnemy[i])) return false;
        if ((mView.mLogic.getTurn() == 3)&&(this != mView.mPlayer)) return false;

        switch (mDirection) {
            case "down":
                //Останавливаемся, если доходим до края карты
                if (mY + Map.TILE_SIZE == Map.TILE_SIZE*Map.TILES_ABR) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до WALL_TILE (плитки стены)
                if (mView.mMap.getTileType(mX, mY + Map.TILE_SIZE).equals("WALL_TILE")) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до снежка такого же или большего размера
                if (mView.isSnowball(mX, mY + Map.TILE_SIZE) != null)
                    if (mView.isSnowball(mX, mY + Map.TILE_SIZE).getSize() >= mSize) {
                        setDirection("none");
                        mView.mLogic.nextTurn(); //Передаем ход следующему
                        return false;
                    }
                //Если покидаем SPINY_TILE (плитку с шипами), убираем на ней шипы
                if (mView.mMap.getTileType(mX, mY).equals("SPINY_TILE")) mView.mMap.clearTile(mX, mY);
                //Если покидаем CRACK_TILE (плитку с трещинами), делаем в ней дыру
                if (mView.mMap.getTileType(mX, mY).equals("CRACK_TILE")) mView.mMap.holeTile(mX, mY);

                mY += STEP;
                break;
            case "up":
                //Останавливаемся, если доходим до края карты
                if (mY == 0) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до WALL_TILE (плитки стены)
                if (mView.mMap.getTileType(mX, mY - Map.TILE_SIZE).equals("WALL_TILE")) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до снежка такого же или большего размера
                if (mView.isSnowball(mX, mY - Map.TILE_SIZE) != null)
                    if (mView.isSnowball(mX, mY - Map.TILE_SIZE).getSize() >= mSize) {
                        setDirection("none");
                        mView.mLogic.nextTurn(); //Передаем ход следующему
                        return false;
                    }
                //Если покидаем SPINY_TILE (плитку с шипами), убираем на ней шипы
                if (mView.mMap.getTileType(mX, mY).equals("SPINY_TILE")) mView.mMap.clearTile(mX, mY);
                //Если покидаем CRACK_TILE (плитку с трещинами), делаем в ней дыру
                if (mView.mMap.getTileType(mX, mY).equals("CRACK_TILE")) mView.mMap.holeTile(mX, mY);

                mY -= STEP;
                break;
            case "right":
                //Останавливаемся, если доходим до края карты
                if (mX + Map.TILE_SIZE == Map.TILE_SIZE*Map.TILES_ABR) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до WALL_TILE (плитки стены)
                if (mView.mMap.getTileType(mX + Map.TILE_SIZE, mY).equals("WALL_TILE")) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до снежка такого же или большего размера
                if (mView.isSnowball(mX + Map.TILE_SIZE, mY) != null)
                    if (mView.isSnowball(mX + Map.TILE_SIZE, mY).getSize() >= mSize) {
                        setDirection("none");
                        mView.mLogic.nextTurn(); //Передаем ход следующему
                        return false;
                    }
                //Если покидаем SPINY_TILE (плитку с шипами), убираем на ней шипы
                if (mView.mMap.getTileType(mX, mY).equals("SPINY_TILE")) mView.mMap.clearTile(mX, mY);
                //Если покидаем CRACK_TILE (плитку с трещинами), делаем в ней дыру
                if (mView.mMap.getTileType(mX, mY).equals("CRACK_TILE")) mView.mMap.holeTile(mX, mY);

                mX += STEP;
                break;
            case "left":
                //Останавливаемся, если доходим до края карты
                if (mX == 0) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до WALL_TILE (плитки стены)
                if (mView.mMap.getTileType(mX - Map.TILE_SIZE, mY).equals("WALL_TILE")) {
                    setDirection("none");
                    mView.mLogic.nextTurn(); //Передаем ход следующему
                    return false;
                }
                //Останавливаемся, если доходим до снежка такого же или большего размера
                if (mView.isSnowball(mX - Map.TILE_SIZE, mY) != null)
                    if (mView.isSnowball(mX - Map.TILE_SIZE, mY).getSize() >= mSize) {
                        setDirection("none");
                        mView.mLogic.nextTurn(); //Передаем ход следующему
                        return false;
                    }
                //Если покидаем SPINY_TILE (плитку с шипами), убираем на ней шипы
                if (mView.mMap.getTileType(mX, mY).equals("SPINY_TILE")) mView.mMap.clearTile(mX, mY);
                //Если покидаем CRACK_TILE (плитку с трещинами), делаем в ней дыру
                if (mView.mMap.getTileType(mX, mY).equals("CRACK_TILE")) mView.mMap.holeTile(mX, mY);

                mX -= STEP;
                break;
            case "none":
                return false;
        }
        //Если оказывается, что мы стоим на плитке с другим снежком, увеличиваем размер и убиваем его
        if ((mView.isSnowball(mX, mY) != null)&&(mView.isSnowball(mX, mY) != this)) {
            grow();
            mView.isSnowball(mX, mY).die();
        }
        //Если оказывается, что мы стоим на SNOWY_TILE (снежной плитке), увеличиваем размер и очищаем плитку
        if (mView.mMap.getTileType(mX, mY).equals("SNOWY_TILE")) {
            grow();
            mView.mMap.clearTile(mX, mY);
        }
        //Если оказывается, что мы стоим на SPINY_TILE (плитке с шипами), останавливаемся
        if (mView.mMap.getTileType(mX, mY).equals("SPINY_TILE")) {
            setDirection("none");
            mView.mLogic.nextTurn();
        }
        //Если оказывается, что мы стоим на HOLE_TILE (плитке с прорубью), погибаем и передаем ход следующему
        if(mView.mMap.getTileType(mX, mY).equals("HOLE_TILE")) {
            die();
            mView.mLogic.nextTurn();
        }

        return true;
    }

    //Снежок погибает (либо попадает в прорубь, либо его съедает другой снежок)
    private void die() {
        mIsAlive = false;
        mX = -Map.TILE_SIZE;
        mY = -Map.TILE_SIZE;
        //А также возвращаем к исходному размеру, чтобы его края не было, если он стал слишком большим
        mSize = 4;
    }

    //Метод, возвращающий значение переменной mIsAlive
    boolean isAlive() { return mIsAlive; }

    //Метод, возвращающий значение переменной mX
    int getX() { return mX; }

    //Метод, возвращающий значение переменной mY
    int getY() { return mY; }

    //Метод, возвращающий значение переменной mSize
    int getSize() {return mSize; }
}