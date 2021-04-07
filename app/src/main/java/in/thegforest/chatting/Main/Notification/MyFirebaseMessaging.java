package in.thegforest.chatting.Main.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.thegforest.chatting.Main.Chat.MassageActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented= remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");
        SharedPreferences preferences =getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser","none");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!= null && sented.equals(firebaseUser.getUid())){
            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    send_O_notification(remoteMessage);
                } else {
                    send_normal_notification(remoteMessage);
                }
                //sendNOtification(remoteMessage);
            }
        }
    }

    private void send_normal_notification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification= remoteMessage.getNotification();
        int i= Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this , MassageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (j>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());

    }

    private void send_O_notification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification= remoteMessage.getNotification();
        int i= Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this , MassageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       OreoAndAboveNotification notification1= new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getNotification(title,body,pendingIntent,defaultSound,icon);
        int j=0;
        if (j>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());
    }

    /*private void sendNOtification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification= remoteMessage.getNotification();
        int i= Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this , MassageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (j>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }*/
}
