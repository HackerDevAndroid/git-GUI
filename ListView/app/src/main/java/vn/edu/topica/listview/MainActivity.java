package vn.edu.topica.listview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    String []arrDay;
    ArrayAdapter<String>adapterDay;
    ListView lvDay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        lvDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "You choose ["+arrDay[i]+"]",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addControls() {
        arrDay=getResources().getStringArray(R.array.arrDay);
        adapterDay=new ArrayAdapter<String>(
            MainActivity.this,
            android.R.layout.simple_list_item_1,
                arrDay

        );
        lvDay= (ListView) findViewById(R.id.lvDay);
        lvDay.setAdapter(adapterDay);
    }
}
