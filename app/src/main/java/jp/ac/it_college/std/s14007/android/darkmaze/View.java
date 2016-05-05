package jp.ac.it_college.std.s14007.android.darkmaze;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class View extends SurfaceView implements SurfaceHolder.Callback {

    private static final float BALL_SCALE = 0.8f;
    public DrawThread drawThread;
    boolean isFinished = false;
    private Map map;
    private Bitmap playerBitmap;
    private Bitmap enemyBitmap;
    private FlickTouchListener flickTouchListener = new FlickTouchListener();
    private Player player;
    private Enemy enemy;
    private Callback callback;
    private int dungeonLevel;
    private int seed;
    private int harf;

    public View(Context context, int dungeonLevel) {
        super(context);

        this.dungeonLevel = dungeonLevel;
        getHolder().addCallback(this);
        this.setOnTouchListener(flickTouchListener);

        if (dungeonLevel == 3) {
            playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.extraplayer);
            harf = 2;
        } else {
            playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            harf = 20;
        }
    }

    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();
        callback.timer();

    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }

        isFinished = true;
        drawThread = null;
        return true;
    }

    public void drawMaze(Canvas canvas) {
        int blockSize = 0;
        switch (dungeonLevel) {
            case 0:
                flickTouchListener.move = 5;
                blockSize = 100;
                break;
            case 1:
                flickTouchListener.move = 3;
                blockSize = 50;
                break;
            case 2:
                flickTouchListener.move = 1;
                blockSize = playerBitmap.getHeight();
                break;
            case 3:
                flickTouchListener.move = 1;
                blockSize = playerBitmap.getHeight();
                break;
        }

//        player = flickTouchListener.player;
//        enemy = flickTouchListener.enemy;

        if (map == null) {
            map = new Map(canvas.getWidth(), canvas.getHeight(), blockSize, callback, seed);

        }

        /*int direction = flickTouchListener.direction;
        switch (direction) {
            case 0:
                player.player = BitmapFactory.decodeResource(getResources(), R.drawable.lplayer);
                break;
            case 1:
                player.player = BitmapFactory.decodeResource(getResources(), R.drawable.player);
                break;
            case 2:
                player.player = BitmapFactory.decodeResource(getResources(), R.drawable.rplayer);
                break;
            case 3:
                player.player = BitmapFactory.decodeResource(getResources(), R.drawable.bplayer);
                break;
        }*/


        if (player == null) {
            player = new Player(playerBitmap, map.getStartBlock(), BALL_SCALE);
            player.setOnMoveListener(map);
        }

//        Log.e("Log :",player.rect.toString());

        flickTouchListener.player = player;
        flickTouchListener.enemy = enemy;

        canvas.drawColor(Color.BLACK);
        map.drawMap(canvas);

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);

        if (dungeonLevel != 3) {
            Path path = new Path();
            path.addRect(0, 0, canvas.getWidth(), canvas.getHeight(), Path.Direction.CW);
            path.addCircle(player.rect.left + 24, player.rect.top + 24, 120, Path.Direction.CCW);
            canvas.drawPath(path, paint1);
        }

        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        Paint paintGoal = new Paint();
        paintGoal.setColor(Color.YELLOW);

        canvas.drawRect(map.getStartBlock().rect, paint);
        canvas.drawRect(map.getGoalBlock().rect, paintGoal);

        player.draw(canvas);
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    interface Callback {
        void timer();
        void onGoal();
    }

    public class DrawThread extends Thread {

        @Override
        public void run() {

            while (!isFinished) {
                Canvas canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    drawMaze(canvas);
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
