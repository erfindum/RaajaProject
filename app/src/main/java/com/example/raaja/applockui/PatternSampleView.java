package com.example.raaja.applockui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RAAJA on 26-08-2016.
 */
public class PatternSampleView extends View {
    Paint circle;
    Paint pathLine;
    Path line;
    boolean patternStart;
    boolean pathComplete;
    boolean pathJoined;
    Canvas myCanvas;
    Region region1;
    Region region2;
    Rect bound1,bound2;

    float lineX,lineY, prevX,prevY;
    public PatternSampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    void init(){
        circle = new Paint();
        circle.setColor(Color.BLUE);
        circle.setFlags(Paint.ANTI_ALIAS_FLAG);
        circle.setStrokeWidth(20);
        myCanvas = new Canvas();
        pathLine = new Paint();
        pathLine.setColor(Color.GREEN);
        pathLine.setFlags(Paint.ANTI_ALIAS_FLAG);
        pathLine.setStrokeWidth(4);
        pathLine.setStyle(Paint.Style.STROKE);
        line =new Path();
        patternStart = true;
    }

    void setPatternStart(boolean patternStart){
        this.patternStart = patternStart;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(500,500);
    }

    int getCircleHeight(int i){
        return getMeasuredHeight()/i;
    }

    int getCircleWidth(int i){
        return getMeasuredWidth()/i;
    }

    void setRegion(int i){
        int circleWidth = getCircleWidth(2);
        Log.d("PatternLock",getPaddingLeft() + "Left");
        Log.d("PatternLock",getPaddingRight() + "Right");
        if (i==1){
            int circleHeight= getCircleHeight(5);
            region1 = new Region(circleWidth-60,circleHeight-60,circleWidth+60,circleHeight+60);
            bound1 = region1.getBounds();
        }else{
            int circleHeight= getCircleHeight(2);
            region2 = new Region(circleWidth-60,circleHeight-60,circleWidth+60,circleHeight+60);
            bound2 =  region2.getBounds();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(patternStart){
            canvas.drawCircle(getCircleWidth(2),getCircleHeight(5),30,circle);
            setRegion(1);
            canvas.drawCircle(getCircleWidth(2),getCircleHeight(2),30,circle);
            setRegion(2);
            canvas.save();
        }
        if(pathComplete){
            canvas.drawLine(prevX,prevY,lineX,lineY,pathLine);
            if(pathJoined){
                canvas.save();
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX= Math.round(event.getX());
        int rawY = Math.round(event.getY());

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if(region1.contains(rawX,rawY) ){
                    lineX = bound1.centerX();
                    lineY= bound1.centerY();
                    prevX= lineX;
                    prevY = lineY;
                    pathJoined = true;
                    pathComplete = true;
                    Log.d("PAtternLock","Pointer in Region"+lineX  + " " + lineY);
                    invalidate();
                    return true;
                }else if ( region2.contains(rawX,rawY)){
                    lineX = bound2.centerX();
                    lineY = bound2.centerY();
                    prevX= lineX;
                    prevY = lineY;
                    pathJoined = true;
                    pathComplete = true;
                    Log.d("PAtternLock","Pointer in Region" + lineX + " " + lineY);
                    invalidate();
                return true;
                }
            case MotionEvent.ACTION_MOVE:
                if(region1.contains(rawX,rawY) ){
                    lineX = bound1.centerX();
                    lineY= bound1.centerY();
                    prevX= lineX;
                    prevY = lineY;
                    pathJoined = true;
                    pathComplete = true;
                    Log.d("PAtternLock","Pointer in Region"+lineX  + " " + lineY);
                    invalidate();
                }else if ( region2.contains(rawX,rawY)){
                    lineX = bound2.centerX();
                    lineY = bound2.centerY();
                    prevX= lineX;
                    prevY = lineY;
                    pathJoined = true;
                    pathComplete = true;
                    Log.d("PAtternLock","Pointer in Region" + lineX + " " + lineY);
                    invalidate();
                }else{
                    lineX = rawX;
                    lineY = rawY;
                    pathJoined = false;
                    invalidate();
                }


                return true;
            case MotionEvent.ACTION_UP:
                lineX=event.getX();
                lineY=event.getY();
                pathComplete = false;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                return true;
        }

        return super.onTouchEvent(event);
    }




}
