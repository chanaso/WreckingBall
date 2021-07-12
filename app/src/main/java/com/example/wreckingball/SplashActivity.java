package com.example.wreckingball;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity
{
    private static final int DELAY_TIME = 3; // seconds
    private int timeLeft;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBarID);

        startTimer();
    }

    private void startTimer()
    {
        timeLeft = DELAY_TIME;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(timeLeft >= 0)
                {
                    progressBar.setProgress(progressBar.getMax()-timeLeft);
                    SystemClock.sleep(1000); //Thread.sleep(1000);
                    timeLeft--;
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }).start();
    }
}

