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
import zerokaata.hashcode.com.utils.GameManager;
import zerokaata.hashcode.com.utils.Util;
import zerokaata.hashcode.com.utils.ZKConstants;


/**
 * Created by hrawat on 19-05-2017.
 */

public class CellView extends View {

    private Paint paint;
    private Context context;
    private int cellId;
    private static float radii;
    private boolean isClicked;
    private String playerSymbol;
    private GestureDetector mDetector = new GestureDetector(context, new CellView.GestureListener());

    private MoveListener listener;

    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPlayerSymbol();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CellView,
                0, 0);

        try {
            cellId = typedArray.getInt(R.styleable.CellView_cellId, 0);
        } finally {
            typedArray.recycle();
        }

        listener =((ZKApplication) ((HomeActivity) context).getApplication()).getGameManager().getListener();
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

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

        paint.setColor(Color.parseColor("#16A085"));
        canvas.drawRect(rect, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10.0f);

        switch (cellId) {
            case 1:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 2:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                break;
            case 3:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);

            case 4:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 5:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);

                break;
            case 6:
                canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);

                break;
            case 7:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 8:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
                break;
            case 9:
                canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
                canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
                break;
        }
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0);
        radii = centerX > centerY ? centerY : centerX;
        canvas.drawCircle(centerX, centerY, radii - 10, paint);

        if (isClicked) {
            paint.setColor(Color.parseColor("#ab3456"));
            paint.setTextSize(120);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawText(playerSymbol, centerX, centerY + 40, paint);

            setPlayerSymbol();
        }


    }

    private void setPlayerSymbol() {
        int playerType = ((ZKApplication) ((HomeActivity) context).getApplication()).getGameManager().getPlayerType();

        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            playerSymbol = "X";
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            playerSymbol = "O";
        }
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
                if (!isClicked) {
                    //TODO : Move by a player
                    GameManager.GAME_COUNTER ++;
                    isClicked = true;
                    listener.sendMessage(String.valueOf(cellId));
                    invalidate();
                } else {
                    Util.showToast(context, "Already clicked");
                }
                result = true;
            }
        }
        return result;
    }

    public void updateMove(){
        isClicked = true;

        if(playerSymbol.equalsIgnoreCase("x")){
            playerSymbol = "O";
        }else{
            playerSymbol = "X";
        }
        invalidate();

    }

    public void reset(){
        isClicked = false;
        invalidate();
    }

    public interface MoveListener {

        void sendMessage(String msg);

    }
}
