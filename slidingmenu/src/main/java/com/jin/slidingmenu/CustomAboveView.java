package com.jin.slidingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jin.slidingmenu.paints.FPaint;
import com.jin.slidingmenu.paints.IPaint;
import com.jin.slidingmenu.paints.LogUtils;

/**
 * 侧滑菜单的上部view
 * 在这里你可以布局一个头像，用户昵称，背景图片，你可以设置背景图片的颜色
 * Created by Jin on 2018/1/2.
 */

public class CustomAboveView extends View {

    private Bitmap backgroudBitmap;

    private IPaint iPaint;
    private FPaint fPaint;

    /**
     * 背景图片的Rectf
     */
    private RectF bgRectF;

    private int bgImgId;

    private String nickName;

    /**
     * 组件高度
     */
    private int viewHeight = 600;

    /**
     * 左边距
     */
    private int leftPadding = 40;

    /**
     * 头像左边距
     */
    private int photoLeftMargin = 0;

    /**
     * 头像右边距
     */
    private int photoRightMargin = 20;

    /**
     * 昵称左边距
     */
    private int nickNameLeftMargin = 0;

    /**
     * 头像 ----------------------------------------------------------------------------------------
     */
    private Bitmap mPhotoBitmap;

    /**
     * 头像半径
     */
    private int photoR = 100;

    /**
     * 头像样式 -- 圆形
     */
    final public int PHOTO_STYLE_CIRCLE = 0;

    /**
     * 头像样式 -- 圆角矩形
     */
    final public int PHOTO_STYLE_ROUND_RECTANGLE = 1;

    /**
     * 头像边框角度
     */
    private int mCornerRadius;

    /**
     * 头像边框样式
     */
    private int mPhotoFormat;

    /**
     * 头像外边框宽度
     */
    private int mBorderWidth;

    /**
     * 头像外边框颜色
     */
    private int mBorderColor;
    private RectF mRectF;
    private Matrix matrix;
    private Path mPath;

    /**
     * 头像边框是否透明
     */
    private boolean mBorderIsTransparent;
    private BitmapShader mBitmapShader;

    /**
     * 使用mPhotoBitmap绘制的一张新头像
     */
    private Bitmap photoBitmap;

    private Canvas c;

    /**
     * 监听器
     */
    private OnClickListener photoOnClickListener;

    private OnClickListener nickNameOnClickListener;
    private int startX;
    private int startY;

    /**
     * 最小点击距离
     * 如果用户移动距离小于这个距离，则视为点击事件。
     */
    private int minOnClickDis = 10;


    public CustomAboveView(Context context) {
        this(context, null);
    }

