package lifetime.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import lifetime.model.Restaurant;
import lifetime.noterestaurant.R;

/**
 * Created by MyPC on 02/10/2017.
 */

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
    Activity context;
    Restaurant restaurant;
    public CustomInfoAdapter(Activity context,Restaurant restaurant){
        this.context=context;
        this.restaurant=restaurant;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater=this.context.getLayoutInflater();
        View row=inflater.inflate(R.layout.item,null);
        ImageView imgPic=(ImageView) row.findViewById(R.id.imgPic);
        TextView txtname=(TextView) row.findViewById(R.id.txtname);

        imgPic.setImageResource(restaurant.getImage());
        txtname.setText(restaurant.getName());
        return row;
    }
}
