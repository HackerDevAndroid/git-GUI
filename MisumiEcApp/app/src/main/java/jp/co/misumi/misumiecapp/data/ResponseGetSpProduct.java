package jp.co.misumi.misumiecapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * SP10_API外部設計書(スマホ用商品詳細API)_20150918
 */
public class ResponseGetSpProduct extends DataContainer {


	// Map series					//○シリーズ情報
	public boolean hasSeries;
	public String categoryCode;				//○カテゴリーコード
	public String seriesCode;				//○シリーズコード
	public String seriesName;				//○シリーズ名称
	public String brandCode;				//○ブランドコード
	public String brandName;				//○ブランド名称

	public String partNumber;				//型番
	public String innerCode;				//インナーコード
	public String completeType;				//確定タイプ

	public List<String> productImageUrlList = new ArrayList<>();	//○画像URL
	public String catchCopy;				//　キャッチコピー
	public Integer minStandardDaysToShip;	//　最小通常出荷日数
	public Integer maxStandardDaysToShip;	//　最大通常出荷日
	public Double minStandardUnitPrice;		//　最小通常価格
	public Double maxStandardUnitPrice;		//　最大通常価格
	public String recommendFlag;			//○おすすめフラグ
	public String gradeType;				//　グレードタイプ
	public String volumeDiscountFlag;		//○スライド値引きフラグ
	public String cValueFlag;				//○C-Valuフラグ
	public List<String> iconList = new ArrayList<>();			//○アイコンリスト
	public List<String> pictList = new ArrayList<>();			//○ピクトリスト
	public String campainEndDate;			//○キャンペーン終了日
	public String complexFlag;				//○複雑品フラグ

	// List selectedSpecList			//○指定中の仕様・寸法情報リスト
	public boolean hasSelectedSpecList;
    public ArrayList<StandardSpecInfo> mSelectedSpecList = new ArrayList<>();

	// List standardSpecList		//○基本情報リスト
	public boolean hasStandardSpecList;
    public ArrayList<StandardSpecInfo> mStandardSpecInfo = new ArrayList<>();

		//不要なのでパースしない
//		// List contactList				//○問い合わせ先情報

	// Map price					//　価格情報
	public boolean hasPrice;
    public PriceInfo mPrice = new PriceInfo();


	// Map partNumber				//　型番情報
	public boolean hasPartNumber;
    public PartNumber mPartNumber = new PartNumber();

    public ErrorList errorList;


	// List standardSpecList			//○基本情報リスト
    public static class StandardSpecInfo extends DataContainer {

		public String specName;					//○スペック名称
		public String specUnit;					//　スペック単位
		public String specValue;				//○スペック値

