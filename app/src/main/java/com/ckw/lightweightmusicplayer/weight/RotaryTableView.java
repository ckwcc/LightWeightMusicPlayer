package com.ckw.lightweightmusicplayer.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.ckw.lightweightmusicplayer.R;

import java.util.List;

/**
 * Created by ckw
 * on 2018/3/7.
 * 转盘view
 */

public class RotaryTableView extends View {

    private static final int TYPE_UNCHECKED = 0;
    private static final int TYPE_CHECKED = 1;

    /*
    * 控件的宽度（高度）
    * */
    private int mDrawWidth;

    /*
    * 避免重复计算宽度（高度）
    * */
    private boolean hasMeasured = false;

    /*
    * 被选中的position，默认是第0个位置
    * */
    private int checkPosition = -1;

    /**
     * 绘画区域的图片。这边会将各个小图片拼接成一张图片
     * 相当于是整个view变成一张图片
     **/
    private Bitmap fontBitmap;

    /*
    * 数据源
    * */
    private List<RotaryTableInfo> bitInfos;

    /**
     * 选项的个数
     **/
    private int mItemCount;

    /**
     * 保存的画布
     **/
    private Canvas mCanvas;

    /**
     * 非选中和选中的颜色色块
     */
    private int[] mColors;

    /**
     * 触摸点的坐标位置
     **/
    private float touchX;
    private float touchY;

    /*
    * 非点击模式下，初始的触摸点
    * */
    private float originX;
    private float originY;

    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();

    /**
     * 绘制盘块的画笔
     * （被划分出来的单独区域）
     */
    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private TextPaint mTextPaint;

    /**
     * 绘制分割线的画笔
     */
    private Paint mLinePaint;

    /**
     * 圆形背景的画笔
     **/
    private Paint mBackgroundPaint;

    /**
     * 圆盘角度
     **/
    private volatile float mStartAngle = 0;


    /**
     * 控件的中心位置,处于中心位置。x和y是相等的
     */
    private int mCenter;

    /**
     * 圆形背景的宽度，这边是在1080p下的780
     **/
    private float mBackgroundWidth = 780;

    /**
     * 内圈画盘小圆的宽度，这边是在1080p下的730，绘画区域
     * （比背景略小）
     **/
    private float mRangeWidth = 730;

    /**
     * 里面的小图大小，这边是1080p下的115
     * 每个item的图片的大小
     **/
    private float mLitterBitWidth = 115;

    /**
     * 文字的大小为26px
     */
    private float mTextSize = 26;

    /**
     * 500毫秒内的点击被认为是点击事件
     **/
    private long timeClick = 500;

    /**
     * 保存点击的时间
     **/
    private long nowClick;

    /**
     * 中心图片信息
     **/
    private Bitmap mCheckBitmap;
    private float mCheckBitmapWidth = 270;//1080p下的270
    private RectF mCheckBitmapRect = new RectF();//图片绘制区域

    /////////////////////////////////////////////////////////////////////////////////////
    /*
    * 对外提供的属性（xml中） 圆盘背景 item未被选中的颜色 item选中的颜色 分割线颜色 中心图片 是否能点击
    * */
    private int mBackgroundColor;
    private int mItemSelectedColor;
    private int mItemUnSelectedColor;
    private int mSplitLineColor;
    private int mCenterBitmapSrc;
    private boolean mCanClick;

    /////////////////////////////////////////////////////////////////////////////////////

    private OnWheelCheckListener mOnWheelCheckListener;

    public void setOnWheelCheckListener(OnWheelCheckListener onWheelCheckListener) {
        this.mOnWheelCheckListener = onWheelCheckListener;
    }

    private OnCenterBitmapClickListener mOnCenterBitmapClickListener;

    public void setOnCenterBitmapClickListener(OnCenterBitmapClickListener onCenterBitmapClickListener){
        this.mOnCenterBitmapClickListener = onCenterBitmapClickListener;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    public RotaryTableView(Context context) {
        this(context,null);
    }

    public RotaryTableView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RotaryTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RotaryTableView);
        mBackgroundColor = typedArray.getColor(R.styleable.RotaryTableView_backgroundColor,Color.YELLOW);
        mSplitLineColor = typedArray.getColor(R.styleable.RotaryTableView_splitLineColor,Color.WHITE);
        mCanClick = typedArray.getBoolean(R.styleable.RotaryTableView_canClick,true);
        if(!mCanClick){//如果不能点击，不会有选中的效果
            mItemUnSelectedColor = typedArray.getColor(R.styleable.RotaryTableView_itemUnSelectedColor,Color.DKGRAY);
            mItemSelectedColor = mItemUnSelectedColor;
        }else {
            mItemSelectedColor = typedArray.getColor(R.styleable.RotaryTableView_itemSelectedColor,Color.DKGRAY);
            mItemUnSelectedColor = typedArray.getColor(R.styleable.RotaryTableView_itemUnSelectedColor,Color.GRAY);
        }

