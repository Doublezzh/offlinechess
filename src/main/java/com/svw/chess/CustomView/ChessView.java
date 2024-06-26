package com.svw.chess.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.svw.chess.ChessMove.Rule;
import com.svw.chess.Info.ChessInfo;
import com.svw.chess.Info.Pos;
import com.svw.chess.R;

import java.util.Iterator;

/**
 * Created by 77304 on 2021/4/5.
 */

public class ChessView extends SurfaceView implements SurfaceHolder.Callback {
    public TutorialThread thread;

    public Paint paint;

    public Bitmap ChessBoard;
    public Bitmap B_box, R_box, Pot;
    public Bitmap[] RP = new Bitmap[7];
    public Bitmap[] BP = new Bitmap[7];

    public Rect cSrcRect, cDesRect;

    public int Board_width, Board_height;

    public ChessInfo chessInfo;


    public Bitmap[] thinkMood = new Bitmap[6];
    public int thinkIndex = 0;
    public String thinkContent = "·····";
    public boolean isRotated = false; // Track rotation state

    public ChessView(Context context, ChessInfo chessInfo) {
        super(context);
        this.chessInfo = chessInfo;
        getHolder().addCallback(this);
        init();

        // 设置SurfaceView背景透明
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public void init() {
        ChessBoard = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);

        B_box = BitmapFactory.decodeResource(getResources(), R.drawable.b_box);
        R_box = BitmapFactory.decodeResource(getResources(), R.drawable.r_box);
        Pot = BitmapFactory.decodeResource(getResources(), R.drawable.pot);

        RP[0] = BitmapFactory.decodeResource(getResources(), R.drawable.r_shuai);
        RP[1] = BitmapFactory.decodeResource(getResources(), R.drawable.r_shi);
        RP[2] = BitmapFactory.decodeResource(getResources(), R.drawable.r_xiang);
        RP[3] = BitmapFactory.decodeResource(getResources(), R.drawable.r_ma);
        RP[4] = BitmapFactory.decodeResource(getResources(), R.drawable.r_ju);
        RP[5] = BitmapFactory.decodeResource(getResources(), R.drawable.r_pao);
        RP[6] = BitmapFactory.decodeResource(getResources(), R.drawable.r_bing);

        BP[0] = BitmapFactory.decodeResource(getResources(), R.drawable.b_jiang);
        BP[1] = BitmapFactory.decodeResource(getResources(), R.drawable.b_shi);
        BP[2] = BitmapFactory.decodeResource(getResources(), R.drawable.b_xiang);
        BP[3] = BitmapFactory.decodeResource(getResources(), R.drawable.b_ma);
        BP[4] = BitmapFactory.decodeResource(getResources(), R.drawable.b_ju);
        BP[5] = BitmapFactory.decodeResource(getResources(), R.drawable.b_pao);
        BP[6] = BitmapFactory.decodeResource(getResources(), R.drawable.b_zu);

        thinkMood[0] = BitmapFactory.decodeResource(getResources(), R.mipmap.wata);


    }

    private int ziWitch = 63;





