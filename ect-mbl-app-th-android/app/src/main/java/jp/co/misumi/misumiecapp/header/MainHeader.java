package jp.co.misumi.misumiecapp.header;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.activity.AppActivity;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 * ヘッダ
 */
public class MainHeader extends HeaderView {

    private Button btnSearch;
    private Button btnBack;
    private TextView txtBadge;

    public static final int BACK_BUTTON = 1;
    public static final int SEARCH_BUTTON = 2;
    public static final int MENU_BUTTON = 3;
    public static final int MY_PARTS_BUTTON = 4;
    public static final int CART_BUTTON = 5;
    public static final int LOGO_AREA = 6;
    //--ADD NT-SLJ 16/11/12 Live800 FR -
    public static final int CONSULTATION_BUTTON = 7;
    private View btnConcult;
    //--ADD NT-SLJ 16/11/12 Live800 TO -
    private View layourtLogoArea;
    private View imageViewLogo;

    private Integer mCartCount = 0;
    //--ADD NT-LWL 17/05/18 QR scan FR -
    private View btnScan;  // 进入扫码按钮
    public static final int SCAN_BUTTON = 8; // 扫码按钮点击标识
    //--ADD NT-LWL 17/05/18 QR scan TO -

    /**
     * MainHeader
     *
     * @param activity
     */
    public MainHeader(AppActivity activity) {
        super(activity);


        View view = getHeaderView();
        this.hideHeader();

        //-- ADD NT-SLJ 16/11/12 Live800 FR -
        btnConcult = view.findViewById(R.id.buttonConsulting);
        imageViewLogo = view.findViewById(R.id.imageViewLogo);
        //中国环境 且已登录 才显示客服按钮
        if (!SubsidiaryCode.isJapan() && AppConfig.getInstance().hasSessionId()) {
            imageViewLogo.setVisibility(View.INVISIBLE);
            btnConcult.setVisibility(View.VISIBLE);
        } else {
            imageViewLogo.setVisibility(View.VISIBLE);
            btnConcult.setVisibility(View.GONE);
        }
        btnConcult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(CONSULTATION_BUTTON, null);
            }
        });
        //-- ADD NT-SLJ 16/11/12 Live800 TO -

        //--ADD NT-LWL 17/05/18 QR scan FR -
        //扫码按钮初始化
        btnScan = view.findViewById(R.id.scan);
        if (!SubsidiaryCode.isJapan()) {
            btnScan.setVisibility(View.VISIBLE);
        } else {
            btnScan.setVisibility(View.GONE);
        }
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(SCAN_BUTTON, null);
            }
        });
        //--ADD NT-LWL 17/05/18 QR scan TO -

        Button btnSideMenu = (Button) view.findViewById(R.id.buttonOpenMenu);
        btnSideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(MENU_BUTTON, null);
            }
        });

        btnSearch = (Button) view.findViewById(R.id.buttonSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(SEARCH_BUTTON, null);
            }
        });


        // 戻るボタン
        btnBack = (Button) view.findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(BACK_BUTTON, null);
            }
        });


        // カート件数バッヂ
        txtBadge = (TextView) view.findViewById(R.id.txtCartBadge);
        txtBadge.setVisibility(View.INVISIBLE);

        // カート
        view.findViewById(R.id.buttonCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(CART_BUTTON, null);
            }
        });

        // My部品表
        view.findViewById(R.id.buttonMyParts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(MY_PARTS_BUTTON, null);
            }
        });

        AppNotifier.getInstance().addListener(mAppNoticeListener,
                AppNotifier.CART_CHANGED | AppNotifier.CART_ADD_REQ | AppNotifier.USER_LOGOUT);

        //-- UPD NT-SLJ 16/11/12 Live800 FR -
