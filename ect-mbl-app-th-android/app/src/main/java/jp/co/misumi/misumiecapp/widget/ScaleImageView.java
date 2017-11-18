package jp.co.misumi.misumiecapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


/**
 * ピンチ操作で拡大縮小できる画像を表示するクラス.
 */
public class ScaleImageView extends ImageView implements OnTouchListener {

    String TAG = "ScaleImageView";

    /**
     * Context.
     */
    private Context mContext;

    /**
     * 拡大率の最大値.
     */
    private float MAX_SCALE = 5.0f;

    /**
     * 拡大計算用のMatrix.
     */
    private Matrix mMatrix;
    /**
     * 拡大計算用のMatrix.
     */
    private final float[] mMatrixValues = new float[9];

    /**
     * 描画エリアのサイズ幅.
     */
    private int mWidth;
    /**
     * 描画エリアのサイズ高さ.
     */
    private int mHeight;

    /**
     * 描画する画像のサイズ幅.
     */
    private int mIntrinsicWidth;
    /**
     * 描画する画像のサイズ高さ.
     */
    private int mIntrinsicHeight;

    /**
     * 拡大率.
     */
    private float mScale;
    /**
     * 最小の拡大率.
     */
    private float mMinScale;

    /**
     * ピンチ操作中フラグ.
     */
    private boolean isScaling;
    /**
     * ピンチ操作イベントの前回の距離.
     */
    private float mPrevDistance;

    /**
     * ピンチ操作の移動座標X.
     */
    private int mPrevMoveX;
    /**
     * ピンチ操作の移動座標Y.
     */
    private int mPrevMoveY;

    /**
     * GestureDetectorクラス.
     */
    private GestureDetector mDetector;


    /**
     * コンストラクタ.
     *
     * @param context Context
     */
    public ScaleImageView(Context context) {
        super(context);
        this.mContext = context;
        initialize();
    }

