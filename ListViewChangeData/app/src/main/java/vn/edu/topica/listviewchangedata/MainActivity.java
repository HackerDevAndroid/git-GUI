package vn.edu.topica.listviewchangedata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String>arrName;
    ArrayAdapter<String>adapterName;
    ListView lvName;

    EditText txtName;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSave();
            }
        });
    }

    private void processSave() {
        String name=txtName.getText().toString();
        arrName.add(name);
        adapterName.notifyDataSetChanged();
        txtName.setText("");
        txtName.requestFocus();
    }

    private void addControls() {
        arrName=new ArrayList<String>();
        adapterName=new ArrayAdapter<String>(
            MainActivity.this,
            android.R.layout.simple_list_item_1,
            arrName);
            lvName= (ListView) findViewById(R.id.lvName);
            lvName.setAdapter(adapterName);
            txtName= (EditText) findViewById(R.id.txtName);
            btnSave= (Button) findViewById(R.id.btnSave);
        }
    }

