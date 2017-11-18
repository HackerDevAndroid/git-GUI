package com.example.mypc.snake;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class SnakeActivity extends Activity {
    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display=getWindowManager().getDefaultDisplay();

        Point size=new Point();
        display.getSize(size);

        snakeEngine=new SnakeEngine(this, size);
        setContentView(R.layout.activity_snake);
    }
    @Override
    protected void onResume(){
        super.onResume();
        snakeEngine.resume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        snakeEngine.pause();
    }
}
