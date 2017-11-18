/*
 * Created by sys1yagi on 12/02/10
 * Copyright (C) 2012 sys1yagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.misumi.misumiecapp;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout.LayoutParams;

public class ExpandAnimator {

    public interface OnAnimationListener {
        void onStartExpand(ExpandAnimator e);

        void onStartUnexpand(ExpandAnimator e);

        void onExpanded(ExpandAnimator e);

        void onUnexpanded(ExpandAnimator e);
    }

    private float mDuration = 1000.0f;
    /**
     * アニメーションのインタポレータ
     */
    private Interpolator mInterpolator = new LinearInterpolator();
    /**
     * 開閉を行うView
     */
    private View mView = null;
    /**
     * アニメーション状態リスナ
     */
    private OnAnimationListener mListener = null;
    /**
     * アニメーション中フラグ
     */
    private boolean mAnimating = false;
    /**
     * 開閉対象となるViewの元の高さ
     */
    private int mOriginHeight = 0;
    /**
     * 画面サイズより大きい場合に伸びきらない時に trueを設定する
     */
    private boolean mWrapContent = false;


    /**
     * コンストラクタ。開閉対象となるViewと開閉アニメーションの状態リスナを受け取り初期化する。
     *
     * @param v        開閉対象となるView
     * @param listener 開閉アニメーションの状態リスナ
     */
    public ExpandAnimator(View v, OnAnimationListener listener) {
        mView = v;
        mOriginHeight = mView.getHeight();
        mListener = listener;
        mWrapContent = false;
        adjustSize();
    }

    /**
     * 開閉対象となるViewの本来の高さを計算します。 開閉対象となるViewに子要素を追加/削除した場合に呼び出す必要があります。
     */
    public void adjustSize() {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mView.measure(spec, spec);
        mOriginHeight = mView.getMeasuredHeight();
    }

    /**
     * 開閉対象となるViewの本来の高さを計算します。 高さを計算後直ちにViewサイズを変更します。
     * 開閉対象となるViewに子要素を追加/削除した場合に呼び出す必要があります。
     */
    public void adjustSizeImmediately() {
        adjustSize();
        if (mWrapContent) {
            //画面サイズより大きい場合に伸びきらない対応
            mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        } else {
            mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mOriginHeight));
        }

    }

    /**
     * アニメーションの時間を取得する事が出来ます。
     *
     * @return アニメーションの時間(ミリ秒)
     */
    public float getDuration() {
        return mDuration;
    }

    /**
     * アニメーションの時間をミリ秒単位でセットする事が出来ます。
     *
     * @param duration アニメーションの時間(ミリ秒)
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 画面サイズより大きい場合に伸びきらない時に trueを設定する
     *
     * @param wrapContent trueで LayoutParams.WRAP_CONTENTする
     */
    public void setWrapContent(boolean wrapContent) {
        mWrapContent = wrapContent;
    }

    /**
     * 現在セットされているインタポレータを取得する事が出来ます。
     *
     * @return インタポレータ
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * アニメーション中フラグ。
     *
     * @return アニメーション中フラグ
     */
    public boolean isAnimating() {
        return mAnimating;
    }


    /**
     * インタポレータをセットする事が出来ます。
     *
     * @param interpolator android.view.animation.Interpolatorを継承したものであれば何でも使えます。
     *                     http://developer
     *                     .android.com/reference/android/view/animation/Interpolator
     *                     .html
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 開閉対象となるViewを取得する事が出来ます。
     *
     * @return
     */
    public View getView() {
        return mView;
    }

    /**
     * あるアニメーションの経過時点の変化量を計算し、返します。
     * 具体的には、経過時間/アニメーション時間の値をインタポレータに掛け、得られた結果と開閉対象のViewの本来の高さを乗じた値を返します。
     *
     * @param origin 全体変化量。開閉対象のViewの本来の高さを利用する
     * @param time   アニメーションを開始してからの経過時間。
     * @return 合計変化量
     */
    private int move(int origin, long time) {
        long diff = (System.currentTimeMillis() - time);
        if (diff >= mDuration) {
            return origin;
        } else {
            float t = mInterpolator.getInterpolation(diff / mDuration);
            return (int) (origin * t);
        }
    }

    /**
     * 開閉対象となるViewが開いた状態かどうかを返します。
     *
     * @return true:開いている false:
     */
    public boolean isExpand() {
        return mView.getHeight() > (AppConfig.getInstance().dp);
    }

    /**
     * 開閉対象となるViewを閉じます。既に閉じている場合は
     * {@link OnAnimationListener#onUnexpanded(ExpandAnimator)} を呼び出して直ちに終了します。
     */
    public void unexpand() {

        //既にアニメ中は処理しない
        if (mAnimating) {
            return;
        }

        if (mView.getHeight() <= (AppConfig.getInstance().dp)) {
            if (mListener != null) {
                mListener.onUnexpanded(this);
            }
            return;
        }

        mAnimating = true;

        final long start = System.currentTimeMillis();
        Handler animationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                msg.arg1 = mOriginHeight - move(mOriginHeight, start);

                if (msg.arg1 <= (AppConfig.getInstance().dp)) {
                    msg.arg1 = (AppConfig.getInstance().dp);
                    //TODO:閉じた時は 0ドット
                    msg.arg1 = 0;
                }

                mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, msg.arg1));
                if (msg.arg1 <= (AppConfig.getInstance().dp)) {
                    if (mListener != null) {
                        mListener.onUnexpanded(ExpandAnimator.this);
                    }

                    mAnimating = false;
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    Message m = this.obtainMessage(0);
                    m.arg1 = msg.arg1;
                    this.sendMessage(m);
                }
            }
        };
        Message msg = animationHandler.obtainMessage(0);
        msg.arg1 = mView.getHeight();
        animationHandler.sendMessage(msg);
        if (mListener != null) {
            mListener.onStartUnexpand(this);
        }
    }

    /**
     * 開閉対象となるViewを開きます。既に開いている場合は
     * {@link OnAnimationListener#onExpanded(ExpandAnimator)} を呼び出して直ちに終了します。
     */
    public void expand() {

        //既にアニメ中は処理しない
        if (mAnimating) {
            return;
        }

        adjustSize();
        if (mView.getHeight() >= mOriginHeight) {
            if (mListener != null) {
                mListener.onExpanded(ExpandAnimator.this);
            }
            return;
        }

        mAnimating = true;

        final long start = System.currentTimeMillis();
        Handler animationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                msg.arg1 = move(mOriginHeight, start);
                if (msg.arg1 >= mOriginHeight) {
                    msg.arg1 = mOriginHeight;
                }

                mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, msg.arg1));
                if (msg.arg1 >= mOriginHeight) {
                    if (mWrapContent) {
                        //画面サイズより大きい場合に伸びきらない対応
                        mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    }

                    if (mListener != null) {
                        mListener.onExpanded(ExpandAnimator.this);
                    }

                    mAnimating = false;

                } else {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    Message m = this.obtainMessage(0);
                    m.arg1 = msg.arg1;
                    this.sendMessage(m);
                }
            }
        };
        Message msg = animationHandler.obtainMessage(0);
        msg.arg1 = (AppConfig.getInstance().dp);
        animationHandler.sendMessage(msg);
        if (mListener != null) {
            mListener.onStartExpand(this);
        }
    }
}
