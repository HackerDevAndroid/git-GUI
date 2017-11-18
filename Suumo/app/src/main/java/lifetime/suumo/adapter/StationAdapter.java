package lifetime.suumo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lifetime.suumo.R;
import lifetime.suumo.model.Station;

/**
 * Created by MyPC on 28/09/2017.
 */

public class StationAdapter extends ArrayAdapter<Station> {
    Activity context;
    @LayoutRes int resource;
     @NonNull List<Station> objects;
    public StationAdapter(Activity context, @LayoutRes int resource,  @NonNull List<Station> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=this.context.getLayoutInflater();
        View row=inflater.inflate(this.resource, null);


        Station station=this.objects.get(position);

        return row;
    }
}
