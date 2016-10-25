package com.alex_aladdin.snowball;

import android.util.Log;

//Этот класс отвечает за очередность ходов и ходы противников
//Не объявляем класс как public, поскольку он должен быть видимым только внутри пакета
class Logic {
    //Переменная, отвечающая за то, чья очередь сейчас ходить
    private int mTurn; //0 -- враг0, 1 -- враг1, 2 -- враг2, 3 -- игрок
    //СИСТЕМА КООРДИНАТ: начало в левом верхнем углу; x по горизонтали вправо, y по вертикали вниз
    //Таблица -- схема уровня
    private String mMatrix[][] = new String[Map.TILES_ABR][Map.TILES_ABR];
    //Координаты всех снежков-врагов
    private int mX[] = new int[3];
    private int mY[] = new int[3];
    //Размеры всех снежков-врагов
    private int mSize[] = new int[3];
    //Направления движений снежков-врагов на прошлом ходе
    private String mPrevDirection[] = {"none", "none", "none"};

    private GameView mView;

    //При старте игры
    Logic(GameView view) {
        mView = view;
        mTurn = 3;
        setMoves();
    }

    //Метод, рассчитывающий ходы противников
    private void setMoves() {
        int i, j;
        //Номер текущего снежка
        int s;
        //Выбранное направление движения для текущего снежка
        String direction;

        //Для начала получаем текущие координаты и размер всех снежков-врагов
        for (i = 0; i <= 2; i++) {
            mX[i] = mView.mEnemy[i].getX() / Map.TILE_SIZE;
            mY[i] = mView.mEnemy[i].getY() / Map.TILE_SIZE;
            mSize[i] = mView.mEnemy[i].getSize() / 2; //Размер кратен двум, а мы в расчетах инкрементируем единицей
        }

        //Составляем таблицу - схему уровня
        for (i = 0; i < Map.TILES_ABR; i++)
            for (j = 0; j < Map.TILES_ABR; j++)
                mMatrix[i][j] = mView.mMap.getTileType(i*Map.TILE_SIZE, j*Map.TILE_SIZE);

        //Цикл для каждого снежка-врага
        for (s = 0; s <= 2; s++) {
            direction = "none";
            //Проверяем, жив ли снежок
            if ((mX[s] < 0) && (mY[s] < 0)) continue;
            //Выбираем наилучшее направление движения по приоритетам всех направлений
            if ((priority(s, "down") >= priority(s, "right")) && (priority(s, "down") >= priority(s, "up")) &&
                    (priority(s, "down") >= priority(s, "left"))) direction = "down";
            if ((priority(s, "left") >= priority(s, "down")) && (priority(s, "left") >= priority(s, "right")) &&
                    (priority(s, "left") >= priority(s, "up"))) direction = "left";
            if ((priority(s, "up") >= priority(s, "left")) && (priority(s, "up") >= priority(s, "down")) &&
                    (priority(s, "up") >= priority(s, "right"))) direction = "up";
            if ((priority(s, "right") >= priority(s, "up")) && (priority(s, "right") >= priority(s, "left")) &&
                    (priority(s, "right") >= priority(s, "down"))) direction = "right";

            Log.i("Logic", "s=" + s + " right=" + priority(s, "right"));
            Log.i("Logic", "s=" + s + " up=" + priority(s, "up"));
            Log.i("Logic", "s=" + s + " left=" + priority(s, "left"));
            Log.i("Logic", "s=" + s + " down=" + priority(s, "down"));

            Log.i("Logic", "s=" + s + " x=" + mX[s]);
            Log.i("Logic", "s=" + s + " y=" + mY[s]);
            Log.i("Logic", "s=" + s + " size=" + mSize[s]);

            Log.i("Logic", "////////////////");

            //Задаем снежку это направление
            mView.mEnemy[s].setDirection(direction);
            //Обновляем переменную с предыдущим направлением, которая используется в расчете приоритетов
            mPrevDirection[s] = direction;

            //Смотрим, где окажется снежок после движения в выбранном направлении
            while (true) {
                //Сдвигаемся на один шаг
                switch (direction) {
                    case "right":
                        mX[s]++;
                        break;
                    case "up":
                        mY[s]--;
                        break;
                    case "left":
                        mX[s]--;
                        break;
                    case "down":
                        mY[s]++;
                        break;
                }
                //Если вышли за пределы карты, выходим из цикла
                if ((mX[s] == -1) || (mX[s] == Map.TILES_ABR) || (mY[s] == -1) || (mY[s] == Map.TILES_ABR)) break;
                //Выполняем действие в зависимости от типа клетки
                if (mMatrix[ mX[s] ][ mY[s] ].equals("WALL_TILE")) break;
                if (mMatrix[ mX[s] ][ mY[s] ].equals("SNOWY_TILE")) {
                    mSize[s]++;
                    mMatrix[ mX[s] ][ mY[s] ] = "EMPTY_TILE";
                }
                if (mMatrix[ mX[s] ][ mY[s] ].equals("SPINY_TILE")) {
                    mMatrix[ mX[s] ][ mY[s] ] = "EMPTY_TILE";
                    continue;
                }
                if (mMatrix[ mX[s] ][ mY[s] ].equals("HOLE_TILE")) {
                    mX[s] = -1; mY[s] = -1;
                    continue;
                }
                if (mMatrix[ mX[s] ][ mY[s] ].equals("CRACK_TILE")) mMatrix[ mX[s] ][ mY[s] ] = "HOLE_TILE";
                for (i = 0; i <= 2; i++)
                    if ((mX[s] == mX[i]) && (mY[s] == mY[i]) && (mSize[s] > mSize[i])) mSize[s]++;
            }
            //Сдвигаемся на шаг назад, так как мы заехали на запрещенную клетку
            switch (direction) {
                case "right":
                    mX[s]--;
                    break;
                case "up":
                    mY[s]++;
                    break;
                case "left":
                    mX[s]++;
                    break;
                case "down":
                    mY[s]--;
                    break;
            }
        }
    }