    public void Draw(Canvas canvas) {
        if (isRotated) {
            canvas.save();
            float centerX = Board_width / 2f;
            float centerY = Board_height / 2f - 48;
            canvas.rotate(-90, centerX, centerY);
        }


        //在原基础上5 为padding
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(ChessBoard, cSrcRect, cDesRect, null);
        Rect pSrcRect, pDesRect;

        for (int i = 0; i < chessInfo.piece.length; i++) {
            for (int j = 0; j < chessInfo.piece[i].length; j++) {
                Pos drawPos = Rule.reversePos(new Pos(j, i), chessInfo.IsReverse);
                if (chessInfo.piece[i][j] > 0) {
                    pDesRect = new Rect(Scale(drawPos.x * 56 - 1), Scale(drawPos.y * 56 + 15), Scale(drawPos.x * 56 - 1 + ziWitch), Scale(drawPos.y * 56 + 15 + ziWitch));
                    if (chessInfo.piece[i][j] <= 7) {
                        int num = chessInfo.piece[i][j] - 1;
                        pSrcRect = new Rect(0, 0, BP[num].getWidth(), BP[num].getHeight());
                        canvas.drawBitmap(BP[num], pSrcRect, pDesRect, null);
                    }
                    if (chessInfo.piece[i][j] >= 8) {
                        int num = chessInfo.piece[i][j] - 8;
                        pSrcRect = new Rect(0, 0, RP[num].getWidth(), RP[num].getHeight());
                        canvas.drawBitmap(RP[num], pSrcRect, pDesRect, null);
                    }
                }
            }
        }

        int drawX = chessInfo.Select[0], drawY = chessInfo.Select[1];
        Pos realPos = Rule.reversePos(new Pos(chessInfo.Select[0], chessInfo.Select[1]), chessInfo.IsReverse);
        int realX = realPos.x, realY = realPos.y;
        if (drawX >= 0 && drawY >= 0 && chessInfo.piece[realY][realX] > 0) {
            if (chessInfo.IsRedGo == true && chessInfo.piece[realY][realX] >= 8) {
                pSrcRect = new Rect(0, 0, R_box.getWidth(), R_box.getHeight());
                pDesRect = new Rect(Scale(drawX * 56 - 1 + 5), Scale(drawY * 56 + 15 + 5), Scale(drawX * 56 - 1 + ziWitch - 5), Scale(drawY * 56 + 15 + ziWitch - 5));


                canvas.drawBitmap(R_box, pSrcRect, pDesRect, null);

                Iterator<Pos> it = chessInfo.ret.iterator();
                while (it.hasNext()) {
                    Pos pos = it.next();
                    pos = Rule.reversePos(pos, chessInfo.IsReverse);
                    int x = pos.x, y = pos.y;
                    pSrcRect = new Rect(0, 0, Pot.getWidth(), Pot.getHeight());
                    pDesRect = new Rect(Scale(x * 56 - 1 + 5), Scale(y * 56 + 15 + 5), Scale(x * 56 - 1 + ziWitch - 5), Scale(y * 56 + 15 + ziWitch - 5));


                    canvas.drawBitmap(Pot, pSrcRect, pDesRect, null);
                }
            } else if (chessInfo.IsRedGo == false && chessInfo.piece[realY][realX] <= 7) {
                pSrcRect = new Rect(0, 0, B_box.getWidth(), B_box.getHeight());
                pDesRect = new Rect(Scale(drawX * 56 - 1 + 5), Scale(drawY * 56 + 15 + 5), Scale(drawX * 56 - 1 + ziWitch - 5), Scale(drawY * 56 + 15 + ziWitch - 5));

                canvas.drawBitmap(B_box, pSrcRect, pDesRect, null);

                Iterator<Pos> it = chessInfo.ret.iterator();
                while (it.hasNext()) {
                    Pos pos = it.next();
                    pos = Rule.reversePos(pos, chessInfo.IsReverse);
                    int x = pos.x, y = pos.y;
                    pSrcRect = new Rect(0, 0, Pot.getWidth(), Pot.getHeight());

                    pDesRect = new Rect(Scale(x * 56 - 1 + 5), Scale(y * 56 + 15 + 5), Scale(x * 56 - 1 + ziWitch - 5), Scale(y * 56 + 15 + ziWitch - 5));

                    canvas.drawBitmap(Pot, pSrcRect, pDesRect, null);
                }
            }
        }

        if (chessInfo.prePos.equals(new Pos(-1, -1)) == false && chessInfo.IsChecked == false) {
            int real_curX = chessInfo.curPos.x;
            int real_curY = chessInfo.curPos.y;

            Pos realPre = Rule.reversePos(chessInfo.prePos, chessInfo.IsReverse);
            Pos realCur = Rule.reversePos(chessInfo.curPos, chessInfo.IsReverse);
            int draw_preX = realPre.x;
            int draw_preY = realPre.y;
            int draw_curX = realCur.x;
            int draw_curY = realCur.y;

            Rect tmpRect;

            pDesRect = new Rect(Scale(draw_curX * 56 - 1 + 5), Scale(draw_curY * 56 + 15 + 5), Scale(draw_curX * 56 - 1 + ziWitch - 5), Scale(draw_curY * 56 + 15 + ziWitch - 5));
            tmpRect = new Rect(Scale(draw_preX * 56 - 1 + 5), Scale(draw_preY * 56 + 15 + 5), Scale(draw_preX * 56 - 1 + ziWitch - 5), Scale(draw_preY * 56 + 15 + ziWitch - 5));


            if (chessInfo.piece[real_curY][real_curX] >= 1 && chessInfo.piece[real_curY][real_curX] <= 7) {
                pSrcRect = new Rect(0, 0, B_box.getWidth(), B_box.getHeight());
                canvas.drawBitmap(B_box, pSrcRect, pDesRect, null);
                canvas.drawBitmap(B_box, pSrcRect, tmpRect, null);
            } else {
                pSrcRect = new Rect(0, 0, R_box.getWidth(), R_box.getHeight());
                canvas.drawBitmap(R_box, pSrcRect, pDesRect, null);
                canvas.drawBitmap(R_box, pSrcRect, tmpRect, null);
            }
        }

        if (chessInfo.status == 1) {
            if (chessInfo.isMachine == true) {
                // 原始图片
                Bitmap originalBitmap = thinkMood[0];
                // 目标新尺寸
                int newWidth = 40;
                int newHeight = 40;

                // 缩放图片
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

                // 计算绘制的中心位置
                int centerX = Board_width / 2;
                int bottomY = Board_height / 2 + newHeight / 2; // 调整为图像底部中心的Y坐标

                // 保存当前画布状态
                canvas.save();

                // 计算摆动角度，使用正弦函数实现摆动效果，幅度控制在±15度之间
                float swingAngle = (float) Math.sin(Math.toRadians(thinkIndex)) * 15; // 15度摆动幅度

                // 将画布移动到图像的底部中心
                canvas.translate(centerX, bottomY);
                // 根据计算出的摆动角度旋转画布
                canvas.rotate(swingAngle);
                // 将画布移回原位
                canvas.translate(-centerX, -bottomY);

                // 绘制缩放后的图片，调整y坐标使图像的底部位于中心线上
                canvas.drawBitmap(scaledBitmap, centerX - newWidth / 2, bottomY - newHeight, null);

                // 恢复画布状态
                canvas.restore();

                // 更新 thinkIndex 以控制摆动速度，这里的值可以根据需要调整
                thinkIndex += 20;
                // 循环摆动效果
                if (thinkIndex > 360) thinkIndex -= 360;

            } else {
                thinkIndex = 0;
                thinkContent = "·····";
            }
        }

        if (isRotated) {
            canvas.restore();
        }

    }

