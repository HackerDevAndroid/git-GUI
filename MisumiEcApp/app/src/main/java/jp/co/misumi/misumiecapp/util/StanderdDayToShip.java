package jp.co.misumi.misumiecapp.util;

import android.content.Context;

/**
 * Created by ost000422 on 2015/09/03.
 */
public class StanderdDayToShip {

    protected static String getResourceString(Context context, int id){
        return context.getString(id);
    }

    /**
     * standarddaytoship → TextViewすべて含んだレイアウト
     * mindaytoship → 通常出荷日：
     * day → 日目
     * daytilde → ～
     *
     * label_day_to_ship_0 → 当日出荷
     * label_day_to_ship_99 → 都度お見積もり
     */

//    public static String standerdDayToShip (Context context,View convertView, Integer mimStanderdDayToShip, Integer maxStanderdDayToShip) {;
//
//        String sMinDayToShip;
//
//        if (mimStanderdDayToShip == null || (mimStanderdDayToShip > 0 && mimStanderdDayToShip < 99)){
//        //最大のみ、例外 → 非表示
//            if (mimStanderdDayToShip == null) {
//                sMinDayToShip = "";
//                convertView.findViewById(R.id.standarddaytoship).setVisibility(View.GONE);
//            }else{
//
//                sMinDayToShip = mimStanderdDayToShip.toString();
//                convertView.findViewById(R.id.mindaytoship).setVisibility(View.VISIBLE);
//                convertView.findViewById(R.id.day).setVisibility(View.VISIBLE);
//            }
//        } else if (mimStanderdDayToShip == 0) {
//            //結果=0 → 当日出荷
//            sMinDayToShip = getResourceString(context, R.string.label_day_to_ship_0);//当日出荷
//            convertView.findViewById(R.id.mindaytoship).setVisibility(View.GONE);
//            convertView.findViewById(R.id.day).setVisibility(View.GONE);
//            convertView.findViewById(R.id.daytilde).setVisibility(View.GONE);
//        } else if (mimStanderdDayToShip == 99) {
//            //結果=99 → 都度お見積
//            sMinDayToShip = getResourceString(context, R.string.label_day_to_ship_99);//都度お見積もり
//            convertView.findViewById(R.id.mindaytoship).setVisibility(View.GONE);
//            convertView.findViewById(R.id.day).setVisibility(View.GONE);
//            convertView.findViewById(R.id.daytilde).setVisibility(View.GONE);
//        }else if (mimStanderdDayToShip == maxStanderdDayToShip) {
//            sMinDayToShip = mimStanderdDayToShip.toString();
//            convertView.findViewById(R.id.mindaytoship).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.day).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.daytilde).setVisibility(View.GONE);
//        }else {
//            sMinDayToShip = "";
//            convertView.findViewById(R.id.standarddaytoship).setVisibility(View.GONE);
//        }
//
//        return sMinDayToShip;
//    }
}