    //Функция, возвращающая приоритет данного направления для данного снежка (снежок номер s)
    private int priority(int s, String direction) {
        int i;
        //Значение, которое мы вернем
        int priority = 0;
        //Локальные (для расчета приоритетов) переменные для всех снежков-врагов
        int x[] = new int[3];
        int y[] = new int[3];
        int size[] = new int[3];

        //Для начала дублируем текущие координаты снежков-врагов в локальные переменные
        for (i = 0; i <= 2; i++) {
            x[i] = mX[i];
            y[i] = mY[i];
            size[i] = mSize[i];
        }

        switch (direction) {
            case "right":
                x[s]++;
                //Следующая клетка за пределами карты
                if (x[s] == Map.TILES_ABR) return(-200);
                //Следующая клетка -- стена
                if (mMatrix[ x[s] ][ y[s] ].equals("WALL_TILE")) return(-200);
                //На следующей клетке стоит снежок такого же или большего размера
                for (i = 0; i <= 2; i++)
                    if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i])) return(-200);
                //Отнимаем единицу приоритета, если вернемся туда откуда ушли на прошлом ходе
                if (mPrevDirection[s].equals("left")) priority--;

                while (x[s] < Map.TILES_ABR) {
                    switch (mMatrix[ x[s] ][ y[s] ]) {
                        case "SNOWY_TILE":
                            size[s]++;
                            priority += 2;
                            break;
                        case "HOLE_TILE":
                            priority -= 20;
                            return priority;
                        case "WALL_TILE":
                            return priority;
                    }
                    //Попадаем на клетку с другим снежком
                    for (i = 0; i <= 2; i++) {
                        if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i]))
                            return priority;
                        if ((x[s] == x[i]) && (y[s] == y[i]) && (size[s] > size[i])) {
                            size[s]++;
                            priority += 2;
                        }
                    }

                    x[s]++;
                }
                break;
            case "up":
                y[s]--;
                //Следующая клетка за пределами карты
                if (y[s] == -1) return(-200);
                //Следующая клетка -- стена
                if (mMatrix[ x[s] ][ y[s] ].equals("WALL_TILE")) return(-200);
                //На следующей клетке стоит снежок такого же или большего размера
                for (i = 0; i <= 2; i++)
                    if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i])) return(-200);
                //Отнимаем единицу приоритета, если вернемся туда откуда ушли на прошлом ходе
                if (mPrevDirection[s].equals("down")) priority--;

                while (y[s] >= 0) {
                    switch (mMatrix[ x[s] ][ y[s] ]) {
                        case "SNOWY_TILE":
                            size[s]++;
                            priority += 2;
                            break;
                        case "HOLE_TILE":
                            priority -= 20;
                            return priority;
                        case "WALL_TILE":
                            return priority;
                    }
                    //Попадаем на клетку с другим снежком
                    for (i = 0; i <= 2; i++) {
                        if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i]))
                            return priority;
                        if ((x[s] == x[i]) && (y[s] == y[i]) && (size[s] > size[i])) {
                            size[s]++;
                            priority += 2;
                        }
                    }

                    y[s]--;
                }
                break;
            case "left":
                x[s]--;
                //Следующая клетка за пределами карты
                if (x[s] == -1) return(-200);
                //Следующая клетка -- стена
                if (mMatrix[ x[s] ][ y[s] ].equals("WALL_TILE")) return(-200);
                //На следующей клетке стоит снежок такого же или большего размера
                for (i = 0; i <= 2; i++)
                    if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i])) return(-200);
                //Отнимаем единицу приоритета, если вернемся туда откуда ушли на прошлом ходе
                if (mPrevDirection[s].equals("right")) priority--;

                while (x[s] >= 0) {
                    switch (mMatrix[ x[s] ][ y[s] ]) {
                        case "SNOWY_TILE":
                            size[s]++;
                            priority += 2;
                            break;
                        case "HOLE_TILE":
                            priority -= 20;
                            return priority;
                        case "WALL_TILE":
                            return priority;
                    }
                    //Попадаем на клетку с другим снежком
                    for (i = 0; i <= 2; i++) {
                        if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i]))
                            return priority;
                        if ((x[s] == x[i]) && (y[s] == y[i]) && (size[s] > size[i])) {
                            size[s]++;
                            priority += 2;
                        }
                    }

                    x[s]--;
                }
                break;
            case "down":
                y[s]++;
                //Следующая клетка за пределами карты
                if (y[s] == Map.TILES_ABR) return(-200);
                //Следующая клетка -- стена
                if (mMatrix[ x[s] ][ y[s] ].equals("WALL_TILE")) return(-200);
                //На следующей клетке стоит снежок такого же или большего размера
                for (i = 0; i <= 2; i++)
                    if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i])) return(-200);
                //Отнимаем единицу приоритета, если вернемся туда откуда ушли на прошлом ходе
                if (mPrevDirection[s].equals("up")) priority--;

                while (y[s] < Map.TILES_ABR) {
                    switch (mMatrix[ x[s] ][ y[s] ]) {
                        case "SNOWY_TILE":
                            size[s]++;
                            priority += 2;
                            break;
                        case "HOLE_TILE":
                            priority -= 20;
                            return priority;
                        case "WALL_TILE":
                            return priority;
                    }
                    //Попадаем на клетку с другим снежком
                    for (i = 0; i <= 2; i++) {
                        if ((s != i) && (x[s] == x[i]) && (y[s] == y[i]) && (size[s] <= size[i]))
                            return priority;
                        if ((x[s] == x[i]) && (y[s] == y[i]) && (size[s] > size[i])) {
                            size[s]++;
                            priority += 2;
                        }
                    }

                    y[s]++;
                }
                break;
        }
        return priority;
    }

    //Метод, возвращающий текущее значение очереди
    int getTurn() {
        return mTurn;
    }

    //Метод, задающий напрямую значение очереди (используется при обработке касаний, если игроком задано невозможное направление)
    void setTurn(int turn) { mTurn = turn; }

    //Переход на следующего в очереди
    void nextTurn() {
        //Проверка на проигрыш
        if (!mView.mPlayer.isAlive()){
            mView.lose();
            return;
        }
        //Проверка на выигрыш
        if (!(mView.mEnemy[0].isAlive() || mView.mEnemy[1].isAlive() || mView.mEnemy[2].isAlive())) {
            mView.win();
            return;
        }
        //Если всё как обычно, передаем ход следующему
        switch (mTurn) {
            case 0: //враг0
                if (mView.mEnemy[1].isAlive()) {
                    mTurn = 1;
                }
                else if (mView.mEnemy[2].isAlive()) {
                    mTurn = 2;
                }
                else {
                    mTurn = 3;
                    setMoves();
                }
                break;
            case 1: //враг1
                if (mView.mEnemy[2].isAlive()) {
                    mTurn = 2;
                }
                else {
                    mTurn = 3;
                    setMoves();
                }
                break;
            case 2: //враг2
                mTurn = 3;
                setMoves();
                break;
            case 3: //игрок
                if (mView.mEnemy[0].isAlive()) {
                    mTurn = 0;
                }
                else if (mView.mEnemy[1].isAlive()) {
                    mTurn = 1;
                }
                else {
                    mTurn = 2;
                }
                break;
        }
    }
}