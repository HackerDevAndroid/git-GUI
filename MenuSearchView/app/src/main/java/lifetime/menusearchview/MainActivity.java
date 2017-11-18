package lifetime.menusearchview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ListView lvDistrict;
    ArrayList<String>listDistrict;
    ArrayAdapter<String>adapterDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
    }

    private void addControls() {
        lvDistrict= (ListView) findViewById(R.id.lvDistrict);
        listDistrict=new ArrayList<>();
        listDistrict.addAll(Arrays.asList(getResources().getStringArray(R.array.arrDistrict)));
        adapterDistrict=new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                listDistrict);
        lvDistrict.setAdapter(adapterDistrict);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem menuSearch=menu.findItem(R.id.menuSearch);
        SearchView searchView= (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapterDistrict.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