    public int Scale(int x) {
        return x * Board_width / 509;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (isRotated) {
            // 当旋转时，限制视图的最大宽度为原始高度，以适应屏幕
            // 当旋转时，限制视图的最大宽度为原始高度，以适应屏幕
            int width, height;
            // 维持棋盘的纵横比
            float aspectRatio = 1.0f * Board_height / Board_width;
            width = originalHeight;
            height = (int) (width * aspectRatio);

            // 确保旋转后的宽度不超过屏幕高度
            width = Math.min(width, originalHeight);
            // 根据旋转后的尺寸进行调整
            setMeasuredDimension(height, width);
            // 更新Board_width和Board_height以反映新的测量结果
            Board_width = width;
            Board_height = (int) (width * aspectRatio);
        } else {
            float aspectRatio = 509f / 591;
            int width = originalWidth;
            int height = (int) (width / aspectRatio);

            if (height > originalHeight) {
                height = originalHeight;
                width = (int) (height * aspectRatio);
            }
            Board_width = width;
            Board_height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }


        cSrcRect = new Rect(0, 0, ChessBoard.getWidth(), ChessBoard.getHeight());
        cDesRect = new Rect(0, 0, Board_width, Board_height);
        paint = new Paint();
        paint.setTextSize(Scale(57));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
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

    public void toggleBoardRotation(boolean misRotated) {
//isRotated 为false不旋转
        isRotated = !misRotated; // Toggle rotation state
        requestLayout(); // Request a layout pass to adjust size
        invalidate(); // Request redraw
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