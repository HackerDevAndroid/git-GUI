package jp.co.misumi.misumiecapp.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.MessageDialog;
import jp.co.misumi.misumiecapp.NetworkInterface;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.activity.LoginActivity;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.data.AddToCart;
import jp.co.misumi.misumiecapp.data.CategoryList;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.ResponseGetSpProduct;
import jp.co.misumi.misumiecapp.data.ResponseKeywordSearch;
import jp.co.misumi.misumiecapp.data.SearchSeriesList;
import jp.co.misumi.misumiecapp.fragment.BaseFragment;
import jp.co.misumi.misumiecapp.fragment.CategorySearchListFragment;
import jp.co.misumi.misumiecapp.fragment.ItemDetailFragment;
import jp.co.misumi.misumiecapp.fragment.SearchResultCategoryFragment;
import jp.co.misumi.misumiecapp.fragment.SearchResultKeywordFragment;
import jp.co.misumi.misumiecapp.fragment.TopFragment;
import jp.co.misumi.misumiecapp.util.CategoryQRUtils;
import jp.co.misumi.misumiecapp.util.ShareUtil;


/**
 * スキーマ起動の共通通信クラス
 */
public abstract class SchemeApi {


	//API
    private GetSpProductApi mGetSpProductApi;
    private SeriesSearchApi mSeriesSearchApi;
    private CategorySearchApi mCategorySearchApi;
	private Context mContext;
    //--ADD NT-LWL 17/05/19 Share FR -
    // 进入详情 登录后询价 先临时保存参数
    public String scode = "";
    private String pcode = "";
    private String ctype = ""; // 分享内容点击是 判断是否询价
    private String number = "" ;// 分享商品数量
    private boolean isFromShare = false; //是否来自分享
    private String sessionId; // sessionId

    // 打开分享统计api
    private OpenApi mOpenApi;

    //-- ADD NT-LWL 17/09/27 Category FR -
    // 新旧CategoryCodeMap
    private HashMap<String,String> categoryQRMap;
    //-- ADD NT-LWL 17/09/27 Category TO -

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    //--ADD NT-LWL 17/05/19 Share TO -

    public SchemeApi(Context context) {

		mContext = context;

		//API
        mGetSpProductApi = new GetSpProductApi();
        mSeriesSearchApi = new SeriesSearchApi();
        mCategorySearchApi = new CategorySearchApi();
        //--ADD NT-LWL 17/06/26 QR scan FR -
        mOpenApi = new OpenApi();
        //--ADD NT-LWL 17/06/26 QR scan TO -
        //-- ADD NT-LWL 17/09/27 Category FR -
        categoryQRMap = CategoryQRUtils.readCategoryQRData(mContext);
        //-- ADD NT-LWL 17/09/27 Category TO -
    }


    private Context getContext() {
		return mContext;
	}

