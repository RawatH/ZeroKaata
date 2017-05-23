package zerokaata.hashcode.com.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    private static final String TAG = CellView.class.getSimpleName();
    private GameListener gameListener;
    private int playerType;
    private int winArr[];

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

        if (winFlag && isWinCell()) {
            paint.setColor(Color.RED);
            switch (getWinLineType()) {
                case WINLINE.HORIZONTAL:
                    switch (position) {
                        case 0:
                        case 3:
                        case 6:
                            canvas.drawLine(centerX, centerY, rect.right, rect.bottom / 2, paint);
                            break;
                        case 1:
                        case 4:
                        case 7:
                            canvas.drawLine(rect.left, rect.bottom / 2, rect.right, rect.bottom / 2, paint);
                            break;
                        case 2:
                        case 5:
                        case 8:
                            canvas.drawLine(rect.left, rect.bottom / 2, centerX, centerY, paint);
                            break;
                    }
                    break;
                case WINLINE.VERTICAL:
                    switch (position) {
                        case 0:
                        case 1:
                        case 2:
                            canvas.drawLine(centerX, centerY, rect.bottom, rect.right / 2, paint);
                            break;
                        case 3:
                        case 4:
                        case 5:
                            canvas.drawLine(rect.top, rect.right / 2, rect.bottom, rect.right / 2, paint);
                            break;
                        case 6:
                        case 7:
                        case 8:
                            canvas.drawLine(rect.top, rect.right / 2, centerX, centerY, paint);
                            break;
                    }
                    break;

                case WINLINE.DIAGONAL:
                    switch (position) {
                        case 0:
                            canvas.drawLine(centerX, centerY, rect.bottom, rect.right, paint);
                            break;
                        case 2:
                            canvas.drawLine(centerX, centerY, rect.left, rect.bottom, paint);
                            break;
                        case 4:
                            canvas.drawLine(rect.top, rect.left, rect.bottom, rect.right, paint);
                            break;
                        case 6:
                            canvas.drawLine(centerX, centerY, rect.top, rect.right, paint);
                            break;
                        case 8:
                            canvas.drawLine(rect.top, rect.left, rect.right, rect.bottom, paint);
                            break;
                    }
                    break;
            }

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
                break;
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
//        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0);
//        radii = centerX > centerY ? centerY : centerX;
//        canvas.drawCircle(centerX, centerY, radii - 10, paint);

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
                if (!isUserClick && gameListener.isMyTunNow()) {
                    //TODO : Move by a player . Use GameListener
                    isUserClick = true;
                    gameListener.updateMove(position, rowId, colId, false);
                    invalidate();
                } else {
                    Util.showToast(context, "Already clicked or Not ur turn now.");
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

    private boolean isWinCell() {
        boolean flag = false;
        for (int i : winArr) {
            if (position == i) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private int getWinLineType() {
        switch (winArr[1] - winArr[0]) {
            case 1:
                return WINLINE.HORIZONTAL;
            case 3:
                return WINLINE.VERTICAL;
            default:
                return WINLINE.DIAGONAL;
        }
    }

    public void drawWin() {
        winFlag = true;
        winArr = gameListener.getWinArr();
        invalidate();
    }

    public void reset() {
        isUserClick = false;
        winFlag = false;
        winArr = null;
        invalidate();
    }


    public interface GameListener {
        void updateMove(int position, int row, int col, boolean isOpponentsMove);

        boolean isMyTunNow();

        int[] getWinArr();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WINLINE.HORIZONTAL, WINLINE.VERTICAL, WINLINE.DIAGONAL})
    public @interface WINLINE {
        int HORIZONTAL = 1;
        int VERTICAL = 3;
        int DIAGONAL = 2;
    }
}
