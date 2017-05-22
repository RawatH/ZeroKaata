package zerokaata.hashcode.com.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.utils.Util;

/**
 * Created by hrawat on 16-05-2017.
 */

public class IndicatorView extends View {

    private Context context;
    private static float radii;
    private Paint paint;
    private static final float MARGIN = 10f;
    private int bgColor;
    private String name;
    private GestureDetector mDetector = new GestureDetector(context, new GestureListener());
    private Handler handler;
    private boolean isServer = false;
    private PlayModeSelectionListener listener;
    private boolean showDiscoveryTime = false;
    private int timeLeft ;

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setShowDiscoveryTime(boolean showDiscoveryTime) {
        this.showDiscoveryTime = showDiscoveryTime;
    }

    public void setListener(PlayModeSelectionListener listener) {
        this.listener = listener;
    }

    public static final String TAG = "indicatorview";

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.handler = new Handler();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.IndicatorView,
                0, 0);

        try {
            bgColor = typedArray.getInteger(R.styleable.IndicatorView_bgColor, 0);
            name = typedArray.getString(R.styleable.IndicatorView_name);
        } finally {
            typedArray.recycle();
        }

        init();
        setWillNotDraw(false);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        Typeface plain = Typeface.createFromAsset(context.getAssets(), "game_font.ttf");

        paint.setTypeface(plain);

        setPaintColorAndStyleToOrignal();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = canvas.getClipBounds();
        int canvasWidth = rect.width();
        int canvasHeight = rect.height();
        float centerX = canvasWidth / 2;
        float centerY = canvasHeight / 2;

        radii = centerX > centerY ? centerY : centerX;
        canvas.drawCircle(centerX, centerY, radii - MARGIN, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(120);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setStyle(Paint.Style.STROKE);

        canvas.drawText(showDiscoveryTime ? String.valueOf(getTimeLeft()):name, centerX, centerY + (MARGIN * 2), paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10.0f);
        canvas.drawCircle(centerX, centerY, radii - MARGIN - 15, paint);

        setPaintColorAndStyleToOrignal();


        if (showClickedAnimation) {
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(10.0f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, animCircleRAdius, paint);
            if(isServer){
                animCircleRAdius -= 10;
                if (animCircleRAdius < 10) {
                    animCircleRAdius = radii - MARGIN - 15;
                }
            }else {
                animCircleRAdius += 10;
                if (animCircleRAdius > radii - MARGIN - 10) {
                    animCircleRAdius = 10.0f;
                }
            }
        }
        setPaintColorAndStyleToOrignal();

    }

    private void setPaintColorAndStyleToOrignal() {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(bgColor == 0 ? Color.parseColor("#ff9f6a") : bgColor);
    }

    private boolean showClickedAnimation = false;
    private float animCircleRAdius;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!showClickedAnimation) {
                    //TODO : START BT CONNECTION

                    showClickedAnimation = true;
                    isServer = getId() == R.id.bserver ? true : false;
                    animCircleRAdius = isServer ?radii - MARGIN - 15 : 10 ;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showAnimation(getId());
                        }
                    }).start();
                } else {
                    Util.showToast(context, "Already clicked");
                }
                result = true;
            }
        }
        return result;
    }

    Runnable animation = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "invalidate()");
            invalidate();
        }
    };

    private void showAnimation(int id) {

        handler.removeCallbacks(animation);
        if (listener != null) {
            listener.onPlayModeSelected(id);
        }
        while (showClickedAnimation) {
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(animation);
        }

    }


    public void reset(){
        handler.removeCallbacks(animation);
        showClickedAnimation = false;
    }

    public interface PlayModeSelectionListener {
        void onPlayModeSelected(int playMode);
    }




}

