package lifetime.noterestaurant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import lifetime.model.FakeRestaurant;
import lifetime.model.Restaurant;

public class MainActivity extends AppCompatActivity {

    ListView lvRestaurant;
    ArrayList<Restaurant>listRestaurant;
    ArrayAdapter<Restaurant>adapterRestaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
        
    }

    private void addEvents() {
        lvRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Restaurant restaurant=listRestaurant.get(i);

                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("RESTAURANT",restaurant);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        lvRestaurant= (ListView) findViewById(R.id.lvRestaurant);
        listRestaurant= FakeRestaurant.getList();
        adapterRestaurant=new ArrayAdapter<Restaurant>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                listRestaurant);
        lvRestaurant.setAdapter(adapterRestaurant);
    }
}