    public CustomAboveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAboveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context c, @Nullable AttributeSet attrs, int defStyleAttr) {

        initAttrs(c,attrs);
        initDrawTools();
        handlerBitmap();
    }

    private void initAttrs(Context c, @Nullable AttributeSet attrs) {
        TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.CustomAboveView);

        bgImgId = typedArray.getResourceId(R.styleable.CustomAboveView_sm_backgroundimg, -1);
        if (attrs != null && bgImgId == -1) {
            throw new RuntimeException("测滑菜单的图片没有设置xml属性哦");
        }

        //背景图片
        backgroudBitmap = BitmapFactory.decodeResource(getResources(), bgImgId);

        //用户昵称
        nickName = typedArray.getString(R.styleable.CustomAboveView_sm_nickname);

        //字体颜色
        int fontColor = typedArray.getColor(R.styleable.CustomAboveView_sm_nicknameColor, Color.BLACK);

        //字体大小
        float fontSize = typedArray.getDimension(R.styleable.CustomAboveView_sm_font_size, 45);

        //头像
        int srcBitmapId = typedArray.getResourceId(R.styleable.CustomAboveView_sm_photo_src, -1);
        if (attrs != null && srcBitmapId == -1) {
            throw new RuntimeException("是不是你的xml_PhotoView源头像（src）没有设置呢^-^？");
        }
        mPhotoBitmap = BitmapFactory.decodeResource(getResources(), srcBitmapId);

        //头像样式
        mPhotoFormat = typedArray.getInt(R.styleable.CustomAboveView_sm_format, PHOTO_STYLE_CIRCLE);

        //头像边框角度
        mCornerRadius = typedArray.getInt(R.styleable.CustomAboveView_sm_color_radius, 0);

        //边框宽度
        mBorderWidth = typedArray.getInt(R.styleable.CustomAboveView_sm_border_width, 0);

        //边框是否透明
        mBorderIsTransparent = typedArray.getBoolean(R.styleable.CustomAboveView_sm_border_is_transparent, false);

        // 边框颜色
        mBorderColor = typedArray.getColor(R.styleable.CustomAboveView_sm_broder_color, Color.WHITE);
        typedArray.recycle();
        initPaints(fontColor,(int)fontSize);
    }

    /**
     * 初始化画笔
     * @param color color
     * @param size size
     */
    private void initPaints(int color, int size) {
        iPaint = new IPaint();
        fPaint = new FPaint(color, Paint.Style.FILL, 3, size);
    }

    private void initDrawTools() {
        mRectF = new RectF();
        matrix = new Matrix();
        mPath = new Path();
        bgRectF = new RectF();

        photoBitmap = Bitmap.createBitmap(getPhotoWidth(), getPhotoHeight(), Bitmap.Config.ARGB_8888);
        c = new Canvas(photoBitmap);
    }

    // 把图片裁剪成 1:1 的大小
    private void handlerBitmap() {
        if (mPhotoBitmap != null) {
            mPhotoBitmap = iPaint.makeScaleBitmap(mPhotoBitmap,0);
            mBitmapShader = new BitmapShader(mPhotoBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
    }


    public Bitmap getBackgroudBitmap() {
        return backgroudBitmap;
    }

    public void setBackgroudBitmap(Bitmap backgroudBitmap) {
        this.backgroudBitmap = backgroudBitmap;
    }

    public int getBgImgId() {
        return bgImgId;
    }

    public void setBgImgId(int bgImgId) {
        this.bgImgId = bgImgId;
        backgroudBitmap = BitmapFactory.decodeResource(getResources(),bgImgId);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoR = photoWidth >> 1;
    }

    public int getPhotoWidth() {
        return photoR << 1;
    }

    public int getPhotoHeight() {
        return photoR << 1;
    }

    public void setFontColor(int fontColor) {
        fPaint.setColor(fontColor);
    }

    public void setFontSize(int fontSize) {
        fPaint.setTextSize(fontSize);
    }

    public int getFontSize() {
        return (int)fPaint.getTextSize();
    }

    public int getNickNameHeight() {
        return (int)fPaint.getTextHeight();
    }

    public void setFontWidth(int fontWidth) {
        fPaint.setStrokeWidth(fontWidth);
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setPhotoLeftMargin(int photoLeftMargin) {
        this.photoLeftMargin = photoLeftMargin;
    }

    public int getPhotoLeftMargin() {
        return photoLeftMargin;
    }

    public void setPhotoRightMargin(int photoRightMargin) {
        this.photoRightMargin = photoRightMargin;
    }

    public int getPhotoRightMargin() {
        return photoRightMargin;
    }

    public void setNickNameLeftMargin(int nickNameLeftMargin) {
        this.nickNameLeftMargin = nickNameLeftMargin;
    }

    public int getNickNameLeftMargin() {
        return nickNameLeftMargin;
    }

    public int getTopPadding() {
        return viewHeight >> 1;
    }

    public void setPhotoR(int photoR) {
        this.photoR = photoR;
    }

    public int getPhotoR() {
        return photoR;
    }

    public void setmPhotoBitmap(Bitmap mPhotoBitmap) {
        this.mPhotoBitmap = mPhotoBitmap;
        // 处理图片
        handlerBitmap();
    }

    public void setmPhotoBitmap(@IdRes int phtotBitmap) {
        this.mPhotoBitmap = BitmapFactory.decodeResource(getResources(),phtotBitmap);
        handlerBitmap();
    }

    public Bitmap getmPhotoBitmap() {
        return mPhotoBitmap;
    }

    public int getmCornerRadius() {
        return mCornerRadius;
    }

    public void setmCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    public int getmPhotoFormat() {
        return mPhotoFormat;
    }

    public void setmPhotoFormat(int mPhotoFormat) {
        this.mPhotoFormat = mPhotoFormat;
    }

    public int getmBorderWidth() {
        return mBorderWidth;
    }

    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public int getmBorderColor() {
        return mBorderColor;
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public boolean ismBorderIsTransparent() {
        return mBorderIsTransparent;
    }

    public void setmBorderIsTransparent(boolean mBorderIsTransparent) {
        this.mBorderIsTransparent = mBorderIsTransparent;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (backgroudBitmap != null) {
            bgRectF.set(0,0,getWidth(), getWidth() * backgroudBitmap.getHeight()/ backgroudBitmap.getWidth());
            canvas.drawBitmap(backgroudBitmap,null,bgRectF,iPaint);
        }

        if (nickName != null) {
            float y = fPaint.findCenterBaseY(getHeight() / 2);
            canvas.drawText(nickName,photoR * 2 + leftPadding + photoLeftMargin + photoRightMargin + nickNameLeftMargin, y,fPaint);
        }


        int startX = leftPadding;
        int startY = getHeight()/2 - photoR;

        if (mPhotoBitmap != null) {

            //view的rect和中心坐标
            float[] v = iPaint.calculateViewRect(photoR * 2, photoR * 2, mBorderWidth);
            //border的rect和中心坐标
            float[] b = iPaint.calculateBorderRect(photoR * 2, photoR * 2, mBorderWidth);

            //缩放图片和控件宽高一致
            float scale = (float) photoR * 2 /  mPhotoBitmap.getWidth();
            matrix.setScale(scale,scale);
            mBitmapShader.setLocalMatrix(matrix);
            iPaint.setShader(mBitmapShader);

            if (mPhotoFormat == PHOTO_STYLE_CIRCLE) {
                //绘制圆形头像框
                cavansCirclePhoto(c,v,b);
            } else if (mPhotoFormat == PHOTO_STYLE_ROUND_RECTANGLE) {
                //绘制矩形头像框
                cavansRectPhoto(c,v,b);
            }

            canvas.drawBitmap(photoBitmap,startX,startY,iPaint);
        }
    }

    private void cavansRectPhoto(Canvas canvas, float[] v, float[] b) {
        mRectF.set(v[0], v[1], v[2], v[3]);
        canvas.drawRoundRect(mRectF,mBorderWidth,mBorderWidth,iPaint);

        if (mBorderWidth > 0) {
            mPath.reset();
            mRectF.set(b[0], b[1], b[2], b[3]);
            if (! mBorderIsTransparent) {
                iPaint.setShader(null);
            }
            mPath.addRoundRect(mRectF,mBorderWidth,mBorderWidth, Path.Direction.CCW);
            iPaint.setColor(mBorderColor);
            iPaint.setStyle(Paint.Style.STROKE);
            iPaint.setStrokeWidth(mBorderWidth);
            canvas.drawPath(mPath,iPaint);
            iPaint.setShader(mBitmapShader);
            iPaint.setStyle(Paint.Style.FILL);
            iPaint.setColor(Color.RED);
        }
    }

    private void cavansCirclePhoto(Canvas canvas, float[] v, float[] b) {
        canvas.drawCircle(v[4],v[5] ,v[6],iPaint);
        //绘制圆形头像的边框
        if (mBorderWidth > 0) {
            //border半径
            mPath.reset();
            if (! mBorderIsTransparent) {
                iPaint.setShader(null);
            }
            mPath.addCircle(b[4], b[5], b[6], Path.Direction.CCW);
            iPaint.setColor(mBorderColor);
            iPaint.setStyle(Paint.Style.STROKE);
            iPaint.setStrokeWidth(mBorderWidth);
            canvas.drawPath(mPath,iPaint);
            iPaint.setShader(mBitmapShader);
            iPaint.setStyle(Paint.Style.FILL);
            iPaint.setColor(Color.RED);
        }
    }

    public int getNickNameTextWidth() {
        if (nickName != null) {
            return fPaint.getTextWidth(nickName);
        } else {
            return 0;
        }
    }

    private RectF getPhotoRectF() {
        RectF photoRectf = new RectF();
        int l = getLeftPadding() + getPhotoLeftMargin();
        int t = getViewHeight() / 2 - getPhotoR();
        int r = l + getPhotoR() * 2;
        int b = t + getPhotoR() * 2;
        photoRectf.set(l,t,r,b);
        return photoRectf;
    }

    private RectF getNickNameRectF() {
        RectF nickNameRectF = new RectF();
        int l = getLeftPadding() + getPhotoLeftMargin() + 2 * getPhotoR() + getPhotoRightMargin() + nickNameLeftMargin;
        int t = getTopPadding() - getNickNameHeight() / 2;
        int r = l + getNickNameTextWidth();
        int b = t + getNickNameHeight();
        nickNameRectF.set(l,t,r,b);
        return nickNameRectF;
    }

    public boolean isPhotoExist() {
        return mPhotoBitmap != null;
    }

    public boolean isNickNameExist() {
        return !TextUtils.isEmpty(getNickName());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wm = MeasureSpec.getMode(widthMeasureSpec); //1073741824 -- 1 << 30 :match_parent
        int hm = MeasureSpec.getMode(heightMeasureSpec); //-2147483648 -- 2 << 20 : wrap_content
        int ws = MeasureSpec.getSize(widthMeasureSpec);  // 801
        int hs = MeasureSpec.getSize(heightMeasureSpec);  // 1704
        int width = getNickNameTextWidth() + photoR * 2 + leftPadding + photoLeftMargin + photoRightMargin + nickNameLeftMargin;
        setMeasuredDimension(wm == MeasureSpec.EXACTLY ? ws : width, hm == MeasureSpec.EXACTLY ? hs: viewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.e("CustomAboveView--down");
                startX = (int)e.getX();
                startY = (int)e.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e("CustomAboveView--up");
                int x = (int) e.getX();
                int y = (int) e.getY();
                int dx = x - startX;
                int dy = y - startY;
                LogUtils.d("dx:%d,dy:%d",dx,dy);
                if (Math.abs(dx) < minOnClickDis && Math.abs(dy) <minOnClickDis) {

                    if (photoOnClickListener != null && isPhotoExist() && getPhotoRectF().contains(x,y)) {
                        photoOnClickListener.onClick(this);
                    }

                    if (nickNameOnClickListener != null && isNickNameExist() && getNickNameRectF().contains(x,y)) {
                        nickNameOnClickListener.onClick(this);
                    }
                }
                break;
        }
        LogUtils.e("CustomAboveView--------onTouchEvent");
        return true;
    }


    public void setPhotoOnClickListener(OnClickListener listener) {
        this.photoOnClickListener = listener;
    }

    public void setNickNameOnClickListener(OnClickListener listener) {
        this.nickNameOnClickListener = listener;
    }

    public void removePhotoOnClickListener() {
        this.photoOnClickListener = null;
    }

    public void removeNickNameOnClickListener() {
        this.nickNameOnClickListener = null;
    }

}