//         view.findViewById(R.id.layourtLogoArea)setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendEvent(LOGO_AREA, null);
//            }
//        });
        layourtLogoArea = view.findViewById(R.id.layourtLogoArea);
        layourtLogoArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent(LOGO_AREA, null);
            }
        });
        //-- UPD NT-SLJ 16/11/12 Live800 TO -

        setLogoImage();

        if (BuildConfig.DebugMode) {
            AppNotifier.getInstance().addListener(new AppNotifier.AppNoticeListener() {
                @Override
                public void appNotice(AppNotifier.AppNotice notice) {
                    setLogoImage();
                }
            }, AppNotifier.LOCAL_SUBSIDIARY_REQ);
        }

    }

    public Integer addCartCount(Integer count) {
        return (mCartCount += count);
    }

    public void disableCartButton(boolean disable) {
        View view = getHeaderView();
        if (!disable) {
            view.findViewById(R.id.buttonCart).setBackgroundResource(R.drawable.header_cart_active);
        } else {
            view.findViewById(R.id.buttonCart).setBackgroundResource(R.drawable.header_cart_button_selector);
        }
    }

    public void disableMyPartButton(boolean disable) {
        View view = getHeaderView();
        if (!disable) {
            view.findViewById(R.id.buttonMyParts).setBackgroundResource(R.drawable.header_favorite_active);
        } else {
            view.findViewById(R.id.buttonMyParts).setBackgroundResource(R.drawable.header_myparts_button_selector);
        }
    }

    private void setLogoImage() {
        View view = getHeaderView();
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewLogo);
        switch (AppConst.subsidiaryCode) {
            case AppConst.SUBSIDIARY_CODE_CHN:
                imageView.setImageResource(R.drawable.header_logo_chn);
                break;
            case AppConst.SUBSIDIARY_CODE_MJP:
                imageView.setImageResource(R.drawable.header_logo_mjp);
                break;
        }
    }


    /**
     *
     */
    AppNotifier.AppNoticeListener mAppNoticeListener = new AppNotifier.AppNoticeListener() {
        @Override
        public void appNotice(AppNotifier.AppNotice notice) {
            switch (notice.event) {
                case AppNotifier.CART_CHANGED:
                    mCartCount = (Integer) notice.option;
                    updateCartCount();
                    break;
                case AppNotifier.CART_ADD_REQ:
                    mCartCount += (Integer) notice.option;
                    updateCartCount();
                    break;
                case AppNotifier.USER_LOGOUT:
                    txtBadge.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    /**
     * updateCartCount
     */
    private void updateCartCount() {
        AppLog.d("cart count = " + mCartCount);
        int visible = View.VISIBLE;
        if (mCartCount > 99) {
            txtBadge.setText("99");
        } else if (mCartCount < 1) {
            visible = View.INVISIBLE;
        } else {
            txtBadge.setText("" + mCartCount);
        }
        AppConfig.getInstance().setCartCount(mCartCount);
        txtBadge.setVisibility(visible);
    }


    /**
     * getHeaderViewResource
     *
     * @return
     */
    @Override
    protected int getHeaderViewResource() {
        return R.layout.header_view;
    }


    /**
     * showSearchButton
     *
     * @param show
     */
    public void showSearchButton(boolean show) {
        if (show) {
            if (btnSearch.getVisibility() != View.VISIBLE) {
                btnSearch.setVisibility(View.VISIBLE);
            }
            //-- ADD NT-SLJ 16/11/12 Live800 FR -
            if (!SubsidiaryCode.isJapan()) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layourtLogoArea.getLayoutParams();
                params.weight = 33;
            }
            //-- ADD NT-SLJ 16/11/12 Live800 TO -
        } else {
            if (btnSearch.getVisibility() != View.INVISIBLE) {
                btnSearch.setVisibility(View.INVISIBLE);
            }
            //-- ADD NT-SLJ 16/11/12 Live800 FR -
            if (!SubsidiaryCode.isJapan() && btnConcult.getVisibility() == View.VISIBLE) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layourtLogoArea.getLayoutParams();
                params.weight = 40;
                btnSearch.setVisibility(View.GONE);
            }
            //-- ADD NT-SLJ 16/11/12 Live800 TO -
        }
    }
    //-- ADD NT-SLJ 16/11/12 Live800 FR -

    /**
     * 控制客服按钮是否显示
     *
     * @param isShow
     */
    public void showConsultButton(boolean isShow) {
        if (isShow) {
            if (btnConcult.getVisibility() != View.VISIBLE) {
                btnConcult.setVisibility(View.VISIBLE);
            }
            if (imageViewLogo.getVisibility() != View.INVISIBLE) {
                imageViewLogo.setVisibility(View.INVISIBLE);
            }
            if (btnSearch.getVisibility() == View.INVISIBLE) {
                {
                    btnSearch.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layourtLogoArea.getLayoutParams();
                    params.weight = 40;
                }
            }
        } else {
            if (btnConcult.getVisibility() != View.GONE) {
                btnConcult.setVisibility(View.GONE);
            }
            if (imageViewLogo.getVisibility() != View.VISIBLE) {
                imageViewLogo.setVisibility(View.VISIBLE);
            }
            if (btnSearch.getVisibility() == View.GONE) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layourtLogoArea.getLayoutParams();
                params.weight = 33;
                btnSearch.setVisibility(View.INVISIBLE);
            }
        }
    }
    //-- ADD NT-SLJ 16/11/12 Live800 TO -

    /**
     * showBackButton
     *
     * @param show
     */
    public void showBackButton(boolean show) {
        if (show) {
            if (btnBack.getVisibility() != View.VISIBLE) {
                btnBack.setVisibility(View.VISIBLE);
            }
        } else {
            if (btnBack.getVisibility() != View.GONE) {
                btnBack.setVisibility(View.GONE);
            }
        }
    }

}
