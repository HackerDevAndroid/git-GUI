package lifetime.asynctask;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    EditText txtNumber;
    Button btnDraw;
    ProgressBar progressBarPercent;
    LinearLayout layoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processButtonRealTime();
            }
        });
    }

    private void processButtonRealTime() {
        int n=Integer.parseInt(txtNumber.getText().toString())
    }

    private void addControls() {
        txtNumber= (EditText) findViewById(R.id.txtNumber);
        btnDraw= (Button) findViewById(R.id.btnDraw);
        progressBarPercent= (ProgressBar) findViewById(R.id.progressBarPercent);
        layoutButton= (LinearLayout) findViewById(R.id.layoutButton);
    }
    class ButtonTask extends AsyncTask<Interger>
}
