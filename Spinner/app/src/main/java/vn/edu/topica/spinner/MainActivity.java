package vn.edu.topica.spinner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import model.Personnel;

public class MainActivity extends AppCompatActivity {
    EditText txtName,txtDay;
    Button btnSubmit;
    Spinner spDay;

    ArrayList<String>listDay;
    ArrayAdapter<String>adapterDay;

    int lastedSelected=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
        
    }

    private void addEvents() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processClick();
            }
        });
        spDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"You choose"+listDay.get(position),Toast.LENGTH_LONG).show();
                lastedSelected=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void processClick() {
        if(lastedSelected==-1)
        {
            Toast.makeText(MainActivity.this,"You don't choose anything",Toast.LENGTH_LONG).show();
            return;
        }
        Personnel ps=new Personnel();
        ps.setName(txtName.getText().toString());
        ps.setDateStart(listDay.get(lastedSelected));
        ps.setDayWorking(Integer.parseInt(txtDay.getText().toString()));
        Toast.makeText(MainActivity.this,ps.toString(),Toast.LENGTH_LONG).show();
    }

    private void addControls() {
        txtDay= (EditText) findViewById(R.id.txtDay);
        txtName= (EditText) findViewById(R.id.txtName);
        btnSubmit= (Button) findViewById(R.id.btnSubmit);
        spDay= (Spinner) findViewById(R.id.spDay);

        listDay=new ArrayList<>();
        listDay.add("Monday");
        listDay.add("Tuesday");
        listDay.add("Wenesday");listDay.add("Thursday");listDay.add("Friday");listDay.add("Saturday");listDay.add("Sunday");

        adapterDay=new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item,listDay
        );
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adapterDay);
    }
}
