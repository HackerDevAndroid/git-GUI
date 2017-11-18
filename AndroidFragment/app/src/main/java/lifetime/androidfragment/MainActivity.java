package lifetime.androidfragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Fragment BottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void showText(String topImageText, String bottomIamgaeText){
    BottomFragment bottomFragment= (BottomFragment) this.getSupportFragmentManager().findFragmentById(R.id.bottom_fragment);
    bottomFragment.showText(topImageText,bottomIamgaeText);
    }
}