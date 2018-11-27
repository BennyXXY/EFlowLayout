package com.android.eflowlayout.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.eflowlayout.EFlowLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] colors = {
                Color.DKGRAY,
                Color.GRAY,
                Color.LTGRAY,
                Color.WHITE,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,
                Color.CYAN,
                Color.MAGENTA,
                Color.TRANSPARENT
        };
        EFlowLayout viewById = (EFlowLayout) findViewById(R.id.fl);
        viewById.setItemMargin(10, 10, 10, 10);
        for (int i = 0; i < 1000; i++) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText("我是条目 " + i);
            int i1 = new Random().nextInt(colors.length);
            textView.setBackgroundColor(colors[i1]);
            viewById.addView(textView);
        }
    }
}
