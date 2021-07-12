package com.example.wreckingball;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

 public class BatteryReceiver extends BroadcastReceiver {

    private static int LOW_BATTERY = 30;
    private AlertDialog.Builder batteryDialog;
    private boolean is_shown;

	public BatteryReceiver() {
	    is_shown = false;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int level = intent.getIntExtra("level", 0);

        if(!is_shown && level <= LOW_BATTERY && level != 0)
        {
            batteryDialog = new AlertDialog.Builder(context);
            batteryDialog.setIcon(R.drawable.icon);
            batteryDialog.setTitle("Wrecking Ball" );
            batteryDialog.setMessage("Your Battery is going low\n\n" + level + "% remaining");
            batteryDialog.setOnCancelListener(null);
            batteryDialog.show();
            is_shown = true;
        }
    }
}