package vn.edu.topica.hoccackythuatxulysukien;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    EditText txtA, txtB;
    Button btnTru;

    Button btnNhan, btnChia;

    Button btnHidden;

    Button btnExit;

    View.OnClickListener suKienChiaSe=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
    }

    private void addControls() {
        txtA= (EditText) findViewById(R.id.txtA);
        txtB= (EditText) findViewById(R.id.txtB);
        btnTru= (Button) findViewById(R.id.btnTru);
        btnNhan= (Button) findViewById(R.id.btnNhan);
        btnChia= (Button) findViewById(R.id.btnChia);
        btnHidden= (Button) findViewById(R.id.btnHidden);
        btnExit= (Button) findViewById(R.id.btnExit);
    }

    private void addEvents() {
        btnTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyPhepTru();
            }
        });
        suKienChiaSe=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnNhan)
                {
                    xuLyPhepNhan();
                }
                else if (view.getId()==R.id.btnChia)
                {
                    xuLyPhepChia();
                }
            }
        };
        btnNhan.setOnClickListener(suKienChiaSe);
        btnChia.setOnClickListener(suKienChiaSe);

        btnHidden.setOnLongClickListener(this);
        btnExit.setOnClickListener(new MyEvent());
    }

    private void xuLyPhepChia() {
        int a=Integer.parseInt(txtA.getText().toString());
        int b=Integer.parseInt(txtB.getText().toString());
        int c=a/b;
        Toast.makeText(MainActivity.this, "Thương="+c, Toast.LENGTH_LONG).show();
    }

    private void xuLyPhepNhan() {
        int a=Integer.parseInt(txtA.getText().toString());
        int b=Integer.parseInt(txtB.getText().toString());
        int c=a*b;
        Toast.makeText(MainActivity.this, "Tích="+c, Toast.LENGTH_LONG).show();
    }

    private void xuLyPhepTru() {
        int a=Integer.parseInt(txtA.getText().toString());
        int b=Integer.parseInt(txtB.getText().toString());
        int c=a-b;
        Toast.makeText(MainActivity.this, "Hiệu="+c, Toast.LENGTH_LONG).show();
    }

    public void xuLyPhepCong(View v)
    {
        int a=Integer.parseInt(txtA.getText().toString());
        int b=Integer.parseInt(txtB.getText().toString());
        int c=a+b;
        Toast.makeText(MainActivity.this, "Tổng="+c, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onLongClick(View view) {
        if(view.getId()==R.id.btnHidden)
        {
            btnHidden.setVisibility(View.INVISIBLE);
        }
        return false;
    }
    public class MyEvent implements View.OnClickListener, View.OnLongClickListener
    {

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.btnExit)
            {
                finish();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
    public void switchScreen(View view)
    {
        Button btnNew=new Button(MainActivity.this)
        {
            @Override
            public boolean performClick() {
                setContentView(R.layout.activity_main);

                addControls();
                addEvents();
                return super.performClick();
            }
        };

        btnNew.setText("Quay Về");
        btnNew.setWidth(200);
        btnNew.setHeight(200);

        setContentView(btnNew);
    }
}
