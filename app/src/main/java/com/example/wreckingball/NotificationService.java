package com.example.wreckingball;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {
	
    private static String CHANNEL1_ID = "channel1";
    private static String CHANNEL1_NAME = "Channel 1 Demo";
	
    private static int id = 1;

    private int i;
	
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        NotificationService getService(){
            return NotificationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
		
        // 1. Get reference to Notification Manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 2. Create Notification Channel (ONLY ONEs)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //Create channel only if it is not already created
            if (notificationManager.getNotificationChannel(CHANNEL1_ID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL1_ID,
                        CHANNEL1_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        i = 0;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(i < 60*60*2) {
                    SystemClock.sleep(1000);
                    i++;
                }

                showNotification();
            }
        }).start();
        return START_NOT_STICKY;
    }


    // 3. When buCreate the Notification & send it to the device status bar
    private void showNotification()
    {
        Log.d("show notification:", "");
		
        String notificationTitle = "Wrecking Ball";
        String notificationText = "You have not played for two hours, want now?";

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
		
//		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		am.cancel(contentIntent);
//		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//				+ AlarmManager.INTERVAL_DAY * 2, AlarmManager.INTERVAL_DAY * 2, contentIntent);

        // Build Notification with NotificationCompat.Builder
        // on Build.VERSION < Oreo the notification avoid the CHANEL_ID
        Notification notification = new NotificationCompat.Builder(this, CHANNEL1_ID)
                .setSmallIcon(R.drawable.icon)		//Set the icon
                .setContentTitle(notificationTitle)		//Set the title of Notification
                .setContentText(notificationText)		//Set the text for notification
                .setContentIntent(pendingIntent)
                //.setOngoing(true)		// stick notification
                .build();

        // Send the notification to the device Status bar.
        notificationManager.notify(id, notification);
        // Start foreground service.
        startForeground(id, notification);

    }
	
	@Override
    public void onDestroy() {
        notificationManager.cancel(id);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
