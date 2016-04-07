package com.wyb.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

//五子棋自定义View
public class Gobang extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;

    private Paint paint = new Paint();

    private Bitmap whitePiece, blackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4 ;

    public Gobang(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        //画笔
        paint.setColor(0x88000000);     //颜色
        paint.setAntiAlias(true);       //抗锯齿
        paint.setDither(true);          //防抖动
        paint.setStyle(Paint.Style.STROKE);  //画笔类型-空心
        //棋子
        whitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        blackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);
    }

    @Override   //测量
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //最小值
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
        //修改棋子尺寸
        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        whitePiece = Bitmap.createScaledBitmap(whitePiece, pieceWidth, pieceWidth, false);
        blackPiece = Bitmap.createScaledBitmap(blackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float LineHeight = mLineHeight;
        for (int i=0; i<MAX_LINE; i++) {
            int startX = (int) (LineHeight / 2);
            int endX = (int) (w - LineHeight / 2);
            int y = (int) ((0.5 + i) * LineHeight);
            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }
}
