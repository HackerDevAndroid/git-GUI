package lifetime.smssettinglayout;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    EditText txtPhone;
    Spinner spConfirm;
    ArrayList<String>listChoose;
    ArrayAdapter<String>adapterChoose;
    Button btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///addControls();
        addEvents();
    }

    private void addEvents() {

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnOk.isFocusable();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnOk.setVisibility(View.VISIBLE);

            }
        });
    }



   ///

    public void Confirm(View view) {
        DialogBasic dialogFragment = new DialogBasic();
        dialogFragment.show(getFragmentManager(), "");
    }
}
