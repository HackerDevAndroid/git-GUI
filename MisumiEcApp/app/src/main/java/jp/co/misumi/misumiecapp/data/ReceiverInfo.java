package jp.co.misumi.misumiecapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.misumi.misumiecapp.AppLog;


/**
 * 直送先
 */
public class ReceiverInfo extends DataContainer {

	public String receiverCode;			//　直送先コード
	public String receiverName;			//　直送先名(現地語)
	public String receiverNameEn;		//　直送先名(英字)
	public String postalCode;			//　郵便番号
	public String address1;				//　住所1(現地語)
	public String address2;				//　住所2(現地語)
	public String address3;				//　住所3(現地語)
	public String address4;				//　住所4(現地語)
	public String address1En;			//　住所1(カナ/英字)
	public String address2En;			//　住所2(カナ/英字)
	public String address3En;			//　住所3(カナ/英字)
	public String address4En;			//　住所4(カナ/英字)
	public String receiverUserName;		//　納入者氏名(現地語)
	public String receiverUserNameEn;	//　納入者氏名(カナ/英字)
	public String receiverDepartmentName;		//　納入者部課(現地語)
	public String receiverDepartmentNameEn;		//　納入者部課(カナ/英字)
	public String tel;					//　電話番号
	public String fax;					//　FAX番号

	//-- ADD NT-SLJ 17/07/13 3小时必达 FR –
	public String immediateDeliveryFlag;//即納配送対象フラグ 0: 対象外	1: 対象
	//-- ADD NT-SLJ 17/07/13 3小时必达 TO –


    boolean setData(JSONObject json) {

        try {

			//　直送先コード
			receiverCode = getJsonString(json, "receiverCode");

			//　直送先名(現地語)
			receiverName = getJsonString(json, "receiverName");

			//　直送先名(英字)
			receiverNameEn = getJsonString(json, "receiverNameEn");

			//　郵便番号
			postalCode = getJsonString(json, "postalCode");

			//　住所1(現地語)
			address1 = getJsonString(json, "address1");

			//　住所2(現地語)
			address2 = getJsonString(json, "address2");

			//　住所3(現地語)
			address3 = getJsonString(json, "address3");

			//　住所4(現地語)
			address4 = getJsonString(json, "address4");

			//　住所1(カナ/英字)
			address1En = getJsonString(json, "address1En");

			//　住所2(カナ/英字)
			address2En = getJsonString(json, "address2En");

			//　住所3(カナ/英字)
			address3En = getJsonString(json, "address3En");

			//　住所4(カナ/英字)
			address4En = getJsonString(json, "address4En");

			//　納入者氏名(現地語)
			receiverUserName = getJsonString(json, "receiverUserName");

			//　納入者氏名(カナ/英字)
			receiverUserNameEn = getJsonString(json, "receiverUserNameEn");

			//　納入者部課(現地語)
			receiverDepartmentName = getJsonString(json, "receiverDepartmentName");

			//　納入者部課(カナ/英字)
			receiverDepartmentNameEn = getJsonString(json, "receiverDepartmentNameEn");

			//　電話番号
			tel = getJsonString(json, "tel");

			//　FAX番号
			fax = getJsonString(json, "fax");

			//-- ADD NT-SLJ 17/07/13 3小时必达 FR –
			//即納配送対象フラグ 0: 対象外	1: 対象
			immediateDeliveryFlag = getJsonString(json, "immediateDeliveryFlag");
			//-- ADD NT-SLJ 17/07/13 3小时必达 TO –

        } catch (JSONException e) {

            AppLog.e(e);
            return false;
        }

		return true;
	}

}