        mCenterBitmapSrc = typedArray.getResourceId(R.styleable.RotaryTableView_centerBitmapSrc,R.mipmap.ic_launcher_round);

        mColors = new int[]{mItemUnSelectedColor, mItemSelectedColor};
        mCheckBitmap = BitmapFactory.decodeResource(getResources(), mCenterBitmapSrc);
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //这边拿到的是px
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mCenter = width / 2;
        mDrawWidth = width;
        //控件整体为正方形
        setMeasuredDimension(width,width);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        drawCanvas();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //判断按点是否在圆内,如果没有在自己的范围内，则返回false，事件由父控件消费
        if (!mRange.contains(event.getX(), event.getY())) {
            return false;
        }

        //当滑动在自己的范围内时，阻止父控件拦截事件，将事件交由自己处理
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                originX = event.getX();
                originY = event.getY();
                //按下时的时间
                nowClick = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //得到旋转的角度
                float arc = getRoundArc(touchX, touchY, moveX, moveY);
                //重新赋值
                touchX = moveX;
                touchY = moveY;
                //起始角度变化下，然后进行重新绘制
                mStartAngle += arc;
                onDrawInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(mCanClick){
                    if (System.currentTimeMillis() - nowClick <= timeClick) {//此时为点击事件
                        //落点的角度加上偏移量基于初始的点的位置

                        checkPosition = calInExactArea(getRoundArc(event.getX(), event.getY()) - mStartAngle);
                        onDrawInvalidate();
                        if (mOnWheelCheckListener != null) {
                            mOnWheelCheckListener.onCheck(checkPosition);
                        }
                    }
                }else {
                    int currentZeroIndex = getZeroPositionByCoordinateSystem(mStartAngle);
                    onDrawInvalidate();
                    if(mOnCenterBitmapClickListener != null){
                        mOnCenterBitmapClickListener.onCenterClick(currentZeroIndex);
                    }

                }

