package com.android.eflowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author ccx
 * create time 2018/11/27
 */
public class EFlowLayout extends ViewGroup {

    /**
     * 左边偏移量
     */
    private float leftMargin   = 0;
    /**
     * 上部偏移量
     */
    private float topMargin    = 0;
    /**
     * 右边偏移量
     */
    private float rightMargin  = 0;
    /**
     * 下部偏移量
     */
    private float bottomMargin = 0;

    public EFlowLayout(Context context) {
        this(context, null);
    }

    public EFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EFlowLayout);
        leftMargin = typedArray.getDimension(R.styleable.EFlowLayout_item_left_margin, 0);
        rightMargin = typedArray.getDimension(R.styleable.EFlowLayout_item_right_margin, 0);
        topMargin = typedArray.getDimension(R.styleable.EFlowLayout_item_top_margin, 0);
        bottomMargin = typedArray.getDimension(R.styleable.EFlowLayout_item_bottom_margin, 0);
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 为什么定义x，因为是从这个坐标开始出发
        // 往右进行摆放
        int x = (int) (this.getPaddingLeft() + leftMargin);

        int paddingRight = this.getPaddingRight();
        // 为什么定义y，因为从上往下，top等于y轴线
        int y        = (int) (this.getPaddingTop() + topMargin);
        int sumWidth = r - l;

        int childCount = this.getChildCount();

        int childMaxHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View view = this.getChildAt(i);

            // 如果大于了。需要规整
            if (sumWidth < x + view.getMeasuredWidth() + paddingRight) {
                // 跨行
                // 改变x轴起始点
                x = (int) (getPaddingLeft() + leftMargin);
                // 改变y轴起始点
                y += childMaxHeight;
                childMaxHeight = 0;
            }
            view.layout(x, y, x + view.getMeasuredWidth(), y + view.getMeasuredHeight());
            // 改变横坐标，切记加入view的宽度.否则会出问题
            x += view.getMeasuredWidth() + rightMargin;
            // 取最大宽度，为下一步跨行做准备
            childMaxHeight = (int) Math.max(childMaxHeight, view.getMeasuredHeight() + topMargin + bottomMargin);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取子view的数量
        int childCount = this.getChildCount();
        // 获取到本view的宽度最大值
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingLeft() - this.getPaddingRight();
        // 需要测量view的宽度以及view的高度。
        // 所有的合集
        // 总高度
        int sumHeight = 0;
        // 一行的子类总高
        int childMaxHeight = 0;
        // 总宽度
        int sumWidth = 0;
        // 一行的子类总宽
        int sumChildWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View view = this.getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            // 如果有leftMargin的话，需要在测量的时候，加上这个
            sumChildWidth = (int) (sumChildWidth + leftMargin + rightMargin);
            // 如果小于两者相加，所以超了，需要计算高度
            // 取高度最大值，也就是所有控件的最大值
            // 加完之后要清除，否则下一行高度无法计算
            if (maxWidth < (sumChildWidth + view.getMeasuredWidth())) {
                // 跨行
                sumHeight += childMaxHeight;
                childMaxHeight = 0;
                // 跟自己比较，获取最大值，优先取最大
                sumWidth = Math.max(sumChildWidth, sumChildWidth);
                sumChildWidth = 0;

            }
            // 判断子类高度最大值
            childMaxHeight = (int) Math.max(childMaxHeight, view.getMeasuredHeight() + topMargin + bottomMargin);
            // 取子类行总宽，需要判断父类的宽度
            sumChildWidth += view.getMeasuredWidth();
        }
        // 因为最后一行可能没有超过，所以不会进入，则需要重新加一下最后一行
        sumHeight += childMaxHeight;
        sumWidth = Math.max(sumChildWidth, sumWidth);

        setMeasuredDimension(measureWidth(widthMeasureSpec, sumWidth), measureHeight(heightMeasureSpec, sumHeight));

    }


    private int measureHeight(int heightMeasureSpec, int sumHeight) {
        int result = 0;
        int mode   = MeasureSpec.getMode(heightMeasureSpec);
        int size   = MeasureSpec.getSize(heightMeasureSpec);
        //EXACTLY
        //精确值模式，当控件的layout_width和layout_height属性指定为具体数值或match_parent时。

        //AT_MOST
        //最大值模式，当空间的宽高设置为wrap_content时。

        //UNSPECIFIED
        //未指定模式，View想多大就多大，通常在绘制自定义View时才会用。

        // 如果为精确值模式，那么不用判断了，直接返回
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
            return result;
        }
        result = sumHeight + this.getPaddingTop() + this.getPaddingBottom();
        if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(size, result);
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec, int sumWidth) {
        int result = 0;
        int mode   = MeasureSpec.getMode(widthMeasureSpec);
        int size   = MeasureSpec.getSize(widthMeasureSpec);
        // 如果为精确值模式，那么不用判断了，直接返回
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
            return result;
        }
        result = widthMeasureSpec + this.getPaddingLeft() + this.getPaddingRight();
        if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(size, result);
        }
        return result;
    }


    public void setItemMargin(int l, int t, int r, int b) {
        this.leftMargin = l;
        this.topMargin = t;
        this.rightMargin = r;
        this.bottomMargin = b;
    }

}
