package com.ckw.lightweightmusicplayer.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.ckw.lightweightmusicplayer.ui.MainActivity;



public class CustomLinearGradient extends View {

    Paint paint;
    int startColor, midColor, endColor;
    int alpha;

    public CustomLinearGradient(Context context) {
        super(context);
        init();
    }

    public CustomLinearGradient(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomLinearGradient(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        paint = new Paint();
        alpha = 140;
        startColor = Color.argb(alpha, Color.red(MainActivity.themeColor), Color.green(MainActivity.themeColor), Color.blue(MainActivity.themeColor));
        midColor = Color.parseColor("#88111111");
        endColor = Color.parseColor("#FF111111");
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setStartColor(int color) {
        startColor = color;
    }

    void setEndColor(int color) {
        endColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startColor = Color.argb(alpha, Color.red(MainActivity.themeColor), Color.green(MainActivity.themeColor), Color.blue(MainActivity.themeColor));
        midColor = Color.parseColor("#88111111");
        endColor = Color.parseColor("#FF111111");
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(), startColor, endColor, Shader.TileMode.CLAMP));
        canvas.drawPaint(paint);
    }
}
