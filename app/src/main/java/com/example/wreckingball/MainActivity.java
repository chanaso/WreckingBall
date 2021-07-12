package com.example.wreckingball;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int currentApiVersion;

    private Button btnStart;
    private Button btnTops;

    private AlertDialog.Builder aboutDialog;
    private AlertDialog.Builder exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        // notification service for check 2 hours without playing
        startService(new Intent(this, NotificationService.class));

        btnStart = findViewById(R.id.btnStartID);
        btnTops = findViewById(R.id.btnTopsID);

        btnStart.setOnClickListener(this);
        btnTops.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnStartID:
                Intent intent_start = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent_start);
                break;

            case R.id.btnTopsID:
                Intent intent_inst = new Intent(MainActivity.this, TopsActivity.class);
                startActivity(intent_inst);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setIcon(R.drawable.icon);
        aboutDialog.setTitle("About Wrecking Ball");
        aboutDialog.setMessage("This app is the Wrecking Ball game.\n\nBy Michal & Hanni (C) 2019");
        aboutDialog.setOnCancelListener(null);

        exitDialog = new AlertDialog.Builder(this);
        exitDialog.setIcon(R.drawable.icon);
        exitDialog.setTitle("Exit Wrecking Ball");
        exitDialog.setMessage("Do you really want to exit?");
        exitDialog.setCancelable(false);
        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();  // destroy this activity
            }
        });
        exitDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){}
        });

        MenuItem instrucItem = menu.add("Instructions");
        MenuItem aboutItem = menu.add("About");
        MenuItem exitItem = menu.add("Exit");
        instrucItem.setOnMenuItemClickListener(new  MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Intent intent_start = new Intent(MainActivity.this, InstructionsActivity.class);
                startActivity(intent_start);
                return true;
            }
        });
        aboutItem.setOnMenuItemClickListener(new  MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                aboutDialog.show();
                return true;
            }
        });
        exitItem.setOnMenuItemClickListener(new  MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                exitDialog.show();
                return true;
            }
        });
        return true;
    }
}