                break;
        }
        return true;
    }

    private void onDrawInvalidate() {
        invalidate();
    }

    private void drawCanvas() {
        if(bitInfos == null || bitInfos.size() == 0){
            return;
        }
        //绘制背景图
        mCanvas.drawCircle(mCenter,mCenter,mBackgroundWidth / 2,mBackgroundPaint);
        //绘制前景图片（比背景图略小的）
        mCanvas.drawBitmap(getFontBitmap(),0,0,null);
        //绘制中心图片
        mCanvas.drawBitmap(mCheckBitmap, null, mCheckBitmapRect, null);

    }

    /*
    * 绘制前景图片,这里包含的是图片信息和文字信息
    * */
    private Bitmap getFontBitmap() {
        fontBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(fontBitmap);
        //根据角度进行同步旋转(感觉这行代码没必要)
        canvas.rotate(mStartAngle,mCenter,mCenter);

        float tmpAngle = 0;
        float sweepAngle = (360/mItemCount);//需要旋转的角度
        for (int i = 0; i < mItemCount; i++) {
            canvas.drawBitmap(getDrawItemBitmap(tmpAngle,sweepAngle,i),0,0,null);
            tmpAngle += sweepAngle;
        }
        return fontBitmap;
    }

    /*
    * 绘制单个的item
    * */
    private Bitmap getDrawItemBitmap(float tmpAngle, float sweepAngle, int position) {
        //是否需要重新绘制
        boolean needReDraw = false;
        if(checkPosition == position && bitInfos.get(position).info.bitmapType == TYPE_UNCHECKED){
            //这次被选中了，但是上一次没有被选中，需要重新绘制
            needReDraw = true;
            bitInfos.get(position).info.bitmapType = TYPE_CHECKED;
        }else if(checkPosition != position && bitInfos.get(position).info.bitmapType == TYPE_CHECKED){
            //上次被选中，这次没有
            needReDraw = true;
            bitInfos.get(position).info.bitmapType = TYPE_UNCHECKED;
        }

        if(bitInfos.get(position).info.itemBitmap == null || needReDraw){
            //选择背景颜色
            if(checkPosition == position){
                mArcPaint.setColor(mColors[1]);
            }else {
                mArcPaint.setColor(mColors[0]);
            }

            //绘制每一个小块
            bitInfos.get(position).info.itemBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            Canvas itemCanvas = new Canvas(bitInfos.get(position).info.itemBitmap);
            //根据角度进行同步旋转
            itemCanvas.rotate(tmpAngle,mCenter,mCenter);
            //绘制背景颜色,从最上边开始画
            itemCanvas.drawArc(mRange, -sweepAngle / 2 - 90, sweepAngle, true, mArcPaint);
            //绘制小图片和文本，因为一起画好画点
            drawIconAndText(position, itemCanvas);
            //绘制分割线，这里保证没一个小块都有一条分割线，分割线的位置是在最右侧
            drawCanvasLine(itemCanvas);
        }else {//不需要重新绘制
            Canvas itemCanvas = new Canvas(bitInfos.get(position).info.itemBitmap);
            //根据角度进行同步旋转
            itemCanvas.rotate(tmpAngle, mCenter, mCenter);
        }
        return bitInfos.get(position).info.itemBitmap;
    }

    /*
    * 绘制item里面的小图片和文字
    * */
    private void drawIconAndText(int position, Canvas canvas) {
        //根据标注，比例为115/730，小图片宽度/内圈宽度
        float rt = mLitterBitWidth / mRangeWidth;
        //计算绘画区域的直径
        int mRadius = (int) (mRange.right - mRange.left);
        int imgWidth = (int) (mRadius * rt);

        //获取中心点坐标
        int x = mCenter;
        //这边让图片从半径的四分之一处开始画
        int y = (int) (mCenter - mRadius / 2 + (float) mRadius / 2 * 1 / 4f);
        //确定小图片的区域
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);
        //将图片画上去
        canvas.drawBitmap(bitInfos.get(position).bitmap, null, rect, null);
        //绘制文本
        if (!TextUtils.isEmpty(bitInfos.get(position).text)) {
            //最大字数限制为8个字
            if (bitInfos.get(position).text.length() > 8) {
                bitInfos.get(position).text = bitInfos.get(position).text.substring(0, 8);
            }
            StaticLayout textLayout = new StaticLayout(bitInfos.get(position).text, mTextPaint, imgWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
            canvas.translate(mCenter, rect.bottom + dip2px(2));
            textLayout.draw(canvas);
            //画完之后移动回来
            canvas.translate(-mCenter, -(rect.bottom + dip2px(2)));
        }
    }

    /*
    * 绘制分割线
    * */
    private void drawCanvasLine(Canvas canvas){
        if (mItemCount == 1) {
            return;
        }
        //计算终点角度，这边从最上边为0度开始计算,则角度在0-180之间，给0.5f的偏移
        float range = 360f / mItemCount / 2 - 0.5f;
        //计算半径的长度
        float radio = (mRange.right - mRange.left) / 2;
        //计算终点坐标
        float x = 0;
        float y = 0;
        if (range < 90) {
            x = (float) (radio * Math.sin(Math.toRadians(range)));
            y = (float) (radio * Math.cos(Math.toRadians(range)));

            x += mCenter;
            y = mCenter - y;
        } else {
            x = (float) (radio * Math.sin(Math.toRadians(180 - range)));
            y = (float) (radio * Math.cos(Math.toRadians(180 - range)));

            x += mCenter;
            y += mCenter;
        }
        canvas.drawLine(mCenter, mCenter, x, y, mLinePaint);
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域
     *
     * @param startAngle
     */
    private int calInExactArea(float startAngle) {
        float size = 360f / mItemCount;
        // 确保rotate是正的，且在0-360度之间
        float rotate = (startAngle % 360.0f + 360.0f) % 360.0f;

        for (int i = 0; i < mItemCount; i++) {
            // 每个的范围
            if (i == 0) {
                if ((rotate > 360 - size / 2) || (rotate < size / 2)) {
                    return i;
                }
            } else {
                float from = size * (i - 1) + size / 2;
                float to = from + size;
                if ((rotate > from) && (rotate < to)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /*
    * 通过坐标系的偏移来确定当前在[330,30]（假如是6个item）范围内的item下标
    * */
    private int getZeroPositionByCoordinateSystem(float offAngle){
        int ret = 0;
        float size = 360 / mItemCount;
        float rotate = (offAngle % 360.0f + 360.0f) % 360.0f;

        if(rotate < 0){
            rotate = 360 + rotate;
        }

        if(Math.abs(rotate) <= Math.abs(size / 2)){
            return ret;
        } else if(rotate > size/2){
            int offCount = (int) ((rotate - size/2)/size);//需要转过的item数
            ret = mItemCount - offCount - 1;
            return ret;
        }
        return -1;
    }

    /*
    * 根据落点计算角度（点击事件）
    * 得到的角度是落点和y轴上半轴的角度
    * */
    private float getRoundArc(float upX, float upY) {
        float arc = 0;
        //首先计算三边的长度 Math.sqrt求平方根，Math.pow求参数一的参数二次方
        float a = (float) Math.sqrt(Math.pow(mCenter - mCenter, 2) + Math.pow(0 - mCenter, 2));
        float b = (float) Math.sqrt(Math.pow(upX - mCenter, 2) + Math.pow(upY - mCenter, 2));
        float c = (float) Math.sqrt(Math.pow(upX - mCenter, 2) + Math.pow(upY - 0, 2));
        //判断是否为三角形
        if (a + b > c) {//两边之和大于第三边为三角形
            /**
             * 接下来计算角度
             *
             * acos((a2+b2-c2)/2ab)
             *
             * **/
            arc = (float) (Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)) * 180 / Math.PI);

            //判断是大于左边还是右边，也就是180以内还是以外
            if (upX < mCenter) {//此时是180以外的
                arc = 360 - arc;
            }
        } else {//上下边界的问题
            if (upX == mCenter) {
                if (upY < mCenter) {
                    arc = 0;
                } else {
                    arc = 180;
                }
            }
        }
        return arc;
    }

    /*
    * 根据三点的坐标计算旋转的角度
    * */
    private float getRoundArc(float startX, float startY, float endX, float endY) {
        float arc = 0;
        //首先计算三边的长度
        float a = (float) Math.sqrt(Math.pow(startX - mCenter, 2) + Math.pow(startY - mCenter, 2));
        float b = (float) Math.sqrt(Math.pow(endX - mCenter, 2) + Math.pow(endY - mCenter, 2));
        float c = (float) Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        //判断是否为三角形
        if (a + b > c) {//两边之和大于第三边为三角形
            /**
             * 接下来计算角度
             *
             * acos((a2+b2-c2)/2ab)
             *
             * **/
            arc = (float) (Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)) * 180 / Math.PI);

            if (startX <= mCenter && endX >= mCenter && startY < mCenter && endY < mCenter) {//上边顺时针越界，不管他
            } else if (startX >= mCenter && endX <= mCenter && startY < mCenter && endY < mCenter) {//上边逆时针越界
                arc = -arc;
            } else if (startX <= mCenter && endX >= mCenter && startY > mCenter && endY > mCenter) {//下边逆时针越界
                arc = -arc;
            } else if (startX <= mCenter && endX >= mCenter && startY < mCenter && endY < mCenter) {//下边顺时针越界，不管他
            } else if (endX >= mCenter && startX >= mCenter) {//这个时候表示在右半区
                if (startY > endY) {
                    arc = -arc;
                }
            } else if (endX < mCenter && startX < mCenter) {//此时在左半区
                if (startY < endY) {
                    arc = -arc;
                }
            }
        }
        if (Math.abs(arc) >= 0 && Math.abs(arc) <= 180) {//主要解决nan的问题
            return arc;
        } else {
            return 0;
        }
    }


    private void init(){
        if(!hasMeasured){
            mBackgroundWidth = getDrawWidth(mBackgroundWidth);
            mRangeWidth = getDrawWidth(mRangeWidth);
            mCheckBitmapWidth = getDrawWidth(mCheckBitmapWidth);
            mLitterBitWidth = getDrawWidth(mLitterBitWidth);
            mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,mTextSize/3,getResources().getDisplayMetrics());
            hasMeasured = true;

        }

        //初始化绘制 圆弧块的画笔（相当于一个item）
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);//抗锯齿
        mArcPaint.setDither(true);//防抖动
        //初始化绘制文字的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(0xff222222);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        //设置圆形背景的画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);
        //分割线的画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(dip2px(1f));
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mSplitLineColor);
        //内圈画盘(矩形)
        mRange = new RectF(mCenter - mRangeWidth / 2, mCenter - mRangeWidth / 2, mCenter + mRangeWidth / 2, mCenter + mRangeWidth / 2);
        //中心图片绘制区域（矩形）
        mCheckBitmapRect = new RectF(mCenter - mCheckBitmapWidth / 2, mCenter - mCheckBitmapWidth / 2, mCenter + mCheckBitmapWidth / 2, mCenter + mCheckBitmapWidth / 2);

    }

    /**
     * 当前切图的比例都是在1080p下进行的，所以这边的比例就是1080的
     * <p>
     * mDrawWidth 表示为控件的宽度
     **/
    private float getDrawWidth(float width) {
        return width * mDrawWidth / 1080f;
    }

    /**
     * dp转像素
     *
     * @param dpValue
     * @return
     */
    private int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getContext().getResources().getDisplayMetrics());
    }

    //////////////////////////////////////////////////////////////////
    /*
    * position：当前被选中（手指按下）的item，在数据源中所在的序列
    * */
    public interface OnWheelCheckListener {
        void onCheck(int position);
    }

    /*
    * position：当前被选中（在0位置）的item，在数据源中所在的序列
    * */
    public interface OnCenterBitmapClickListener{
        void onCenterClick(int position);
    }

    //////////////////////////////////////////////////////////////
    /*
    * 对外提供的方法
    * */

    /*
    * 设置数据源
    * */
    public void setBitInfos(List<RotaryTableInfo> bitInfos) {
        this.bitInfos = bitInfos;
        mItemCount = this.bitInfos.size();
        invalidate();
    }
}