    /**
     * コンストラクタ.
     *
     * @param context Context
     * @param attr    AttributeSet
     */
    public ScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        initialize();
    }


    /* (非 Javadoc)
     * @see android.widget.ImageView#setImageBitmap(Bitmap bm)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        this.initialize();
    }

    /* (非 Javadoc)
     * @see android.widget.ImageView#setImageBitmap(Bitmap bm)
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.initialize();
    }

    /* (非 Javadoc)
     * @see android.widget.ImageView#setImageResource(int resId)
     */
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        this.initialize();
    }

    /**
     * 画像表示用の初期化を行う.<br>
     * 拡大縮小用のMATRIX変数やピンチ操作用のGestureDetectorを設定する
     */
    private void initialize() {

        this.setScaleType(ScaleType.MATRIX);
        this.mMatrix = new Matrix();
        Drawable d = getDrawable();

        if (d != null) {
            mIntrinsicWidth = d.getIntrinsicWidth();
            mIntrinsicHeight = d.getIntrinsicHeight();
            setOnTouchListener(this);
        }

        mDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                maxZoomTo((int) e.getX(), (int) e.getY());
                cutting();
                return super.onDoubleTap(e);
            }
        });
    }

    /* (非 Javadoc)
     * @see android.widget.ImageView#setFrame(int l, int t, int r, int b)
     */
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        mWidth = r - l;
        mHeight = b - t;

        if (mIntrinsicWidth > 0 && mIntrinsicHeight > 0) {

            mMatrix.reset();

            mScale = (float) mWidth / (float) mIntrinsicWidth;

            int paddingHeight = 0;
            int paddingWidth = 0;
            // scaling vertical
            if (mScale * mIntrinsicHeight > mHeight) {

                mScale = (float) mHeight / (float) mIntrinsicHeight;
                mMatrix.postScale(mScale, mScale);
                paddingWidth = (r - mWidth) / 2;
                paddingHeight = 0;
                // scaling horizontal
            } else if (mScale * mIntrinsicWidth > mWidth) {

                mMatrix.postScale(mScale, mScale);
                paddingHeight = (b - mHeight) / 2;
                paddingWidth = 0;
            } else {
            }

            mMatrix.postTranslate(paddingWidth, paddingHeight);

            setImageMatrix(mMatrix);

            mMinScale = mScale;
            mScale = mMinScale / getScale();
            zoomTo(mScale, mWidth / 2, mHeight / 2);
            cutting();
        }

        return super.setFrame(l, t, r, b);
    }

    /**
     * Matrixクラスの指定の値を取得する.
     *
     * @param matrix     Matrixクラス
     * @param whichValue 取得するタイプ
     * @return Matrixクラスの指定の値
     */
    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }


    /**
     * Matrixクラスの拡大率を取得する.
     *
     * @return Matrixクラスの拡大率の値
     */
    protected float getScale() {
        return getValue(mMatrix, Matrix.MSCALE_X);
    }

    /**
     * MatrixクラスのX座標を取得する.
     *
     * @return MatrixクラスのX座標の値
     */
    protected float getTranslateX() {
        return getValue(mMatrix, Matrix.MTRANS_X);
    }

    /**
     * MatrixクラスのY座標を取得する.
     *
     * @return MatrixクラスのY座標の値
     */
    protected float getTranslateY() {
        return getValue(mMatrix, Matrix.MTRANS_Y);
    }

    /**
     * 拡大率と座標をセットする.
     *
     * @param x X座標
     * @param y Y座標
     */
    protected void maxZoomTo(int x, int y) {

        if (mMinScale != getScale() && (getScale() - mMinScale) > 0.1f) {
            // threshold 0.1f
            float scale = mMinScale / getScale();
            zoomTo(scale, x, y);
        } else {
            float scale = MAX_SCALE / getScale();
            zoomTo(scale, x, y);
        }

    }

    /**
     * 拡大率と座標をセットする.
     *
     * @param scale 拡大率
     * @param x     X座標
     * @param y     Y座標
     */
    public void zoomTo(float scale, int x, int y) {
        if (getScale() * scale < mMinScale) {
            return;
        }
        if (scale >= 1 && getScale() * scale > MAX_SCALE) {
            // 小さい画像の場合は最大になる様にする
            scale = MAX_SCALE / getScale();
//			return;
        }
        mMatrix.postScale(scale, scale);
        // move to center
        mMatrix.postTranslate(-(mWidth * scale - mWidth) / 2,
                -(mHeight * scale - mHeight) / 2);

        // move x and y distance
        mMatrix.postTranslate(-(x - (mWidth / 2)) * scale, 0);
        mMatrix.postTranslate(0, -(y - (mHeight / 2)) * scale);
        setImageMatrix(mMatrix);
    }

    /**
     * 画像の表示位置を調整する.
     */
    public void cutting() {

        int width = (int) (mIntrinsicWidth * getScale());
        int height = (int) (mIntrinsicHeight * getScale());

        if (getTranslateX() < -(width - mWidth)) {
            mMatrix.postTranslate(-(getTranslateX() + width - mWidth), 0);
        }

        if (getTranslateX() > 0) {
            mMatrix.postTranslate(-getTranslateX(), 0);
        }

        if (getTranslateY() < -(height - mHeight)) {
            mMatrix.postTranslate(0, -(getTranslateY() + height - mHeight));
        }

        if (getTranslateY() > 0) {
            mMatrix.postTranslate(0, -getTranslateY());
        }

        if (width < mWidth) {
            mMatrix.postTranslate((mWidth - width) / 2, 0);
        }

        if (height < mHeight) {
            mMatrix.postTranslate(0, (mHeight - height) / 2);
        }

        setImageMatrix(mMatrix);

    }

    /**
     * 2点間のピンチ操作の距離を計算する.
     *
     * @param x0 X座標
     * @param x1 X座標
     * @param y0 Y座標
     * @param y1 Y座標
     * @return 2点間の距離
     */
    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Viewの対角線の長さを計算する.
     *
     * @return Viewの対角線の長さ
     */
    private float dispDistance() {
        return (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
    }


    /* (非 Javadoc)
     * @see android.widget.ImageView#onTouchEvent(MotionEvent event)
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event)) {
            return true;
        }
        int touchCount = event.getPointerCount();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
                if (touchCount >= 2) {
                    mPrevDistance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    isScaling = true;
                } else {
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                }
            case MotionEvent.ACTION_MOVE:
                if (touchCount >= 2 && isScaling) {
                    float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    float scale = (dist - mPrevDistance) / dispDistance();
                    mPrevDistance = dist;
                    scale += 1;
                    scale = scale * scale;
                    zoomTo(scale, mWidth / 2, mHeight / 2);
                    cutting();
                } else if (!isScaling) {
                    int distanceX = mPrevMoveX - (int) event.getX();
                    int distanceY = mPrevMoveY - (int) event.getY();
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                    mMatrix.postTranslate(-distanceX, -distanceY);
                    cutting();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
                if (event.getPointerCount() <= 1) {
                    isScaling = false;
                }
                break;
        }
        return true;
    }

    /* (非 Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(View v, MotionEvent event)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
