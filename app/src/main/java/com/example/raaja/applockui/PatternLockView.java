package com.example.raaja.applockui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by RAAJA  for SmartFoxItSolutions on 28-08-2016.
 *
 * View which draws a 3X3 matrix of square nodes and handles the pattern drawn from touch events on the nodes.
 * Suitable for pattern locks.
 */
public class PatternLockView extends View {

    private float nodeRectSize,nodeCornerSize;
    private int patternViewDimension;
    private String[] nodeColor,nodeSelectedColor;
    private int defaultColor,defaultSelectedColor;
    private boolean patternRecreated, patternError, patternStarted;
    private Paint nodePaint,nodeSelectedPaint,linePaint;
    private ArrayList<Node> nodeList;
    private OnPatternChangedListener patternListener;
    private Path patternPath;

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
        float nodeRectSizePixel = attributeArray.getDimension(R.styleable.PatternLockView_nodeCellSize,30);
        float nodeCornerSizePixel = attributeArray.getDimension(R.styleable.PatternLockView_nodeCornerSize,5);
        int nodeColorResourceID = attributeArray.getResourceId(R.styleable.PatternLockView_nodeColor,0);
        int nodeSelectedColorResourceID = attributeArray.getResourceId(R.styleable.PatternLockView_nodeSelectedColor,0);
        attributeArray.recycle();
        setNodeCornerSize(nodeCornerSizePixel);
        setNodeRectSize(nodeRectSizePixel);
        if(nodeColorResourceID !=0){
           String[] colorResource =  getResources().getStringArray(nodeColorResourceID);
            setNodeColor(colorResource);
            Log.d("PatternLockView","Color = "+ colorResource[4]);
        }
        if (nodeSelectedColorResourceID !=0){
            String[] colorSelectedResource = getResources().getStringArray(nodeSelectedColorResourceID);
            setNodeSelectedColor(colorSelectedResource);
        }
        setPatternPath();
        setNodePaint();
        setNodeSelectedPaint();
        setLinePaint();
    }

    /** ---------------- Setters And Getters ------------------*/

    public void setNodeRectSize(float nodeRectPixel){
        this.nodeRectSize = nodeRectPixel;
        Log.d("PatternLockView","Node Total Rect in DP "+ nodeRectPixel);
    }

    public void setNodeCornerSize(float nodeCornerPixel){
        this.nodeCornerSize = nodeCornerPixel;
    }

                /* Sets the total dimension of the Pattern View without padding after the measurement
                *  in {@code onMeasure()} method.
                * */
    public void setPatternViewDimension(int measuredPatternDimension){
        this.patternViewDimension = measuredPatternDimension;
    }

    public void setPatternPath(){
        this.patternPath = new Path();
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

                    //Sets a boolean flag to listen for after the pattern is completed. If false touch events are handled.
    public void setPatternError(boolean errorPattern){
        this.patternError = errorPattern;
    }

    public void setPatternStarted(boolean selectedNode){
        this.patternStarted = selectedNode;
    }

    public void setNodeList(ArrayList<Node> nodeList){
        this.nodeList = nodeList;
    }

    public void setOnPatternChangedListener(OnPatternChangedListener patternListener){
        this.patternListener = patternListener;
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

    public float getNodeRectSize(){
        return this.nodeRectSize;
    }

    public  float getNodeCornerSize(){
        return this.nodeCornerSize;
    }

    public int getPatternViewDimension(){
        return this.patternViewDimension;
    }

    public Path getPatternPath(){
        return this.patternPath;
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

    public boolean isPatternStarted(){
        return this.patternStarted;
    }

    public ArrayList<Node> getNodeList(){
        return this.nodeList;
    }

    public OnPatternChangedListener getOnPatternChangedListener(){
        return this.patternListener;
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
     * Listener interface which should be implemented to listen for changes in pattern
     */
    interface OnPatternChangedListener {

        /**
         * Called when a node is selected from the users's touch movement.
         * @param selectedPatternNode The integer for the selected node number
         */
        void onPatternNodeSelected(int selectedPatternNode);

        /**
         * Called when the user's touch movement is completed, notifying that the pattern is finished.
         * @param patternCompleted The boolean value notifying the pattern completion
         */
        void onPatternCompleted(boolean patternCompleted);
    }

    /**
     * Class which contains drawing information of a particular node in the Pattern View
     */

    static class Node{
        RectF nodeTotalRect; /* Rect for the region around the node*/
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
        float nodeTotalRectSize = getNodeRectSize();
        float nodeSize = nodeTotalRectSize * 0.25f; //For 50% of nodeSize inside nodeTotalRect
        float nodeSelectedSize = nodeTotalRectSize * 0.125f; //For 75% of nodeSize inside nodeTotalRect;
        float nodeSpace = (measuredPatternViewRect.width()-(nodeTotalRectSize*3))/2;
        int colorIndex =0;
        String[] nodeColorArray = getNodeColor();
        String[] nodeSelectedColorArray = getNodeSelectedColor();

        for(int i=0;i<3;i++){
            for (int j=0; j<3;j++){
            Node node = new Node();
                float rectLeft = (nodeSpace*j)+(nodeTotalRectSize*j)+measuredPatternViewRect.left;
                float rectRight = rectLeft+nodeTotalRectSize;
                float rectTop = (nodeSpace*i)+(nodeTotalRectSize*i)+measuredPatternViewRect.top;
                float rectBottom = rectTop+nodeTotalRectSize;
                node.nodeTotalRect = new RectF(rectLeft,rectTop,rectRight,rectBottom);

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
        setPatternError(false);
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
                finalSize =  Math.round((getNodeRectSize()*3)+(unspecifiedSpacePixel*2));
                break;
        }
        return finalSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isPatternStarted()){

            Paint selectedRectPaint = getNodeSelectedPaint();
            Paint patternLinePaint = getLinePaint();
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                for(Node node:getNodeList()){
                    if(node.isNodeSelected){
                        selectedRectPaint.setColor(node.nodeSelectedColor);
                        canvas.drawRoundRect(node.nodeSelectedRect,getNodeCornerSize(),getNodeCornerSize(),selectedRectPaint);
                        patternLinePaint.setColor(node.nodeColor);
                    }
                }
                canvas.drawPath(getPatternPath(),getLinePaint());
            }else{
                for(Node node:getNodeList()){
                    if (node.isNodeSelected){
                        selectedRectPaint.setColor(node.nodeSelectedColor);
                        patternLinePaint.setColor(node.nodeColor);
                        canvas.drawRoundRect(node.nodeSelectedLeft,node.nodeSelectedTop,node.nodeSelectedRight
                                            ,node.nodeSelectedBottom,getNodeCornerSize(),getNodeCornerSize(),selectedRectPaint);
                    }
                }
                canvas.drawPath(getPatternPath(),getLinePaint());
            }
        }

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
            setPatternRecreate(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(!isPatternError()){
            return handlePatternGesture(event);
        }
        return false;
    }

    boolean handlePatternGesture(MotionEvent event){
            float pathX = event.getX();
            float pathY = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for(Node node:getNodeList()){
                    if(node.nodeTotalRect.contains(pathX,pathY)){
                        setPatternStarted(true);
                        getPatternPath().moveTo(node.nodeTotalRect.centerX(),node.nodeTotalRect.centerY());
                        getOnPatternChangedListener().onPatternNodeSelected(node.nodeInt);
                        node.isNodeSelected = true;
                        invalidate();
                        //Animate the node
                        return true;
                    }
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                for (Node node:getNodeList()){
                    if(node.nodeTotalRect.contains(pathX,pathY) && !(node.isNodeSelected)){
                        getPatternPath().moveTo(node.nodeTotalRect.centerX(),node.nodeTotalRect.centerY());
                        getOnPatternChangedListener().onPatternNodeSelected(node.nodeInt);
                        node.isNodeSelected = true;
                        invalidate();
                        //Animate the node
                    }else{
                        getPatternPath().lineTo(pathX,pathY);
                        invalidate();
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
                setPatternStarted(false);
                getOnPatternChangedListener().onPatternCompleted(true);
                return false;
        }

        return false;
    }
}
