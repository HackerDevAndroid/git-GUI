package jp.co.misumi.misumiecapp.util;

/**
 * Created by ost000422 on 2015/09/03.
 */
public class StanderdUnitPrice {

//    public static String standerdUnitPrice (Double minPrice, Double maxPrice, View convertView) {
//
//        Double mPrice;
//        String sPrice;
//
//        if (minPrice==null || minPrice==0) {
//            convertView.findViewById(R.id.price).setVisibility(View.GONE);
//            convertView.findViewById(R.id.yen).setVisibility(View.GONE);
//            convertView.findViewById(R.id.tilde).setVisibility(View.GONE);
//            convertView.findViewById(R.id.standardunitprice).setVisibility(View.GONE);
//            sPrice = "";
//        } else if ( minPrice.equals(maxPrice) || maxPrice==null){
//            mPrice = minPrice;
//            sPrice = Format.formatAmount(mPrice);
//            convertView.findViewById(R.id.tilde).setVisibility(View.GONE);
//        } else if (minPrice < maxPrice){
//            convertView.findViewById(R.id.price).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.yen).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.tilde).setVisibility(View.VISIBLE);
//            mPrice = minPrice;
//            sPrice = Format.formatAmount(mPrice);
//        } else {
//            convertView.findViewById(R.id.price).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.yen).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.tilde).setVisibility(View.VISIBLE);
//            mPrice = maxPrice;
//            sPrice = Format.formatAmount(mPrice);
//        }
//        return sPrice;
//    }
}
