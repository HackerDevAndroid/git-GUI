package jp.co.misumi.misumiecapp.header;

import android.view.View;
import android.widget.ImageView;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.activity.AppActivity;


/**
 * ヘッダ
 */
public class SubHeader extends HeaderView {

	public static final int CLOSE = 10;

    public SubHeader(AppActivity activity){
        super(activity);

        View view = getHeaderView();

		View	buttonClose	= view.findViewById(R.id.buttonClose);
		if (buttonClose != null) {
	        buttonClose.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
					sendEvent(CLOSE,null);
	            }
	        });
		}

		setLogoImage();
    }

	private void setLogoImage(){
		View view = getHeaderView();
		ImageView imageView = (ImageView) view.findViewById(R.id.imageViewLogo);
		switch (AppConst.subsidiaryCode){
			case AppConst.SUBSIDIARY_CODE_CHN:
				imageView.setImageResource(R.drawable.header_logo_chn);
				break;
			case AppConst.SUBSIDIARY_CODE_MJP:
				imageView.setImageResource(R.drawable.header_logo_mjp);
				break;
		}
	}


    @Override
    protected int getHeaderViewResource() {
        return R.layout.header_view_sub;
    }


}
