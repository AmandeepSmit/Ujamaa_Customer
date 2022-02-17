package com.ujamaaonline.customer.service_class;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.Html;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.ActivityChat;
import com.ujamaaonline.customer.activities.MainActivity;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.utils.BaseUtil;
import com.ujamaaonline.customer.utils.Constants;
import org.jetbrains.annotations.NotNull;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    static int count = 0;
    private String senderId;
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage){
        senderId= BaseUtil.getSenderAccountId(getApplicationContext());
        if (remoteMessage != null) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        count++;
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String id = remoteMessage.getData().get("sender_id");
        Intent intent;
        if (remoteMessage.getData().get("type")!= null) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", remoteMessage.getData().get("booking_id"));
            intent.putExtra("notification","");
            title = "UJAMAA Online Customer";
            body = remoteMessage.getData().get("title");
            setNotificationData(title, body, intent);
        }else{
            assert id != null;
            if (!id.equals(senderId)) {
                String mTitle = remoteMessage.getData().get("title");
                String mData = remoteMessage.getData().get("message");
                intent = new Intent(this, ActivityChat.class);
                intent.putExtra("businessId", id);
                intent.putExtra("notification","");
                intent.putExtra("userName",remoteMessage.getData().get("businessName"));
                intent.putExtra("businessLogo",remoteMessage.getData().get("businessLogo"));
                intent.putExtra("notification", "");
                setNotificationData(mTitle, mData, intent);
            }
        }
    }

    private void setNotificationData(String title, String message, Intent stackBuilder) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, stackBuilder,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("ID", "Channel", importance);
            mChannel.setName(Html.fromHtml("<b>" + title + "</b>", 0));
            mChannel.setDescription(message);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(defaultSoundUri, null);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotifyManager != null;
            mNotifyManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ID");
        mBuilder.setContentTitle(Html.fromHtml("<b>" + title + "</b>"))
                .setContentText(message)
                .setSmallIcon(R.drawable.main_squire_logo)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId("ID")
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setNumber(count)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        assert mNotifyManager != null;
        mNotifyManager.cancelAll();
        mNotifyManager.notify(0, mBuilder.build());
    }

}