        boolean setData(JSONObject json) {
            try {

				//○スペック名称
				specName = getJsonString(json, "specName");

				//　スペック単位
				specUnit = getJsonString(json, "specUnit");

				//○スペック値
				specValue = getJsonString(json, "specValue");

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
	}


	// List specList				//○スペック項目リスト
    public static class SpecInfo extends DataContainer {

		public String specName;						//○名前
		public String specUnit;						//　単位

        boolean setData(JSONObject json) {
            try {

				//○名前
				specName = getJsonString(json, "specName");

				//　単位
				specUnit = getJsonString(json, "specUnit");

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
	}

	// List partNumberList			//○型番リスト
    public static class PartNumberInfo extends DataContainer {

		public String innerCode;				//○インナーコード
		public String partNumber;				//○型番候補(or 型番)
		public Double standardUnitPrice;		//　通常単価
		public Integer standardDaysToShip;		//　通常出荷日数
		public String volumeDiscountFlag;		//○スライド割引フラグ
		public String rohsFlag;					//○RoHSフラグ
		public Integer piecesPerPakage;			//　パック数量
		public List<String> specValueList = new ArrayList<>();		//○スペック項目値リスト

        boolean setData(JSONObject json) {
            try {

				//○インナーコード
				innerCode = getJsonString(json, "innerCode");

				//○型番候補(or 型番)
				partNumber = getJsonString(json, "partNumber");

				//　通常単価
				standardUnitPrice = getJsonDouble(json, "standardUnitPrice");

				//　通常出荷日数
				standardDaysToShip = getJsonInteger(json, "standardDaysToShip");

				//○スライド割引フラグ
				volumeDiscountFlag = getJsonString(json, "volumeDiscountFlag");

				//○RoHSフラグ
				rohsFlag = getJsonString(json, "rohsFlag");

				//　パック数量
				piecesPerPakage = getJsonInteger(json, "piecesPerPackage");

				//○スペック項目値リスト
                Object obj;
                specValueList = new ArrayList<>();
                JSONArray jsonArray = getJsonArray(json, "specValueList");
                for (int i=0; i<jsonArray.length(); i++){
                    obj = jsonArray.get(i);
                    specValueList.add((String)obj);
                }

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
	}


	// Map partNumber				//　型番情報
    public static class PartNumber extends DataContainer {

		public Integer totalCount;				//○総件数

		// List specList				//○スペック項目リスト
	    public ArrayList<SpecInfo> mSpecList = new ArrayList<>();

		// List partNumberList			//○型番リスト
	    public ArrayList<PartNumberInfo> mPartNumberList = new ArrayList<>();

        boolean setData(JSONObject json) {
            try {

				//○総件数
				totalCount = getJsonInteger(json, "totalCount");

				JSONArray jsonArray;

				// List specList				//○スペック項目リスト
				mSpecList.clear();
	            jsonArray = getJsonArray(json, "specList");
	            for (int ii = 0; ii < jsonArray.length(); ii++) {
	                SpecInfo itemInfo = new SpecInfo();
	                if (!itemInfo.setData(jsonArray.getJSONObject(ii))){
	                    return false;
	                } else {
	                    mSpecList.add(itemInfo);
	                }
	            }

				// List partNumberList			//○型番リスト
				mPartNumberList.clear();
	            jsonArray = getJsonArray(json, "partNumberList");
	            for (int ii = 0; ii < jsonArray.length(); ii++) {
	                PartNumberInfo itemInfo = new PartNumberInfo();
	                if (!itemInfo.setData(jsonArray.getJSONObject(ii))){
	                    return false;
	                } else {
	                    mPartNumberList.add(itemInfo);
	                }
	            }

            } catch (JSONException e) {

                AppLog.e(e);
                return false;
            }

            return true;
        }
	}


    public boolean setData(String src){

        try {
            JSONObject json = new JSONObject(src);

            // エラーリスト
            errorList = getErrorList(json);


			// Map series					//○シリーズ情報
			if (json.has("series")) {
				JSONObject jsonMap = json.getJSONObject("series");

				//○カテゴリーコード
				categoryCode = getJsonString(jsonMap, "categoryCode");

				//○シリーズコード
				seriesCode = getJsonString(jsonMap, "seriesCode");

				//○シリーズ名称
				seriesName = getJsonString(jsonMap, "seriesName");

				//○ブランドコード
				brandCode = getJsonString(jsonMap, "brandCode");

				//○ブランド名称
				brandName = getJsonString(jsonMap, "brandName");

				partNumber = getJsonString(jsonMap, "partNumber");
				innerCode = getJsonString(jsonMap, "innerCode");
				completeType = getJsonString(jsonMap, "completeType");

				//○画像URL
//				productImageUrlList = getJsonList<String>(jsonMap, "productImageUrlList");
                Object photUrl;
                productImageUrlList.clear();
                JSONArray productImageUrlListArray = getJsonArray(jsonMap, "productImageUrlList");
                for (int i=0; i<productImageUrlListArray.length(); i++){
					if (productImageUrlListArray.isNull(i)){
						continue;
					}
                    photUrl = productImageUrlListArray.get(i);
                    productImageUrlList.add((String)photUrl);
                }

				//　キャッチコピー
				catchCopy = getJsonString(jsonMap, "catchCopy");

				//　最小通常出荷日数
				minStandardDaysToShip = getJsonInteger(jsonMap, "minStandardDaysToShip");

				//　最大通常出荷日
				maxStandardDaysToShip = getJsonInteger(jsonMap, "maxStandardDaysToShip");

				//　最小通常価格
				minStandardUnitPrice = getJsonDouble(jsonMap, "minStandardUnitPrice");

				//　最大通常価格
				maxStandardUnitPrice = getJsonDouble(jsonMap, "maxStandardUnitPrice");

				//○おすすめフラグ
				recommendFlag = getJsonString(jsonMap, "recommendFlag");

				//　グレードタイプ
				gradeType = getJsonString(jsonMap, "gradeType");

				//○スライド値引きフラグ
				volumeDiscountFlag = getJsonString(jsonMap, "volumeDiscountFlag");

				//○C-Valuフラグ
				cValueFlag = getJsonString(jsonMap, "cValueFlag");


				//○アイコンリスト
                Object icon;
                iconList.clear();
                JSONArray iconListArray = getJsonArray(jsonMap, "iconList");
                for (int i=0; i<iconListArray.length(); i++){
                    icon = iconListArray.get(i);
                    iconList.add((String)icon);
                }

				//○ピクトリスト
                Object pict;
                pictList.clear();
                JSONArray pictListArray = getJsonArray(jsonMap, "pictList");
                for (int i=0; i<pictListArray.length(); i++){
                    pict = pictListArray.get(i);
                    pictList.add((String)pict);
                }

				//○キャンペーン終了日
				campainEndDate = getJsonString(jsonMap, "campaignEndDate");

				//○複雑品フラグ
				complexFlag = getJsonString(jsonMap, "complexFlag");

				// List selectedSpecList			//○指定中の仕様・寸法情報リスト
	            JSONArray selectedSpecList = getJsonArray(jsonMap, "selectedSpecList");
				mSelectedSpecList.clear();
	            for (int ii = 0; ii < selectedSpecList.length(); ii++) {
	                StandardSpecInfo itemInfo = new StandardSpecInfo();
	                if (!itemInfo.setData(selectedSpecList.getJSONObject(ii))){
	                    return false;
	                } else {
	                    mSelectedSpecList.add(itemInfo);
	                }
	            }

				// List standardSpecList			//○基本情報リスト
	            JSONArray standardSpecList = getJsonArray(jsonMap, "standardSpecList");
				mStandardSpecInfo.clear();
	            for (int ii = 0; ii < standardSpecList.length(); ii++) {
	                StandardSpecInfo itemInfo = new StandardSpecInfo();
	                if (!itemInfo.setData(standardSpecList.getJSONObject(ii))){
	                    return false;
	                } else {
	                    mStandardSpecInfo.add(itemInfo);
	                }
	            }

				hasSeries = true;
			}


			// Map price					//　価格情報
			if (json.has("price")) {
				JSONObject jsonMap = json.getJSONObject("price");

                if (!mPrice.setData(jsonMap)){
                    return false;
                }

				hasPrice = true;
			}


			// Map partNumber			//　型番情報
			if (json.has("partNumber")) {
				JSONObject jsonMap = json.getJSONObject("partNumber");

                if (!mPartNumber.setData(jsonMap)){
                    return false;
                }

				hasPartNumber = true;
			}

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

        return true;
    }

}

