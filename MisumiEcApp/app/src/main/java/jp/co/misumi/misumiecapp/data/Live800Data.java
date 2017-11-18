//-- ADD NT-SLJ 16/11/11 AliPay Payment FR -
package jp.co.misumi.misumiecapp.data;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.AppLog;

/**
 * Created date: 2016/11/11 11:32
 * Description: Live800 consulting data.
 */
public class Live800Data extends DataContainer{
    public String prefixURL;
    public String tel;
    public List<Question> questionList=new ArrayList<>();

    public static class Question implements Serializable{
        public String question;
        public String companyID;
        public String configID;
        public String codeType;
        public String name;
        public String live800_ud_CellCD;

        @Override
        public String toString() {
            return question;
        }
    }

    /**
     * setData
     * @param src
     * @return
     */
    public boolean setData(String src){
        if (src == null || src.length() == 0){
            return false;
        }

        try {
            JSONObject object=new JSONObject(src);
            prefixURL=getJsonString(object,"prefixURL");
            tel=getJsonString(object,"tel");

            if (object.has("questionList")){
                JSONArray array = object.getJSONArray("questionList");
                int length=array.length();
                for (int i=0;i<length;i++){
                    Question question=new Question();
                    JSONObject json=array.getJSONObject(i);
                    question.question=getJsonString(json,"question");
                    question.companyID=getJsonString(json,"companyID");
                    question.configID=getJsonString(json,"configID");
                    question.codeType=getJsonString(json,"codeType");
                    question.name=getJsonString(json,"name");
                    String cellCD=getJsonString(json,"live800_ud_CellCD");
                    // Zetta CS Cell（FB11/FB12...），若取不到，直接用【NC】代替
                    //-- UDP NT-LWL 17/03/21 AliPay Payment FR -
                    // 将NC 改为 ECWS  17/05/04 要求还原NC
                    question.live800_ud_CellCD= TextUtils.isEmpty(cellCD)?"NC":cellCD;
//                    question.live800_ud_CellCD= TextUtils.isEmpty(cellCD)?"ECWOS":cellCD;
                    //-- UDP NT-LWL 17/03/21 AliPay Payment TO -
                    questionList.add(question);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AppLog.e(src + "\n" + e.getMessage());
            return false;
        }

        return true;
    }
}
//-- ADD NT-SLJ 16/11/11 AliPay Payment TO -
