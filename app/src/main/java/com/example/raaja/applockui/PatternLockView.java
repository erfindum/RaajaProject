package com.example.raaja.applockui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by RAAJA  for SmartFoxItSolutions on 28-08-2016.
 *
 * View which draws a 3X3 matrix of square nodes and handles the pattern drawn from touch events on the nodes.
 * Suitable for pattern locks.
 */
public class PatternLockView extends View {

    private int nodeRectSize,nodeCornerSize,patternViewDimension;
    private String[] nodeColor,nodeSelectedColor;
    private int defaultColor,defaultSelectedColor;
    private boolean patternRecreated, patternError, nodeSelected;
    private Paint nodePaint,nodeSelectedPaint,linePaint;
    private ArrayList<Node> nodeList;

    public PatternLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPatternView(context,attrs);
    }

    /**
     * Initializes the PatternLockView from the Context and the AttributeSet parameters.
     *
     * @param context The Context in which the view is running
     * @param attrs The AttributeSet for view's attribute which is set in XML
     */

    private void initPatternView(Context context,AttributeSet attrs){
        TypedArray attributeArray = context.obtainStyledAttributes(attrs,R.styleable.PatternLockView,0,0);
        float nodeRectSizeDp = attributeArray.getDimension(R.styleable.PatternLockView_nodeCellSize,30);
        float nodeCornerSizeDp = attributeArray.getDimension(R.styleable.PatternLockView_nodeCornerSize,5);
        int nodeColorResourceID = attributeArray.getResourceId(R.styleable.PatternLockView_nodeColor,0);
        int nodeSelectedColorResourceID = attributeArray.getResourceId(R.styleable.PatternLockView_nodeSelectedColor,0);
        attributeArray.recycle();
        setNodeCornerSize(nodeCornerSizeDp);
        setNodeRectSize(nodeRectSizeDp);
        if(nodeColorResourceID !=0){
           String[] colorResource =  getResources().getStringArray(nodeColorResourceID);
            setNodeColor(colorResource);
            Log.d("PatternLockView","Color = "+ colorResource[4]);
        }
        if (nodeSelectedColorResourceID !=0){
            String[] colorSelectedResource = getResources().getStringArray(nodeSelectedColorResourceID);
            setNodeSelectedColor(colorSelectedResource);
        }
        setNodePaint();
        setNodeSelectedPaint();
        setLinePaint();
    }

    /** ---------------- Setters And Getters ------------------*/

    public void setNodeRectSize(float nodeRectDp){
        this.nodeRectSize = Math.round(nodeRectDp);
        Log.d("PatternLockView","Node Total Rect in DP "+ nodeRectDp);
    }

    public void setNodeCornerSize(float nodeCornerDp){
        this.nodeCornerSize = Math.round(nodeCornerDp);
    }

                /* Sets the total dimension of the Pattern View without padding after the measurement
                *  in {@code onMeasure()} method.
                * */
    public void setPatternViewDimension(int measuredPatternDimension){
        this.patternViewDimension = measuredPatternDimension;
    }

    public void setNodeColor(String[] colorArray){
        if((colorArray.length ==9)) {
            this.nodeColor = colorArray;
        }else{
            defaultColor = Color.BLUE;
        }
    }

    public void setNodeSelectedColor(String[] colorSelectedArray){
        if((colorSelectedArray.length==9)){
            this.nodeSelectedColor = colorSelectedArray;
        }else{
            defaultSelectedColor = Color.CYAN;
        }
    }

    public void setPatternRecreate(boolean recreatePattern){
        this.patternRecreated = recreatePattern;
    }

    public void setPatternError(boolean errorPattern){
        this.patternError = errorPattern;
    }

    public void setNodeSelected(boolean selectedNode){
        this.nodeSelected = selectedNode;
    }

    public void setNodeList(ArrayList<Node> nodeList){
        this.nodeList = nodeList;
    }

    public void setNodePaint(){
        this.nodePaint = new Paint();
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setNodeSelectedPaint(){
        this.nodeSelectedPaint = new Paint();
        nodeSelectedPaint.setStyle(Paint.Style.FILL);
        nodeSelectedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setLinePaint(){
        this.linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public int getNodeRectSize(){
        return this.nodeRectSize;
    }

    public  int getNodeCornerSize(){
        return this.nodeCornerSize;
    }

    public int getPatternViewDimension(){
        return this.patternViewDimension;
    }

    public String[] getNodeColor(){
        return this.nodeColor;
    }

    public String[] getNodeSelectedColor(){
        return this.nodeSelectedColor;
    }

    public boolean isPatternRecreated(){
        return this.patternRecreated;
    }

    public boolean isPatternError(){
        return this.patternError;
    }

    public boolean isNodeSelected(){
        return this.nodeSelected;
    }

    public ArrayList<Node> getNodeList(){
        return this.nodeList;
    }

    public Paint getNodePaint(){
        return this.nodePaint;
    }

    public Paint getNodeSelectedPaint(){
        return this.nodeSelectedPaint;
    }

    public Paint getLinePaint(){
        return this.linePaint;
    }

    /* ------------ End of Setters And Getters ------------- */

    /**
     * Class which contains drawing information of a particular node in the Pattern View
     */

    static class Node{
        Rect nodeTotalRect; /* Rect for the region around the node*/
        RectF nodeRect,nodeSelectedRect; /* Rect for the node and selected node for pre Lollipop devices*/
        float nodeLeft,nodeRight,nodeTop,nodeBottom; /* Size for the node on post Lollipop devices */
        float nodeSelectedLeft, nodeSelectedRight,nodeSelectedTop,nodeSelectedBottom; /* Size for the selected node on post Lollipop devices */
        int nodeColor;
        int nodeSelectedColor;
        ValueAnimator nodeAnimator;
        int nodeInt; /* Identifier of the node drawn from 1-9. Used to identify a particular node for pattern validation */
        boolean isNodeSelected;
    }

    /**
     * Initializes the nodes with their drawing information for Pattern View
     */

    void initNode(){
        ArrayList<Node> nodeList = new ArrayList<>(9);
        int measuredTotalPatternWidth = getPatternViewDimension()+getPaddingLeft()+getPaddingRight();
        int measuredTotalPatternHeight = getPatternViewDimension()+getPaddingTop()+getPaddingBottom();
        Rect measuredPatternViewRect = new Rect(getPaddingLeft(),getPaddingTop(),measuredTotalPatternWidth-getPaddingRight()
                                                ,measuredTotalPatternHeight-getPaddingBottom());
        int nodeTotalRectSize = getNodeRectSize();
        float nodeSize = nodeTotalRectSize * 0.25f; //For 50% of nodeSize inside nodeTotalRect
        float nodeSelectedSize = nodeTotalRectSize * 0.125f; //For 75% of nodeSize inside nodeTotalRect;
        int nodeSpace = (measuredPatternViewRect.width()-(nodeTotalRectSize*3))/2;
        int colorIndex =0;
        String[] nodeColorArray = getNodeColor();
        String[] nodeSelectedColorArray = getNodeSelectedColor();

        for(int i=0;i<3;i++){
            for (int j=0; j<3;j++){
            Node node = new Node();
                int rectLeft = (nodeSpace*j)+(nodeTotalRectSize*j)+measuredPatternViewRect.left;
                int rectRight = rectLeft+nodeTotalRectSize;
                int rectTop = (nodeSpace*i)+(nodeTotalRectSize*i)+measuredPatternViewRect.top;
                int rectBottom = rectTop+nodeTotalRectSize;
                node.nodeTotalRect = new Rect(rectLeft,rectTop,rectRight,rectBottom);

                node.nodeLeft = node.nodeTotalRect.left + (nodeSize);
                node.nodeRight = node.nodeTotalRect.right - (nodeSize);
                node.nodeTop = node.nodeTotalRect.top + (nodeSize);
                node.nodeBottom = node.nodeTotalRect.bottom - (nodeSize);

                node.nodeSelectedLeft = node.nodeTotalRect.left + nodeSelectedSize;
                node.nodeSelectedRight = node.nodeTotalRect.right - nodeSelectedSize;
                node.nodeSelectedTop = node.nodeTotalRect.top + nodeSelectedSize;
                node.nodeSelectedBottom = node.nodeTotalRect.bottom - nodeSelectedSize;

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    node.nodeRect = new RectF(node.nodeLeft,node.nodeTop,node.nodeRight,node.nodeBottom);
                    node.nodeSelectedRect = new RectF(node.nodeSelectedLeft,node.nodeSelectedTop,node.nodeSelectedRight,node.nodeSelectedBottom);
                }

                if(nodeColorArray.length==9 && nodeSelectedColorArray.length==9){
                    node.nodeColor = Color.parseColor(nodeColorArray[colorIndex]);
                    node.nodeSelectedColor = Color.parseColor(nodeSelectedColorArray[colorIndex]);
                }else{
                    node.nodeColor = defaultColor;
                    node.nodeSelectedColor = defaultSelectedColor;
                }
                node.nodeInt = ++colorIndex;
                nodeList.add(node);
            }
        }
        setNodeList(nodeList);
        resetIsNodeSelected();
        setPatternRecreate(true);
        Log.d("PatternLockView","Called in onMeasure"+ getMeasuredHeight()+"\n"
                + "PatternDimension after measurement " + getPatternViewDimension()+"\n"
                +" Padding left and right : "+getPaddingLeft()+" "+ getPaddingRight()+"\n"
                + "NodeSpace : "+nodeSpace + " NodeSize : "+nodeSize+ " NodeSelectedSize : "+nodeSelectedSize
                + " NodeTotalSize : "+ nodeTotalRectSize);

    }

    /**
     * Resets the selected nodes to the unselected state
     */

    void resetIsNodeSelected(){
        for(Node node:getNodeList()){
            node.isNodeSelected = false;
        }
    }

    /**
     * Resets the PatternView to its original state
     */

    void resetPatternView(){
        resetIsNodeSelected();
        setPatternRecreate(true);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int suggestedWidth = (MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft())- getPaddingRight();
        int suggestedHeight = (MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop())- getPaddingBottom();
        int finalWidth = getFinalPatternViewSize(widthMeasureMode,suggestedWidth);
        int finalHeight = getFinalPatternViewSize(heightMeasureMode,suggestedHeight);

        int measuredSize = Math.min(finalWidth,finalHeight);
        setPatternViewDimension(measuredSize);
        initNode();
        setMeasuredDimension((measuredSize+getPaddingLeft())+getPaddingRight(),(measuredSize+getPaddingTop())+getPaddingBottom());

    }

    /**
     * Returns the final dimension for the PatternLockView from the size suggested in MeasureSpec.
     * Note: The size passed to this method doesn't calculate padding for the view. It should be explicitly handled.
     * @param measureMode The measuring mode from MeasureSpec
     * @param measureSize The suggested size from MeasureSpec
     * @return The final size without padding in int
     */

    int getFinalPatternViewSize(int measureMode, int measureSize){
        int finalSize =0;
        switch(measureMode){
            case MeasureSpec.EXACTLY:
                finalSize = measureSize;
                break;
            case MeasureSpec.AT_MOST:
                finalSize = measureSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                int unspecifiedSpacePixel= Math.round(DimensionConverter.convertDpToPixel(30f,getContext()));
                finalSize =  (getNodeRectSize()*3)+(unspecifiedSpacePixel*2);
                break;
        }
        return finalSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isPatternRecreated()){

            Paint rectPaint = getNodePaint();
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                for(Node node:getNodeList()){
                    rectPaint.setColor(node.nodeColor);
                    canvas.drawRoundRect(node.nodeRect,getNodeCornerSize(),getNodeCornerSize(),rectPaint);
                }
            }else{
                for (Node node :getNodeList()){
                    rectPaint.setColor(node.nodeColor);
                    canvas.drawRoundRect(node.nodeLeft,node.nodeTop,node.nodeRight,node.nodeBottom,getNodeCornerSize(),getNodeCornerSize(),rectPaint);
                    Log.d("PatternLockView","Rect Bounds " + node.nodeTotalRect.width() + " " + node.nodeTotalRect.height());
                }
            }
        }
    }
}
