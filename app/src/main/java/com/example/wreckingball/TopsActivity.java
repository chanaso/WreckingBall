package com.example.wreckingball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TopsActivity extends AppCompatActivity {

    private TextView txtTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tops);

        txtTop = findViewById(R.id.txtTopsID);

        // get top score from scoreFile
        SharedPreferences sp = getSharedPreferences("scoreFile", Context.MODE_PRIVATE);
        int score = sp.getInt("score", 0);

        txtTop.setText(score+"");
    }

}
