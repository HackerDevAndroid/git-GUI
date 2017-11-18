package lifetime.androidfragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by MyPC on 02/11/2017.
 */

public class TopFragment extends Fragment {
    private EditText inputTopImageText;
    private EditText inputBottomImageText;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conainer, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.activity_top,conainer,false);

        inputTopImageText=view.findViewById(R.id.input_top_image_text);
        inputBottomImageText=view.findViewById(R.id.input_bottom_image_text);
        mainActivity=new MainActivity();

        Button applyButton= view.findViewById(R.id.button_apply);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                applyText();
            }

    });
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof MainActivity){
            this.mainActivity=(MainActivity) context;
        }
    }
    private void applyText(){
        String topText= this.inputTopImageText.getText().toString();
        String bottomText=this.inputBottomImageText.getText().toString();

        this.mainActivity.showText(topText,bottomText);
    }
}
