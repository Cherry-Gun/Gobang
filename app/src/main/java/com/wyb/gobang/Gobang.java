package com.wyb.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//五子棋自定义View
public class Gobang extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;

    private Paint paint = new Paint();

    private Bitmap whitePiece, blackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    private boolean isWhite = true;
    private List<Point> whiteArray = new ArrayList<>();
    private List<Point> blackArray = new ArrayList<>();

    private boolean isGameOver;
    private boolean isWhiteWinner;

    private int MAX_COUNT_IN_LINE = 5;

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
        drawPiece(canvas);
        checkGameOver();
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float LineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (LineHeight / 2);
            int endX = (int) (w - LineHeight / 2);
            int y = (int) ((0.5 + i) * LineHeight);
            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }

    //绘制棋子
    private void drawPiece(Canvas canvas) {
        for (int i = 0, n = whiteArray.size(); i < n; i++) {
            Point whitePoint = whiteArray.get(i);
            canvas.drawBitmap(whitePiece, (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        for (int i = 0, n = blackArray.size(); i < n; i++) {
            Point blackPoint = blackArray.get(i);
            canvas.drawBitmap(blackPiece, (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return false;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point pointPiece = getValidPoint(x, y);
            if (whiteArray.contains(pointPiece) || blackArray.contains(pointPiece)) {
                return false;
            }
            if (isWhite) {
                whiteArray.add(pointPiece);
            } else {
                blackArray.add(pointPiece);
            }
            invalidate();  //重新绘制
            isWhite = !isWhite;
            return true;
        }
        return true;
    }

    //计算用户点击的棋盘点（保证点击的地方只能下一个棋子）
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(whiteArray);
        boolean blackWin = checkFiveInLine(blackArray);
        if (whiteWin || blackWin) {
            isGameOver = true;
            isWhiteWinner = whiteWin;
            String text = isWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);  //横向五连
            if(win) return true;
            win = checkVertical(x, y, points);  //纵向五连
            if(win) return true;
            win = checkLeftDiagonal(x, y, points);  //斜向五连
            if(win) return true;
            win = checkRightDiagonal(x, y, points);  //斜向五连
            if(win) return true;
        }
        return false;
    }

    /**
     * 判断x, y位置的棋子是否有相邻的五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //右上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //右下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;

        return false;
    }

}
