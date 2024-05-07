package com.svw.chess.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.svw.chess.Info.ChessInfo;
import com.svw.chess.R;

//自定义回合view

public class RoundView extends SurfaceView implements SurfaceHolder.Callback {
    public TutorialThread thread;

    public ChessInfo chessInfo;

    public Paint paint1;
    public Paint paint2;
    public Paint paint3;
    public RectF rectF;
    public int Round_width = 80, Round_height = 250;
    int borderWidth = 3; // 边框的宽度

    public RoundView(Context context, ChessInfo chessInfo) {
        super(context);
        this.chessInfo = chessInfo;
        getHolder().addCallback(this);
        //init();
    }

    void init() {

        setZOrderOnTop(true); // 使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSPARENT); // 设置surface为透明

        paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);

        paint2 = new Paint();
        paint2.setTextSize(40);
        paint2.setStrokeWidth(1);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.argb(204, 255, 255, 255));

        paint3 = new Paint();
        paint3.setTextSize(40);
        paint3.setStrokeWidth(1);
        paint3.setAntiAlias(true);
        paint3.setColor(Color.argb(204, 255, 255, 255));
        int strokeWidth = 3; // 与bgBorderPaint.setStrokeWidth(5)中的边框宽度相同

        rectF = new RectF(strokeWidth, strokeWidth, Round_width - strokeWidth, Round_height - strokeWidth);
    }

    public void Draw(Canvas canvas) {

        if (canvas == null) return;
        canvas.drawColor(0, PorterDuff.Mode.CLEAR); // 清除画布

        // Set a paint for drawing the background rectangle with transparent fill
        Paint bgFillPaint = new Paint();
        bgFillPaint.setStyle(Paint.Style.FILL);
        bgFillPaint.setColor(Color.TRANSPARENT); // 设置填充颜色为透明

        // Set a paint for drawing the border of the rectangle
        Paint bgBorderPaint = new Paint();
        bgBorderPaint.setStyle(Paint.Style.STROKE);
        bgBorderPaint.setColor(Color.argb(204, 255, 255, 255)); // 边框颜色rgb(105, 125, 101)
        bgBorderPaint.setStrokeWidth(borderWidth); // 边框宽度
        bgBorderPaint.setAntiAlias(true);

        // Draw transparent fill rounded rectangle
        float cornerRadius = 50.0f; // 调整圆角半径
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgFillPaint);

        // Draw border rounded rectangle over the fill
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgBorderPaint);

        // 确定基于回合的文本
        String text = chessInfo.IsRedGo ? "红方回合" : "黑方回合";
        Paint textPaint = chessInfo.IsRedGo ? paint2 : paint3;
//API 26 及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Typeface typeface = getResources().getFont(com.zyn.common.R.font.zyhei);
            textPaint.setTypeface(typeface);
        }

        // Calculate the text height
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float totalTextHeight = (fontMetrics.descent - fontMetrics.ascent) * text.length();

        // Calculate the starting y position to center the text vertically
        float textWidth = textPaint.measureText("回");

        float x = (Round_width - textWidth) / 2;


        float y = (Round_height - totalTextHeight) / 2 - fontMetrics.ascent; // 垂直居中

        // Draw each character of the text, one below the other
        for (char c : text.toCharArray()) {
            canvas.drawText(String.valueOf(c), x, y, textPaint);
            y += fontMetrics.descent - fontMetrics.ascent; // 下一个字符的位置
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = Round_width; // 设置默认宽度
        int desiredHeight = Round_height; // 设置默认高度

        // 调用父类的onMeasure方法获取建议的宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 根据子视图的测量模式调整宽度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            // 如果宽度模式不是EXACTLY（即不是固定大小），则使用默认宽度
            desiredWidth = Math.min(desiredWidth, widthSize);
        }

        // 根据子视图的测量模式调整高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            // 如果高度模式不是EXACTLY（即不是固定大小），则使用默认高度
            desiredHeight = Math.min(desiredHeight, heightSize);
        }
        init();


        // 设置测量后的宽高
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.thread = new TutorialThread(getHolder());
        this.thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    class TutorialThread extends Thread {//刷帧线程
        public int span = 100;//睡眠100毫秒数
        public SurfaceHolder surfaceHolder;

        public TutorialThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() {//重写的方法
            Canvas c;//画布
            while (true) {//循环绘制
                c = this.surfaceHolder.lockCanvas();
                try {
                    Draw(c);//绘制方法
                } catch (Exception e) {
                }
                if (c != null) this.surfaceHolder.unlockCanvasAndPost(c);
                try {
                    Thread.sleep(span);//睡眠时间，单位是毫秒
                } catch (Exception e) {
                    e.printStackTrace();//输出异常堆栈信息
                }
            }
        }
    }
}
