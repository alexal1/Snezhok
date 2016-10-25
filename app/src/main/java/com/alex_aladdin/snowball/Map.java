package com.alex_aladdin.snowball;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

//Не объявляем класс как public, поскольку он должен быть видимым только внутри пакета
class Map {
    //Объявляем константы
    //public -- доступна из любого класса
    //final -- запрещено любое изменение значения, кроме инициализации
    //static -- едина для всех объектов класса
    final static int TILES_ABR = 10; //число плиток в ряд (tiles abreast)
    final static int TILE_SIZE = 8; //размер одной плитки
    //Обозначения плиток
    private final static int EMPTY_TILE = 0; //пустая
    private final static int WALL_TILE = 1; //стена
    private final static int SNOWY_TILE = 2; //снег
    private final static int SPINY_TILE = 3; //шипы
    private final static int CRACK_TILE = 4; //трещины
    private final static int HOLE_TILE = 5; //прорубь
    private final static int ENEMY0_TILE = 6; //стартовая позиция врага0
    private final static int ENEMY1_TILE = 7; //стартовая позиция врага1
    private final static int ENEMY2_TILE = 8; //стартовая позиция врага2
    private final static int PLAYER_TILE = 9; //стартовая позиция игрока

    private GameView mView;
    private int mMapDataArray[];

    //Конструктор карты
    Map(GameView view, Context context, int level) {
        //Обновляем ссылку на игровое поле
        mView = view;
        //Данные хранятся в папке assets в виде level1.txt, level2.txt и т.д.
        String filename = "level" + level + ".txt";
        //Файловый поток
        InputStream stream = null;
        try {
            //Конструируем массив
            mMapDataArray = new int[TILES_ABR*TILES_ABR];
            //Открываем нужный файл
            stream = context.getAssets().open(filename);
            //Проходим через все символы, хранящиеся в Unicode, конвертируем их и помещаем их в массив
            for (int i = 0; i < mMapDataArray.length; i++) {
                mMapDataArray[i] = Character.getNumericValue(stream.read());
                //Пропускаем " ," между каждыми двумя цифрами и "\r\n" в конце каждой строки
                Log.i("Map", filename + " пропускаем символ " + stream.read());
                Log.i("Map", filename + " пропускаем символ " + stream.read());
            }
        } catch (IOException e) {
            Log.i("Map", "Ошибка при открытии файла: " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.i("Map", "Ошибка при закрытии файла: " + e);
                }
            }
        }
    }

    //Метод, возвращающий X-координату данного снежка при загрузке карты
    int getStartPosX(Snowball snowball) {
        if (snowball == mView.mPlayer) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Остаток от деления номера в массиве на размер целой строки это и есть как раз номер плитки в строке
                if (mMapDataArray[i] == PLAYER_TILE) return ((i % TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[0]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Остаток от деления номера в массиве на размер целой строки это и есть как раз номер плитки в строке
                if (mMapDataArray[i] == ENEMY0_TILE) return ((i % TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[1]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Остаток от деления номера в массиве на размер целой строки это и есть как раз номер плитки в строке
                if (mMapDataArray[i] == ENEMY1_TILE) return ((i % TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[2]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Остаток от деления номера в массиве на размер целой строки это и есть как раз номер плитки в строке
                if (mMapDataArray[i] == ENEMY2_TILE) return ((i % TILES_ABR) * TILE_SIZE);
        }
        //Если положение данного снежка не обнаружено, выводим его за пределы карты
        return(-TILE_SIZE);
    }

    //Метод, возвращающий Y-координату данного снежка при загрузке карты
    int getStartPosY(Snowball snowball) {
        if (snowball == mView.mPlayer) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Частное от деления номера в массиве на размер целой строки это и есть как раз номер текущей строки
                if (mMapDataArray[i] == PLAYER_TILE) return((i / TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[0]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Частное от деления номера в массиве на размер целой строки это и есть как раз номер текущей строки
                if (mMapDataArray[i] == ENEMY0_TILE) return((i / TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[1]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Частное от деления номера в массиве на размер целой строки это и есть как раз номер текущей строки
                if (mMapDataArray[i] == ENEMY1_TILE) return((i / TILES_ABR) * TILE_SIZE);
        }
        if (snowball == mView.mEnemy[2]) {
            for (int i = 0; i < mMapDataArray.length; i++)
                //Частное от деления номера в массиве на размер целой строки это и есть как раз номер текущей строки
                if (mMapDataArray[i] == ENEMY2_TILE) return((i / TILES_ABR) * TILE_SIZE);
        }
        //Если положение данного снежка не обнаружено, выводим его за пределы карты
        return(-TILE_SIZE);
    }

    //Метод, возвращающий тип плитки, где (x, y) -- координаты левого верхнего угла этой плитки
    String getTileType(int x, int y) {
        //Если координаты не делятся нацело на размер плитки, значит мы ещё в движении...
        //А значит, можно вернуть EMPTY_TILE, чтобы метод move() снежка продолжал движение
        if ((y % TILE_SIZE != 0)||(x % TILE_SIZE != 0)) return "EMPTY_TILE";
        int row = y / TILE_SIZE;
        int col = x / TILE_SIZE;
        switch (mMapDataArray[row * TILES_ABR + col]) {
            case WALL_TILE:
                return "WALL_TILE";
            case SNOWY_TILE:
                return "SNOWY_TILE";
            case SPINY_TILE:
                return "SPINY_TILE";
            case CRACK_TILE:
                return "CRACK_TILE";
            case HOLE_TILE:
                return "HOLE_TILE";
        }
        return "EMPTY_TILE"; //Тип EMPTY_TILE возвращается также в случае если в клетке стартовая позиция игрока/врага
    }

    //Метод, присваивающей плитке с координатами (x, y) значение EMPTY_TILE
    void clearTile(int x, int y) {
        int row = y / TILE_SIZE;
        int col = x / TILE_SIZE;
        mMapDataArray[row * TILES_ABR + col] = EMPTY_TILE;
    }

    //Метод, присваивающий плитке с координатами (x, y) значение HOLE_TILE
    void holeTile(int x, int y) {
        int row = y / TILE_SIZE;
        int col = x / TILE_SIZE;
        mMapDataArray[row * TILES_ABR + col] = HOLE_TILE;
    }
}