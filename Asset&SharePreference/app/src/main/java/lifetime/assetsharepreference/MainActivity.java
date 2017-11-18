package lifetime.assetsharepreference;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView txtFont;
    ListView lvFont;
    ArrayList<String>dsFont;
    ArrayAdapter<String>fontAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        lvFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changefont(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changefont(int i) {
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/"+dsFont.get(i));
        txtFont.setTypeface(typeface);
    }

    private void addControls() {
        txtFont= (TextView) findViewById(R.id.txtFont);
        lvFont= (ListView) findViewById(R.id.lvFont);
        dsFont=new ArrayList<>();
        fontAdapter=new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                dsFont);
        lvFont.setAdapter(fontAdapter);

        try {

            AssetManager assetManager = getAssets();
            String[] arrFontName = assetManager.list("font");
            dsFont.addAll(Arrays.asList(arrFontName));
            fontAdapter.notifyDataSetChanged();

        }
        catch (Exception ex)
        {
            Log.e("Font Error",ex.toString());
        }
    }
}
