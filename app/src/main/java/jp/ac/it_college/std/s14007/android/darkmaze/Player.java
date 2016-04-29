package jp.ac.it_college.std.s14007.android.darkmaze;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Player {
    private static final Paint PAINT = new Paint();
    public final Rect rect;
    public final Rect srcRect;
    public Bitmap player;
    private OnMoveListener listener;

    public Player(Bitmap bitmap, int left, int top, float scale) {
        player = bitmap;

//        int right = left + bitmap.getWidth();
//        int bottom = top + bitmap.getHeight();
        int right = left + Math.round(bitmap.getWidth() * scale);
        int bottom = top + Math.round(bitmap.getHeight() * scale);
        rect = new Rect(left, top, right, bottom);

        srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Player(Bitmap bitmap, Map.Block startBlock, float scale) {
        this(bitmap, startBlock.rect.left, startBlock.rect.top, scale);
    }

    public void setOnMoveListener(OnMoveListener moveListener) {
        listener = moveListener;
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(player, srcRect, rect, PAINT);
    }

    void move(int xOffset, int yOffset) {
        int align = yOffset >= 0 ? 1 : -1;
        while (!tryMoveVertical(yOffset)) {
            Log.e("logYOffset :", String.valueOf(yOffset));
            yOffset -= align;
        }
//        tryMoveVertical(yOffset);

        align = xOffset >= 0 ? 1 : -1;
        while (!tryMoveHorizontal(xOffset)) {
            Log.e("logXOffset :", String.valueOf(xOffset));
            xOffset -= align;
        }
//        tryMoveHorizontal(xOffset);
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

    public interface OnMoveListener {
        boolean canMove(int left, int top, int right, int bottom);
    }
}
