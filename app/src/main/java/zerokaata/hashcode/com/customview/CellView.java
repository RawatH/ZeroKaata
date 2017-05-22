package zerokaata.hashcode.com.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.application.ZKApplication;
import zerokaata.hashcode.com.ui.HomeActivity;
import zerokaata.hashcode.com.utils.Util;
import zerokaata.hashcode.com.utils.ZKConstants;


/**
 * Created by hrawat on 19-05-2017.
 */

public class CellView extends View {

    private Paint paint;
    private Context context;
    private int rowId;
    private int colId;
    private int position;
    private static float radii;
    private boolean isUserClick;
    private boolean winFlag;
    private String playerSymbol;
    private GestureDetector mDetector = new GestureDetector(context, new CellView.GestureListener());

    private GameListener gameListener;
    private int playerType;

    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPlayerSymbol();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CellView,
                0, 0);

        try {
            position = typedArray.getInt(R.styleable.CellView_position, 0);
            rowId = typedArray.getInt(R.styleable.CellView_rowId, 0);
            colId = typedArray.getInt(R.styleable.CellView_colId, 0);
        } finally {
            typedArray.recycle();
        }

        gameListener = ((ZKApplication) ((HomeActivity) context).getApplication()).getGameManager().getGameListener();
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        playerType = ((ZKApplication) ((HomeActivity) context).getApplication()).getGameManager().getPlayerType();
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "game_font.ttf");
        paint.setTypeface(plain);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = canvas.getClipBounds();
        int canvasWidth = rect.width();
        int canvasHeight = rect.height();
        float centerX = canvasWidth / 2;
        float centerY = canvasHeight / 2;

        if(winFlag) {
            paint.setColor(winFlag ? Color.RED : Color.parseColor("#16A085"));
            canvas.drawRect(rect, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10.0f);

        switch (position) {
            case 0:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 1:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                break;
            case 2:

                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);

            case 3:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 4:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);

                break;
            case 5:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);

                break;
            case 6:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 7:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 8:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                break;
        }
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0);
        radii = centerX > centerY ? centerY : centerX;
        canvas.drawCircle(centerX, centerY, radii - 10, paint);

        if (isUserClick) {
            // o , x color
            paint.setColor(Color.parseColor("#ab3456"));
            paint.setTextSize(120);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawText(playerSymbol, centerX, centerY + 40, paint);

            setPlayerSymbol();
        }


    }

    private void setPlayerSymbol() {

        playerSymbol = ((ZKApplication) ((HomeActivity) context).getApplication()).getGameManager().getPlayerSymbol();
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isUserClick) {
                    //TODO : Move by a player . Use GameListener
                    isUserClick = true;
                    gameListener.updateMove(position, rowId, colId, false);
                    invalidate();
                } else {
                    Util.showToast(context, "Already clicked");
                }
                result = true;
            }
        }
        return result;
    }

    public void updateMove() {
        isUserClick = true;

        if (playerSymbol.equalsIgnoreCase(ZKConstants.MARK_X)) {
            playerSymbol = ZKConstants.MARK_O;
        } else {
            playerSymbol = ZKConstants.MARK_X;
        }
        invalidate();

    }

    public void drawWin() {
        winFlag = true;
        invalidate();
    }

    public void reset() {
        isUserClick = false;
        invalidate();
    }


    public interface GameListener {
        void updateMove(int position, int row, int col, boolean isOpponentsMove);
    }
}