    public void onCreate(Uri uri) {


		//スキーマ起動のパラメータ
		String sname = "";
		String ccode = "";
		String scode = "";
		String pcode = "";
        //--ADD NT-LWL 17/05/19 Share FR -
        String ctype = ""; // 分享内容点击是 判断是否询价
        String number = "";// 分享商品数量
        sessionId = AppConfig.getInstance().getSessionId();

        //--ADD NT-LWL 17/05/19 Share TO -

		//TODO:スキーマ起動で起動画面を判別
//		        Uri uri = getParameterUri();
		try {
	    	if (uri!=null) {
                checkQRcodeIsOk(uri);
				//MISUMI_MOBILE_APP-426
				if (uri.isHierarchical()) {

		    		sname = uri.getQueryParameter("sname");
		    		ccode = uri.getQueryParameter("ccode");
                    //-- ADD NT-LWL 17/09/27 Category FR -
                    if (!TextUtils.isEmpty(ccode)){
                        if (categoryQRMap == null || categoryQRMap.isEmpty()) {
                            categoryQRMap = CategoryQRUtils.readCategoryQRData(mContext);
                        }
                        // 替换旧code
                        if (categoryQRMap.containsKey(ccode)){
                            ccode = categoryQRMap.get(ccode);
                        }
                    }
                    //-- ADD NT-LWL 17/09/27 Category TO -
		    		scode = uri.getQueryParameter("scode");
		    		pcode = uri.getQueryParameter("pcode");
                    //--ADD NT-LWL 17/05/19 Share FR -
                    ctype = uri.getQueryParameter("ctype");
                    number = uri.getQueryParameter("number");
                    //--ADD NT-LWL 17/05/19 Share TO -
		        }
	        }
		} catch (Exception e) {

			sname = "";
			ccode = "";
			scode = "";
			pcode = "";
            //--ADD NT-LWL 17/05/19 Share FR -
            ctype = "";
            number = "";
            //--ADD NT-LWL 17/05/19 Share TO -
		}
//--UDP NT-LWL 17/05/18 QR scan FR -
		//スキーマの引数で実行する処理を振り分けする
//		if ("scate".equals(sname)) {
//
//			doCategorySearch(ccode);
//		} else if ("slist".equals(sname)) {
//
//    		doSearchSeries(ccode);
//		} else if ("detail".equals(sname)) {
//
//			doSpProductSub(scode, pcode);
//		} else {
//
//			//それ以外はトップ画面
//			doNextScreen(null);
//		}

// QR扫码/分享点击后处理逻辑
        if (!TextUtils.isEmpty(ccode)){

            // 分类检索API
            doCategorySearch(ccode);
        }else if (!TextUtils.isEmpty(scode)){

            // 调用商品详情API
            doSpProductSub(scode, pcode,ctype,number);
        }else {
            //それ以外はトップ画面
			doNextScreen(null);
        }
//--UDP NT-LWL 17/05/18 QR scan TO -
	}


    public void onPause() {

        mGetSpProductApi.close();
        mSeriesSearchApi.close();
        mCategorySearchApi.close();
    }


	//
    protected abstract void doNextScreen(DataContainer dataContainer);


    protected BaseFragment getNextFragment(DataContainer dataContainer) {

		//スキーマ起動でデータ種別で起動画面を判別
		BaseFragment nextFragment = null;

		if (dataContainer != null) {

			//スキーマ起動
			if (dataContainer instanceof ResponseGetSpProduct) {

				nextFragment = new ItemDetailFragment();

			} else if (dataContainer instanceof CategoryList.Category) {

				nextFragment = new CategorySearchListFragment();

			} else if (dataContainer instanceof SearchSeriesList) {

				nextFragment = new SearchResultCategoryFragment();

			}

		}

		if (nextFragment == null) {

			//それ以外はトップ画面
			nextFragment = new TopFragment();

		}

		return nextFragment;
    }


	//
	private void doSpProductSub(String scode, String pcode) {

        String completeType = "1";
//        String innerCode = null;
        Integer quantity = 1;

        if (!android.text.TextUtils.isEmpty(pcode)) {
        	completeType = "4";
		}

        mGetSpProductApi.setParameter(completeType, scode, null, pcode, quantity);
        mGetSpProductApi.connect(getContext());
	}

