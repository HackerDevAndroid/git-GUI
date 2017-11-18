//-- ADD NT-SLJ 16/11/11 AliPay Payment FR -
package jp.co.misumi.misumiecapp.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import jp.co.misumi.misumiecapp.data.Live800Data;

/**
 * Created date: 2016/11/11 15:23
 * Description: QuestionAdapter
 */
public class QuestionAdapter extends ArrayAdapter<Live800Data.Question> {
    public QuestionAdapter(Context context, int resource, List<Live800Data.Question> objects) {
        super(context, resource, objects);
    }

    public QuestionAdapter(Context context, int resource, int textViewResourceId, List<Live800Data.Question> objects) {
        super(context, resource, textViewResourceId, objects);
    }
}
//-- ADD NT-SLJ 16/11/11 AliPay Payment TO -
