package com.refuem.planbucket.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.LoginActivity;
import com.refuem.planbucket.Models.ConnectChatModel;
import com.refuem.planbucket.Models.MessageModel;
import com.refuem.planbucket.Models.NotificationInviteModel;
import com.refuem.planbucket.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotifService extends Service {
    private ArrayList<MessageModel> messageNotificationStack = new ArrayList<>();
    /*Message Notification Service*/
    Socket messagesNotificationSocket;
    private Emitter.Listener onMessageConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("Message Socket Connect:", "Connected!");
        }
    };

    private Emitter.Listener onMessageConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("Message Socket Error:", args[0].toString());
        }
    };

    private Emitter.Listener onMessageNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MessageModel messageModel = gson.fromJson(String.valueOf(args[0]), MessageModel.class);
            throwMessageNotification(messageModel);
        }
    };

    private void throwMessageNotification(MessageModel messageModel){
        this.messageNotificationStack.add(messageModel);
        if(messageNotificationStack.size() >= 5){
            this.messageNotificationStack.remove(0);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"InviteChannel")
                .setSmallIcon(R.drawable.buksy);
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Ben");
        messagingStyle.setConversationTitle("Mesajlar");
        for(int i = this.messageNotificationStack.size() - 1; i >= 0; i--){
            MessageModel currentMessage = this.messageNotificationStack.get(i);
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(
                    "(" + currentMessage.getUsername() + ") - " + currentMessage.getMessage(), System.currentTimeMillis(),currentMessage.getGroup()
            );
            messagingStyle.addMessage(message);
        }
        Notification notification = builder.setStyle(messagingStyle).build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(3,notification);
    }
    /*Message Notification Service*/

    Socket socket;
    Gson gson = new Gson();
    private String[] groupNamesGlobal;
    String usernameGlobal;
    String tokenGlobal;

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("Socket Connect:","Connected!");
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("Socket Connect:",args[0].toString());
        }
    };
    private Emitter.Listener onInviteNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            NotificationInviteModel notificationInviteModel = gson.fromJson(String.valueOf(args[0]), NotificationInviteModel.class);
            throwNotification(notificationInviteModel);
        }
    };

    private void throwNotification(NotificationInviteModel notificationInviteModel){
        Notification notification = new NotificationCompat.Builder(this,"InviteChannel")
                .setContentTitle(notificationInviteModel.getHeader())
                .setContentText(notificationInviteModel.getMessage())
                .setSmallIcon(R.drawable.buksy)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(2,notification);
    }

     private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Merhaba";
            String description = "Merhaba Notification :)))))))";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("InviteChannel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        String username = intent.getStringExtra("username");
        String token = intent.getStringExtra("token");
        String[] groupNames = intent.getStringArrayExtra("groupNames");
        Intent newP = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,newP,0);
        Notification notification = new NotificationCompat.Builder(this,"InviteChannel")
                .setContentTitle("Bildirim Servisi")
                .setContentText("Bildirim servisleri aktif..")
                .setSmallIcon(R.drawable.boxer)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MIN)
                .build();
        try {
            socket = IO.socket(GlobalVariables.API_URL + "inviteNotification");
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        socket.once(Socket.EVENT_CONNECT,onConnect);
        socket.once(Socket.EVENT_CONNECT_ERROR,onConnectError);
        socket.on("onInviteNotification",onInviteNotification);
        socket.connect();
        socket.emit("sendUsername",username);
        if(groupNames.length > 0){
            try {
                messagesNotificationSocket = IO.socket(GlobalVariables.API_URL + "messages");
            } catch (URISyntaxException exception) {
                exception.printStackTrace();
            }

            messagesNotificationSocket.once(Socket.EVENT_CONNECT,onMessageConnect);
            messagesNotificationSocket.once(Socket.EVENT_CONNECT_ERROR,onMessageConnectError);
            messagesNotificationSocket.on("messageReceive",onMessageNotification);
            messagesNotificationSocket.connect();
            this.groupNamesGlobal = groupNames;
            this.tokenGlobal = token;
            this.usernameGlobal = username;
            for(String groupName : groupNames){
                ConnectChatModel connectChatModel = new ConnectChatModel(username,groupName,token,true);
                String jsonString = gson.toJson(connectChatModel);
                try {
                    JSONObject connectChatJSONObject = new JSONObject(jsonString);
                    messagesNotificationSocket.emit("joinChat",connectChatJSONObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        startForeground(1,notification);
        return START_NOT_STICKY;
    }
    private void disconnectFromChannel(String groupName){
        ConnectChatModel connectChatModel = new ConnectChatModel(usernameGlobal,groupName,tokenGlobal,true);
        String jsonString = gson.toJson(connectChatModel);
        try {
            JSONObject connectChatJSONObject = new JSONObject(jsonString);
            messagesNotificationSocket.emit("leaveRoom",connectChatJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
         if(socket != null)
             socket.disconnect();
        if(messagesNotificationSocket != null){
            for(String groupName : groupNamesGlobal){
                disconnectFromChannel(groupName);
            }
            messagesNotificationSocket.off("messageReceive");
            messagesNotificationSocket.disconnect();
        }
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
