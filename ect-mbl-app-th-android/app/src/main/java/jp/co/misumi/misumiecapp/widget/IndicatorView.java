package jp.co.misumi.misumiecapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import jp.co.misumi.misumiecapp.R;

/**
 * ViewPager用のインジケータ表示クラス.
 */
public class IndicatorView extends View {

    /**
     * インジケータの半径.
     */
    private static float RADIUS;    // = 5.0f;

    /**
     * インジケータの間隔.
     */
    private static float DISTANCE;    // = 30.0f;

    /**
     * 描画する時の線の幅.
     */
    private static float WIDTH;        // = 30.0f;

    /**
     * 選択中の色.
     */
    private static int CURRENT_COLOR;

    /**
     * 非選択中の色.
     */
    private static int OTHER_COLOR;

    /**
     * ページの数.
     */
    private int mNumOfViews;

    /**
     * 選択中のページ.
     */
    private int mPosition;

    /**
     * 連動するViewPagerクラス.
     */
    private ViewPager mViewPager;

    /**
     * 連動するGalleryクラス.
     */
    private Gallery mGallery;

    /**
     * コンストラクタ.
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = context.getResources();
        RADIUS = res.getDimension(R.dimen.pager_marker_radius);
        DISTANCE = res.getDimension(R.dimen.pager_marker_distance);
        WIDTH = res.getDimension(R.dimen.pager_marker_width);

        CURRENT_COLOR = res.getColor(R.color.pager_marker_current);
        OTHER_COLOR = res.getColor(R.color.pager_marker_other);
    }

    /**
     * コンストラクタ.
     *
     * @param context  Context
     * @param attrs    AttributeSet
     * @param defStyle defStyle
     */
    public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources res = context.getResources();
        RADIUS = res.getDimension(R.dimen.pager_marker_radius);
        DISTANCE = res.getDimension(R.dimen.pager_marker_distance);
        WIDTH = res.getDimension(R.dimen.pager_marker_width);

        CURRENT_COLOR = res.getColor(R.color.pager_marker_current);
        OTHER_COLOR = res.getColor(R.color.pager_marker_other);
    }


    /**
     * インジケータの選択中のページを設定する.
     *
     * @param position 選択中のページ番号
     */
    public void setPosition(final int position) {
        if (position < mNumOfViews) {
            mPosition = position;
            if (mViewPager != null) {
//                mViewPager.setCurrentItem(mPosition);
            }
            invalidate();
        }
    }

    /**
     * 連動するViewPagerクラスを設定する.
     *
     * @param viewPager 連動するViewPagerクラス
     */
    public void setViewPager(final ViewPager viewPager) {
        mViewPager = viewPager;
        updateNumOfViews();
    }


    /**
     * ViewPagerクラスのページ変更イベントを設定する.
     *
     * @param viewPager ViewPagerクラス
     */
    public void setViewPagerEvent(final ViewPager viewPager) {

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

//				Logger.e("TAG", "onPageSelected:" + position);

                updateNumOfViews();
                setPosition(position);
            }
        });

    }


    /**
     * Galleryクラスのページ変更イベントを設定する.
     *
     * @param viewPager Galleryクラス
     */
    public void setGallery(final Gallery viewPager) {
        mGallery = viewPager;
        updateNumOfViews();

        final AdapterView.OnItemSelectedListener onItemSelectedListener = mGallery.getOnItemSelectedListener();

        mGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                onItemSelectedListener.onItemSelected(parent, view, position, id);

                updateNumOfViews();
                setPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                onItemSelectedListener.onNothingSelected(parent);
            }
        });
    }

    /**
     * インジケータを描画する.
     *
     * @param canvas Canvasクラス
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStrokeWidth(WIDTH);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        int bw = (int) ((mNumOfViews + 1) * DISTANCE);
        int bh = getHeight();

        if (getWidth() >= bw) {

            for (int i = 0; i < mNumOfViews; i++) {

                float cx = (getWidth() - (mNumOfViews - 1) * DISTANCE) / 2 + i * DISTANCE;
                float cy = getHeight() / 2.0f;
                if (mPosition == i) {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setColor(CURRENT_COLOR);
                } else {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setColor(OTHER_COLOR);
                }
                canvas.drawCircle(cx, cy, RADIUS, paint);
            }
        } else {

            //Bitmap作成
            Bitmap bmp = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
            Canvas bmpCanvas = new Canvas(bmp);

            for (int i = 0; i < mNumOfViews; i++) {

                float cx = DISTANCE + i * DISTANCE;
                float cy = bh / 2.0f;
                if (mPosition == i) {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setColor(CURRENT_COLOR);
                } else {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setColor(OTHER_COLOR);
                }
                bmpCanvas.drawCircle(cx, cy, RADIUS, paint);
            }

            //(int left, int top, int right, int bottom)
            Rect src = new Rect(0, 0, bw, bh);

            //縦幅も合わせて縮小する
            bh = (int) (bh * ((float) getWidth() / bw));
            if (bh < (int) (RADIUS / 2.0f)) {
                bh = (int) (RADIUS / 2.0f);
            }

            Rect dst = new Rect(0, 0, getWidth(), bh);
            canvas.drawBitmap(bmp, src, dst, paint);
        }

    }


    /**
     * インジケータの総ページ数を更新する.
     */
    private void updateNumOfViews() {
        if (mGallery != null) {
            if (mGallery.getAdapter() == null) {
                mNumOfViews = 0;
            } else {
                mNumOfViews = mGallery.getAdapter().getCount();
            }
        } else if (mViewPager != null) {
            if (mViewPager.getAdapter() == null) {
                mNumOfViews = 0;
            } else {
                mNumOfViews = mViewPager.getAdapter().getCount();
            }
        }
    }

}
