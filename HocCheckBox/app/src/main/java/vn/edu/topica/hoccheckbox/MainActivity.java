package vn.edu.topica.hoccheckbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    CheckBox chkAndroid, chkWindow, chkIos;
    Button btnChoose;
    TextView txtSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
    }

    private void addEvents() {
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSubject();
            }
        });
    }

    private void chooseSubject() {
        String s="";
        if(chkAndroid.isChecked() )
        {
            s+=chkAndroid.getText().toString()+"\n";
        }
        if(chkWindow.isChecked())
        {
           s+=chkWindow.getText().toString()+"\n";
        }
        if(chkIos.isChecked())
        {
            s+=chkIos.getText().toString();
        }
        txtSub.setText(s);
    }

    private void addControls() {
        chkAndroid= (CheckBox) findViewById(R.id.chkAndroid);
        chkIos= (CheckBox) findViewById(R.id.chkIos);
        chkWindow= (CheckBox) findViewById(R.id.chkWinphone);
        btnChoose= (Button) findViewById(R.id.btnChoose);
        txtSub= (TextView) findViewById(R.id.txtSub);
    }
}
