package jp.co.misumi.misumiecapp.widget;

import android.view.LayoutInflater;
import android.view.View;

import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.util.ViewUtil;


/**
 * An enhanced {@code CheckBox} that differentiates between user clicks and
 * programmatic clicks. In particular, the {@code OnCheckedChangeListener} is
 * <strong>not</strong> triggered when the state of the checkbox is changed
 * programmatically.
 * 
 */
public class FooterView {

    private View mFooterView;
    private boolean mIsFooterError;

    private OnViewListener mListener;


	public interface OnViewListener {
	    void onReadList();
	}

	public FooterView(LayoutInflater inflater, OnViewListener listener) {

		mListener = listener;

        mFooterView = inflater.inflate(R.layout.list_item_footer_progress, null);
		ViewUtil.setSplitMotionEventsToAll(mFooterView);

		init();

        mFooterView.findViewById(R.id.textError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFooterView.findViewById(R.id.viewProgress).setVisibility(View.VISIBLE);
                mFooterView.findViewById(R.id.textError).setVisibility(View.GONE);
                mIsFooterError = false;

                    mListener.onReadList();
            }
        });
	}


	public void init() {

        mIsFooterError = false;

        mFooterView.findViewById(R.id.viewProgress).setVisibility(View.VISIBLE);
        mFooterView.findViewById(R.id.textError).setVisibility(View.GONE);
        mFooterView.findViewById(R.id.textOverItem).setVisibility(View.GONE);
	}

	public View getFooterView() {

        return mFooterView;
    }


    public void setFooterViewError() {

        mIsFooterError = true;

        mFooterView.findViewById(R.id.viewProgress).setVisibility(View.GONE);
        mFooterView.findViewById(R.id.textError).setVisibility(View.VISIBLE);
    }

    public void setFooterViewPc() {

        mFooterView.findViewById(R.id.viewProgress).setVisibility(View.GONE);
        mFooterView.findViewById(R.id.textError).setVisibility(View.GONE);
        mFooterView.findViewById(R.id.textOverItem).setVisibility(View.VISIBLE);
    }

    public boolean isFooterError() {

        return mIsFooterError;
    }

}

