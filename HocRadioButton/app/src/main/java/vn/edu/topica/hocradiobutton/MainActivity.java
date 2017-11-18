package vn.edu.topica.hocradiobutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    RadioButton radVeryGood, radGood, radNor, radBad;
    Button btnVote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processVote();
            }
        });
    }

    private void processVote() {
        String s="";
        if(radVeryGood.isChecked())
        {
            s=radVeryGood.getText().toString();
        }
        else if(radGood.isChecked())
        {
            s=radGood.getText().toString();
        }
        else if(radNor.isChecked())
        {
            s=radNor.getText().toString();
        }
        else if(radBad.isChecked())
        {
            s=radBad.getText().toString();
        }
        Toast.makeText(MainActivity.this,"You choose"+" " +s, Toast.LENGTH_LONG).show();
    }

    private void addControls() {
        radVeryGood= (RadioButton) findViewById(R.id.radVeryGood);
        radGood= (RadioButton) findViewById(R.id.radGood);
        radNor= (RadioButton) findViewById(R.id.radNor);
        radBad= (RadioButton) findViewById(R.id.radBad);
        btnVote= (Button) findViewById(R.id.btnVote);
    }
}
