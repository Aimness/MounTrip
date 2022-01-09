package com.company.android.myapplication.test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.company.android.myapplication.MainActivity;
import com.company.android.myapplication.Notification.NotificationFragment;
import com.company.android.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FbMessagingServices extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
            final Intent intent = new Intent(this, NotificationFragment.class);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notifId = new Random().nextInt(3000);

            setChannel(notificationManager);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
            Uri notifURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "sdfsf")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setAutoCancel(true)
                    .setSound(notifURI)
                    .setContentIntent(pendingIntent);



            notificationManager.notify(notifId, notificationBuilder.build());
        }
    }

    private void setChannel(NotificationManager notificationManager)
    {
        CharSequence adminChannelName = "New notification";
        String adiminChannalDesc = "Notification hahah";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("sdfsf",adminChannelName, NotificationManager.IMPORTANCE_HIGH);

        adminChannel.setDescription(adiminChannalDesc);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        if(notificationManager != null)
            notificationManager.createNotificationChannel(adminChannel);
    }
}