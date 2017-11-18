package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * My部品表取得API用データ
 */
//TODO:エクセル、メンバ変数等を完全に定義する
public class ResponseGetMyComponents extends DataContainer {

    public String folderId;						//フォルダID
    public String folderName;					//フォルダ名

    public List<Folder> mFolderList = new ArrayList<>();
    public List<Product> mProductList = new ArrayList<>();

    public ErrorList errorList;

	//フォルダ
    public static class Folder extends DataContainer {

	    public String folderId;					//
	    public String folderName;				//

        boolean setData(JSONObject json) {

            try {
				//
				folderId		= getJsonString(json, "folderId");
				folderName		= getJsonString(json, "folderName");

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

			return true;
		}

	}


	//商品
    public static class Product extends DataContainer {

        public String componentId;     //ID
        public String partNumber;      //型番
        public String productName;     //商品名
        public String seriesCode;      //シリーズコード
        public String innerCode;       //インナーコード
        public String productPageUrl;  //商品ページURL
        public String productImageUrl; //商品画像URL
        public String brandCode;       //メーカーコード
        public String brandName;       //メーカー名
        public Integer quantity;       //数量
        public Double unitPrice;       //単価
        public Double standardUnitPrice; //カタログ単価
        public Double totalPriceWithTax;   //税込合計金額
        public String currencyCode;        //通貨コード
        public Integer daysToShip;         //出荷日数
        public String shipType;            //出荷区分
        public String expressType;         //ストーク
        public Integer piecesPerPackage;   //パック品入数
        public String productType;         //商品タイプ
        public String campaignEndDate;     //キャンペーン終了日
        public String updateDateTime;      //カート更新日時

        public boolean	expanded;						//UI操作用

        public ArrayList<VolumeDiscount> mVolumeDiscountList = new ArrayList<>();
        public ArrayList<Modula> componentItemList = new ArrayList<>();
	    public ErrorList errorList;

        boolean setData(JSONObject json) {

            try {

	            // エラーリスト
	            errorList = getErrorList(json);

                //ID
                componentId = getJsonString(json, "componentId");
                //型番
                partNumber = getJsonString(json, "partNumber");
                //商品名
                productName = getJsonString(json, "productName");
                //シリーズコード
                seriesCode = getJsonString(json, "seriesCode");
                //インナーコード
                innerCode = getJsonString(json, "innerCode");
                //商品ページURL
                productPageUrl = getJsonString(json, "productPageUrl");
                //商品画像URL
                productImageUrl = getJsonString(json, "productImageUrl");
                //メーカーコード
                brandCode = getJsonString(json, "brandCode");
                //メーカー名
                brandName = getJsonString(json, "brandName");
                //数量
                quantity = getJsonInteger(json, "quantity");
                //単価
                unitPrice = getJsonDouble(json, "unitPrice");
                //カタログ単価
                standardUnitPrice = getJsonDouble(json, "standardUnitPrice");
                //税込合計金額
                totalPriceWithTax = getJsonDouble(json, "totalPriceWithTax");
                //通貨コード
                currencyCode = getJsonString(json, "currencyCode");
                //出荷日数
                daysToShip = getJsonInteger(json, "daysToShip");
                //出荷区分
                shipType = getJsonString(json, "shipType");
                //ストーク
                expressType = getJsonString(json, "expressType");
                //パック品入数
                piecesPerPackage = getJsonInteger(json, "piecesPerPackage");
                //商品タイプ
                productType = getJsonString(json, "productType");
                //キャンペーン終了日
                campaignEndDate = getJsonString(json, "campaignEndDate");
                //カート更新日時
                updateDateTime = getJsonString(json, "updateDateTime");

        		expanded = false;

				//UI数量
				if (quantity == null) {
					//空欄時のデフォルト数量は 1にする（決定済み仕様）
					quantity = 1;
				}

                JSONArray discountList = getJsonArray(json, "volumeDiscountList");
                for (int ii = 0; ii < discountList.length(); ii++) {
                    JSONObject discountinfo = discountList.getJSONObject(ii);

                    VolumeDiscount vol = new VolumeDiscount();
                    if (vol.setData(discountinfo)) {
                        mVolumeDiscountList.add(vol);

                    } else {
                        return false;
                    }
                }


                JSONArray partList = getJsonArray(json, "componentItemList");
                for (int ii = 0; ii < partList.length(); ii++) {
                    JSONObject modulainfo = partList.getJSONObject(ii);

                    Modula modu = new Modula();
                    if (modu.setData(modulainfo)) {
                        componentItemList.add(modu);

                    } else {
                        return false;
                    }
                }

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

			return true;
		}

	}


	//モジュラ
    public static class Modula extends DataContainer {


	    public String partNumber;				//
	    public String productName;				//
        public Integer quantity;       //数量
        public Double unitPrice;       //単価

        boolean setData(JSONObject json) {

            try {
				//
				partNumber		= getJsonString(json, "partNumber");
				productName		= getJsonString(json, "productName");
                //数量
                quantity = getJsonInteger(json, "quantity");
                //単価
                unitPrice = getJsonDouble(json, "unitPrice");

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

			return true;
		}

	}


    public boolean setData(String src){

        if (!mFolderList.isEmpty()){
            mFolderList.clear();
        }

        if (!mProductList.isEmpty()){
            mProductList.clear();
        }

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);

            folderId = getJsonString(json, "folderId");

            folderName = getJsonString(json, "folderName");

			//
            JSONArray folderList = getJsonArray(json, "folderList");
            for (int ii = 0; ii < folderList.length(); ii++) {
                Folder folder = new Folder();
                if (!folder.setData(folderList.getJSONObject(ii))){
                    return false;
                } else {
                    mFolderList.add(folder);
                }
            }

			//
            JSONArray productList = getJsonArray(json, "componentList");
            for (int ii = 0; ii < productList.length(); ii++) {
                Product product = new Product();
                if (!product.setData(productList.getJSONObject(ii))){
                    return false;
                } else {
                    mProductList.add(product);
                }
            }
        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }
        return true;
	}

}
