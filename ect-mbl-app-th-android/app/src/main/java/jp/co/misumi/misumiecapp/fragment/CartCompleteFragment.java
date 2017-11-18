package jp.co.misumi.misumiecapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.activity.SubActivity;
import jp.co.misumi.misumiecapp.data.DataContainer;


/**
 * カート完了画面（基本クラス）
 */
public abstract class CartCompleteFragment extends BaseFragment {


    private int mOpenType;
    private DataContainer mDataContainer;

    public CartCompleteFragment() {

    }

    protected boolean isFromOrder() {
        return (mOpenType == SubActivity.SUB_TYPE_ORDER);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //アプリ内で変更しないので保存不要
        mOpenType = getBundleData().getInt("openType");
        mDataContainer = (DataContainer) getBundleData().getSerializable("dataContainer");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflateLayout(inflater, getLayoutId(), container, false);

/*
        //デバグ用
		if (mDataContainer instanceof ResponseConfirmQuotation) {

			showToast("見積完了データが渡された");
		}

		if (mDataContainer instanceof ResponseConfirmOrder) {

			showToast("注文完了データが渡された");
		}
*/

        // 閉じる
        rootView.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFinish(Activity.RESULT_OK);
            }
        });


        makeDataView(rootView);


        return rootView;
    }


//    @Override
//    public boolean onBackKey()
//	{
//		//バックボタンは何もしない
//		return  true;
//	}

    @Override
    public void onHeaderEvent(int event, Objects objects) {
        super.onHeaderEvent(event, objects);

        //TODO:遷移元画面の内容を更新の通信処理

        //この画面のヘッダは閉じるだけなので IDで判別しなくても良い
        doFinish(Activity.RESULT_OK);
    }

    protected DataContainer getDataContainer() {

        return mDataContainer;
    }

    protected abstract int getLayoutId();

    protected abstract void makeDataView(View view);


    protected void setIncludeItemText(View subView, String str1, String str2, String str3) {
        ((TextView) subView.findViewById(R.id.textView1)).setText(str1);

        if (android.text.TextUtils.isEmpty(str2)) {
            str2 = getResourceString(R.string.label_hyphen);    //ハイフン化
        }

        ((TextView) subView.findViewById(R.id.textView2)).setText(str2);

        if (str3 == null) {
            subView.findViewById(R.id.textView3).setVisibility(View.GONE);
        } else {
            ((TextView) subView.findViewById(R.id.textView3)).setText(str3);
            subView.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
        }

    }


}