    /**
     * 调用详情API
     * @param scode 系列代码
     * @param pcode 商品型番
     * @param ctype 是否询价 4 表示询价(只有分享时传)
     * @param number 商品数量 (只有分享时传)
     */
    //--ADD NT-LWL 17/05/19 Share FR -
    private void doSpProductSub(String scode, String pcode,String ctype,String number) {

        String completeType = "1";
//        String innerCode = null;
        Integer quantity = 1;
        if (!TextUtils.isEmpty(pcode)&&!"null".equals(pcode)){

            // 扫码/分享时候 如果有型号 completeType=4
            completeType = "4";
        }

        if (completeType.equals("4")){
            // 暂存询价参数
            this.ctype = completeType;
            this.scode = scode;
            this.pcode = pcode;
            this.number = number;
            mGetSpProductApi.isCheckPrice = true;
        }
        isFromShare = false;

        // 分享处理逻辑 ctype 的值可能为1,2,3,4,5   5为选型已登录
        if (!TextUtils.isEmpty(ctype)&&!"null".equals(ctype)) {
            isFromShare = true;
            if (!TextUtils.isEmpty(number) && !"null".equals(number) && !number.equals("unconfirmed")) {
                try {
                    quantity = Integer.valueOf(number);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // 选型已登录有价格时分享 需要判断本地有无sessionId
            if (ctype.equals("5")){
                this.ctype = "4";
                if (AppConfig.getInstance().hasSessionId()){
                    mGetSpProductApi.setParameter(completeType, scode, null, pcode, quantity);
                    mGetSpProductApi.connect(getContext());
                }else {
//                    LoginFragment fragment = new LoginFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(LoginFragment.PARAM_MODE,LoginFragment.MODE_CHECK_PRICE);
//                    fragment.setArguments(bundle);
//                    fragment.show(((MainActivity)mContext).getSupportFragmentManager(),null);
                    LoginActivity.launchActivity(mContext, LoginActivity.MODE_CHECK_PRICE);
                }
            } else {
                completeType = ctype;
                this.ctype = ctype;
                // 未选型和选型未登录时 通过请求API自动判断
                mGetSpProductApi.setParameter(completeType, scode, null, pcode, quantity);
                mGetSpProductApi.connect(getContext());
            }

            return;// 分享处理结束
        }

        mGetSpProductApi.setParameter(completeType, scode, null, pcode, quantity);
        mGetSpProductApi.connect(getContext());

    }
    // 需要登录询价时 调用
    public void checkPrice(){
        doSpProductSub(scode,pcode,ctype,number);
    }
    // 设置打开分享uuid
    public void setOpenShareApiUuid(String uuid){
        if (!TextUtils.isEmpty(uuid)){
            mOpenApi.UUID = uuid;
//            mOpenApi.connect(getContext(),false);
        }
    }
    // 上传打开分享记录
    public void openShareApi(){
        mOpenApi.connect(mContext,false);
    }
    /**
     * 判断二维码是否合法
     * @param uri1
     */
    public void checkQRcodeIsOk(Uri uri1){
        String uuid = uri1.getQueryParameter("UUID");

        // 来自分享不处理
        if (!TextUtils.isEmpty(uuid)){
            return;
        }
        String url = uri1.toString();
        if (url.contains("ccode")||url.contains("scode")){

            Uri uri =  Uri.parse(url);
            String ccode = uri.getQueryParameter("ccode");
            String scode = uri.getQueryParameter("scode");

            if (TextUtils.isEmpty(ccode)&&TextUtils.isEmpty(scode)){
                new MessageDialog(mContext,null).show(R.string.QR_error,R.string.dialog_button_close);
            }else {
//                mAppSchemeApi.onCreate(uri);
            }
        }else {
            new MessageDialog(mContext,null).show(R.string.QR_error,R.string.dialog_button_close);
        }
    }
    //--ADD NT-LWL 17/05/19 Share TO -

    protected class GetSpProductApi extends ApiAccessWrapper {

        String mCompleteType;
        String mSeriesCode;
        String mInnerCode;
        String mPartNumber;
        Integer mQuantity;
        //--ADD NT-LWL 17/05/20 Share FR -
        boolean isCheckPrice = false; // 是否需要自动询价
        //--ADD NT-LWL 17/05/20 Share TO -

        public void setParameter(String completeType, String seriesCode, String innerCode, String partNumber, Integer quantity) {

            mCompleteType = completeType;
            mSeriesCode = seriesCode;
            mInnerCode = innerCode;
            mPartNumber = partNumber;
            mQuantity = quantity;
        }


		//派生先で実装する
        @Override
        protected String getScreenId() {

			//TODO:ScreenIdが暫定
	        return ScreenId.ItemDetail;
        }


        @Override
        protected boolean getMethod() {

            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {

            Integer page = 1;
            Integer pageSize = AppConst.PART_NUMBER_LIST_REQUEST_COUNT + 1;
            //--UDP NT-LWL 17/06/23 Share FR -
            //return ApiBuilder.createGetSpProduct(mCompleteType, mSeriesCode, mInnerCode, mPartNumber, mQuantity, page, pageSize);
            return ApiBuilder.createGetSpProduct(mCompleteType, mSeriesCode, mInnerCode, mPartNumber, mQuantity, page, pageSize,sessionId);
            //--UDP NT-LWL 17/06/23 Share FR -
        }

        @Override
        public void onResult(int responseCode, String result) {

            ResponseGetSpProduct response = new ResponseGetSpProduct();
            boolean pars = response.setData(result);
            if (!pars){

				//TODO:エラーダイアログのボタンをリトライと終了に変更する
                showErrorMessage(null);
                return;
            }

            switch (responseCode) {
                case NetworkInterface.STATUS_OK:

					//画面遷移
					doNextScreen(response);

                    //--ADD NT-LWL 17/07/20 Share FR -
                    // 判断来自分享
                    if (isFromShare) {
                        // 添加谁打开分享统计
                        mOpenApi.connect(mContext, false);
                    }
                    //--ADD NT-LWL 17/07/20 Share TO -
                    break;

                default:
                    //--UDP NT-LWL 17/06/23 Share FR -
					//TODO:エラーダイアログのボタンをリトライと終了に変更する
//                    showErrorMessage(response.errorList);
                    if (isFromShare){
                        showErrorMessage(response.errorList);
                    }else {
                        // 扫码失败的时候 显示未找到商品画面
                        BaseFragment nextFragment = new SearchResultKeywordFragment();
                        MainActivity mainActivity = (MainActivity) mContext;
                        mainActivity.getFragmentController().stackFragment(nextFragment, FragmentController.ANIMATION_SLIDE_IN, new ResponseKeywordSearch());
                    }
                    //--UDP NT-LWL 17/06/23 Share TO -
                    break;
            }
        }

        @Override
        protected void onNetworkError(int responseCode){

            super.onNetworkError(responseCode);
        }

        @Override
        protected void onTimeout(){

            super.onTimeout();
        }

		//セッション切れ
		@Override
		protected void onLostSession(int responseCode, String result) {
            //--UDP NT-LWL 17/05/20 Share FR -
//            AddToCart response = new AddToCart();
//            if (android.text.TextUtils.isEmpty(result) || !response.setData(result)){
//				showMessageDialog(R.string.message_session_scheme_error);
//                return;
//            }
//
//			showErrorMessage(response.errorList);
            // 弹出登录提示框
            LoginActivity.launchActivity(mContext,LoginActivity.MODE_CHECK_PRICE);
            //--UDP NT-LWL 17/05/20 Share TO -
		}

        @Override
		protected MessageDialog.MessageDialogListener getMessageDialogListener() {

			return SchemeApi.this.getMessageDialogListener();
		}
    }


	/**
	 * doCategorySearch
	 */
    private void doCategorySearch(String categoryCode){

        mCategorySearchApi.connect(getContext(), categoryCode);
	}

	/**
	 * カテゴリ検索
	 */
	private class CategorySearchApi extends ApiAccessWrapper {
        String mCategoryCode;

		@Override
		protected String getScreenId() {

			//TODO:ScreenIdが暫定
	        return ScreenId.SearchResult;
		}

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

		@Override
		public HashMap<String, String> getParameter() {
			return ApiBuilder.createSearchCategory(mCategoryCode, null, false);
		}

        public void connect(Context context, String categoryCode){
            mCategoryCode = categoryCode;
            super.connect(context);
        }

		@Override
		public void onResult(int responseCode, String result) {

			CategoryList categorylist = new CategoryList();
			if (!categorylist.setData(result)){

				//TODO:エラーダイアログのボタンをリトライと終了に変更する
				showErrorMessage(null);
				return;
			}

			if (responseCode == NetworkInterface.STATUS_OK) {

				//【08.カテゴリ一覧】【Android】カテゴリリストが空リストの場合に、アプリが強制終了する
				CategoryList.Category cate;
                //--UDP NT-LWL 17/05/18 QR scan FR -
				if (categorylist.categoryList.isEmpty()||categorylist.categoryList.get(0).categoryList==null||categorylist.categoryList.get(0).categoryList.isEmpty()) {
					cate = new CategoryList.Category();
					cate.categoryName = "";
                    // 分类检索结果为0时 调用商品系列API
                    doSearchSeries(mCategoryCode);
				} else {
					cate = categorylist.categoryList.get(0);
                    // 分类检索结果不为0时 直接进行画面跳转
                    doNextScreen(cate);
				}

				//画面遷移
				//doNextScreen(cate);
                //--UDP NT-LWL 17/05/18 QR scan TO -

			} else {

				//TODO:エラーダイアログのボタンをリトライと終了に変更する
				showErrorMessage(categorylist.errorList);
			}
		}

		//セッション切れ
		@Override
		protected void onLostSession(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (android.text.TextUtils.isEmpty(result) || !response.setData(result)){
				showMessageDialog(R.string.message_session_scheme_error);
                return;
            }

			showErrorMessage(response.errorList);
		}

        @Override
		protected MessageDialog.MessageDialogListener getMessageDialogListener() {

			return SchemeApi.this.getMessageDialogListener();
		}
	}


    /**
     * シリーズ検索
     */
    private void doSearchSeries(String categoryCode){

        mSeriesSearchApi.connect(getContext(), categoryCode);
    }

    private class SeriesSearchApi extends ApiAccessWrapper {
        String mCategoryCode;

        @Override
        protected String getScreenId() {

			//TODO:ScreenIdが暫定
            return ScreenId.CategorySub;
        }

        @Override
        protected boolean getMethod() {
            return API_GET;
        }

        @Override
        public HashMap<String, String> getParameter() {
            Integer page = 1;
            Integer pageSize = AppConst.SERIES_LIST_REQUEST_COUNT;
            return ApiBuilder.createGetSeries(mCategoryCode, page, pageSize);
        }

        public void connect(Context context, String categoryCode){
            mCategoryCode = categoryCode;
            super.connect(context);
        }

        @Override
        public void  onResult(int responseCode, String result) {

            SearchSeriesList response = new SearchSeriesList();
            boolean pars = response.setData(result);
            if (!pars){

				//TODO:エラーダイアログのボタンをリトライと終了に変更する
                showErrorMessage(null);
                return;
            }

            switch (responseCode){
                case NetworkInterface.STATUS_OK:

					//カテゴリ名称を自前で代入
                    response.getCategoryName = "";

			        if ((response.totalCount != null && response.totalCount != 0 )&&
			                (response.mSeriesList != null && !(response.mSeriesList.isEmpty()))) {

	                    response.getCategoryName = response.mSeriesList.get(0).categoryName;
					}

					//画面遷移
					doNextScreen(response);
                    break;
                default:

					//TODO:エラーダイアログのボタンをリトライと終了に変更する
                    showErrorMessage(response.errorList);
                    break;
            }
        }

		//セッション切れ
		@Override
		protected void onLostSession(int responseCode, String result) {

            AddToCart response = new AddToCart();
            if (android.text.TextUtils.isEmpty(result) || !response.setData(result)){
				showMessageDialog(R.string.message_session_scheme_error);
                return;
            }

			showErrorMessage(response.errorList);
		}

        @Override
		protected MessageDialog.MessageDialogListener getMessageDialogListener() {

			return SchemeApi.this.getMessageDialogListener();
		}
    }

	protected abstract MessageDialog.MessageDialogListener getMessageDialogListener();

    private class OpenApi extends ApiAccessWrapper {
        String UUID; // uuid 分享标识
        @Override
        protected String getScreenId() {
            return getScreenId();
        }

        @Override
        public HashMap<String, String> getParameter() {
            String url = ShareUtil.SHARE_URL+"OpenShareServlet";

            StringBuilder body = new StringBuilder();

            body.append("UUID=");
            body.append(UUID);
            body.append("&userCode=");
            body.append(AppConfig.getInstance().getUserCode());
            body.append("&customerCode=");
            body.append(AppConfig.getInstance().getCustomerCode());

            HashMap<String, String> result = new HashMap<>();
            result.put(ApiBuilder.URL, url);
            result.put(ApiBuilder.BODY, body.toString());
            return result;
        }

        @Override
        public void onResult(int responseCode, String result) {

        }

        @Override
        protected void onLostSession(int responseCode, String result) {
//            super.onLostSession(responseCode, result);
        }

        @Override
        protected void onNetworkError(int responseCode) {
//            super.onNetworkError(responseCode);
        }

        @Override
        protected void onTimeout() {
//            super.onTimeout();
        }

        @Override
        protected void onEndConnect() {
//            super.onEndConnect();
        }

        @Override
        protected void onStartConnect() {
//            super.onStartConnect();
        }
    }

}

