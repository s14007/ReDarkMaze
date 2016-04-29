package jp.ac.it_college.std.s14007.android.darkmaze;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

public class Enemy {
    Random random;
    Bitmap enemyBitmap;
    Paint paint = new Paint();
    private OnMoveListener listener;
    private final Rect rect;
    private final Rect srcRect;

    public void setOnMoveListener(OnMoveListener moveListener) {
        listener = moveListener;
    }

    public interface OnMoveListener {
        boolean canMove(int left, int top, int right, int bottom);
    }

    public Enemy(Bitmap bitmap, int left, int top, float scale) {
        enemyBitmap = bitmap;

        int right = left + Math.round(bitmap.getWidth() * scale);
        int bottom = top + Math.round(bitmap.getHeight() * scale);
        rect = new Rect(left, top, right, bottom);

        srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Enemy(Bitmap bitmap, Map.Block startBlock, float scale) {
        this(bitmap, startBlock.rect.left, startBlock.rect.top, scale);
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(enemyBitmap, srcRect, rect, paint);
    }

    void move(int xOffset, int yOffset) {
        int align = yOffset >= 0 ? 1 : -1;
        while (!tryMoveVertical(yOffset)) {
            yOffset -= align;
            Log.e("logY", "" + yOffset);
        }

        align = xOffset >= 0 ? 1 : -1;
        while (!tryMoveHorizontal(xOffset)) {
            xOffset -= align;
        }
    }

    private boolean tryMoveHorizontal(int xOffset) {
        int left = rect.left + xOffset;
        int right = left + rect.width();

        if (!listener.canMove(left, rect.top, right, rect.bottom)) {
            return false;
        }

        rect.left = left;
        rect.right = right;
        return true;
    }

    private boolean tryMoveVertical(int yOffset) {
        int top = rect.top + yOffset;
        int bottom = top + rect.height();

        if (!listener.canMove(rect.left, top, rect.right, bottom)) {
            return false;
        }

        rect.top = top;
        rect.bottom = bottom;
        return true;
    }
}
