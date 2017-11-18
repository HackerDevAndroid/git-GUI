package lifetime.model;

import java.util.ArrayList;

import lifetime.noterestaurant.R;

/**
 * Created by MyPC on 02/10/2017.
 */

public class FakeRestaurant {
    public static ArrayList<Restaurant> getList()
    {
        ArrayList<Restaurant> list = new ArrayList<>();
        list.add(new Restaurant("Restaurant Bach Kim", R.drawable.bachkim,10.7829217,106.6404468));
        list.add(new Restaurant("Restaurant Huong Pho", R.drawable.huongpho,10.8287109,106.6812328));
        list.add(new Restaurant("Restaurant Dong Phuong", R.drawable.dongphuong,10.8358561,106.6172075));
        return list;
    }
}
