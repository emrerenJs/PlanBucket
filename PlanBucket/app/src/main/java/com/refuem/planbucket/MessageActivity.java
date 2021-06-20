package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.Configurations.SocketIOHelper;
import com.refuem.planbucket.Models.AddGroupModel;
import com.refuem.planbucket.Models.ConnectChatModel;
import com.refuem.planbucket.Models.MessageModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.SendMessageModel;
import com.refuem.planbucket.Models.UserRoleModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    ScrollView messageScrollView;
    SocketIOHelper socketIOHelper;
    private EditText messageET;
    private MyAccountManager myAccountManager;
    private final String LEAVE_ROOM = "leaveRoom";
    private final String LEAVED = "leaved";
    private final String NEW_JOIN = "newJoin";
    private final String JOIN_ROOM = "joinChat";
    private final String SEND_MESSAGE = "sendMessage";
    private final String MESSAGE_RECEIVE = "messageReceive";
    private final String getMessagesURL = GlobalVariables.API_URL + "group/getMessages";
    private Socket socket;
    private ConnectChatModel connectChatModel;
    private JSONObject connectChatJSONObject;
    private final Gson gson = new Gson();

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
    private Emitter.Listener onConnectErrorTO = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("Socket Connect (TO):",args[0].toString());
        }
    };
    private Emitter.Listener onNewJoinListener = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageModel messageModel = gson.fromJson(String.valueOf(args[0]),MessageModel.class);
                    renderMessage(messageModel);
                }
            });
        }
    };
    private Emitter.Listener onLeaved = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageModel messageModel = gson.fromJson(String.valueOf(args[0]),MessageModel.class);
                    renderMessage(messageModel);
                }
            });
        }
    };
    private Emitter.Listener onMessageReceived = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageModel messageModel = gson.fromJson(String.valueOf(args[0]),MessageModel.class);
                    renderMessage(messageModel);
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.v("Socket Connect (Disconnect)",args[0].toString());
        }
    };


    private void bindViews(){
        messageScrollView = findViewById(R.id.messageScrollView);
        messageET = findViewById(R.id.messageET);
    }

    public void renderMessage(MessageModel messageModel){
        final LinearLayout linearLayout = findViewById(R.id.messagesLayout);
        TextView textView = new TextView(this);
        final LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        if(messageModel.getMessageType().equals("information") && messageModel.getUsername().equals(myAccountManager.getUsername())){
            textView.setText("Bağlandınız");
        }else if(messageModel.getMessageType().equals("information")){

            textView.setText(messageModel.getUsername() + "\n" + messageModel.getMessage());
        }else{
            String date = messageModel.getDate().substring(0,10).replace("-",".");
            String clock = messageModel.getDate().substring(11,16);
            String messag = messageModel.getUsername() + "\n" + messageModel.getMessage();
            SpannableString spannableString = new SpannableString(messag + "\n" + date + " " + clock);
            spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),messag.length(),messag.length()+17, 0);
            spannableString.setSpan(new RelativeSizeSpan(0.8f), messag.length(),messag.length()+17, 0);
            spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), messag.length(),messag.length()+17, 0);
            spannableString.setSpan(new ForegroundColorSpan(Color.rgb(245,106,86)), 0,messageModel.getUsername().length(), 0);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),0,messageModel.getUsername().length(),0);
            textView.setText(spannableString);
        }
        if(messageModel.getMessageType().equals("information")){
            textView.setBackground(getDrawable(R.drawable.info_message));
            layoutParamsTextView.setMargins(0,30,0,30);
            layoutParamsTextView.gravity = Gravity.CENTER;
            textView.setPadding(40,40,40,40);
        }else{
            if(messageModel.getUsername().equals(myAccountManager.getUsername())){
                textView.setBackground(getDrawable(R.drawable.my_message));
                layoutParamsTextView.setMargins(250,30,30,30);
                layoutParamsTextView.gravity = Gravity.RIGHT;
                textView.setPadding(50,50,50,50);
            }else{
                textView.setBackground(getDrawable(R.drawable.friend_message));
                layoutParamsTextView.setMargins(30,30,250,30);
                layoutParamsTextView.gravity = Gravity.LEFT;
                textView.setPadding(50,50,50,50);
            }
        }
        textView.setLayoutParams(layoutParamsTextView);
        textView.setTextColor(Color.WHITE);
        if(messageModel.getMessageType().equals("information")){
            textView.setGravity(Gravity.CENTER);
        }else{
            textView.setGravity(Gravity.LEFT);
        }
        textView.setTextSize(16);
        linearLayout.addView(textView);
        messageScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        bindViews();
        getMessagesHTTPRequest(new AddGroupModel(
                getIntent().getStringExtra("groupTitle"),
                myAccountManager.getUsername(),
                myAccountManager.getToken()
        ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.emit(this.LEAVE_ROOM,connectChatJSONObject);
        socket.off(this.NEW_JOIN);
        socket.off(this.LEAVED);
        socket.off(this.MESSAGE_RECEIVE);
        socket.disconnect();
        myAccountManager.startNotificationService(this);
    }

    private void getMessagesHTTPRequest(AddGroupModel getMessagesModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(getMessagesModel);
        try{
            JSONObject getMessagesJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getMessagesURL,
                    getMessagesJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<MessageModel>>(){}.getType();
                                ArrayList<MessageModel> getMessagesModel = gson.fromJson(responseModel.getBody(),(listType));
                                for(MessageModel messageModel : getMessagesModel){
                                    renderMessage(messageModel);
                                }
                                connectSocket();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MessageActivity.this);
                            }
                            else
                                Toast.makeText(MessageActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MessageActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /*SOCKET FUNCTİONS*/
    private void connectSocket() {
        this.connectChatModel = new ConnectChatModel(
                myAccountManager.getUsername(),
                getIntent().getStringExtra("groupTitle"),
                myAccountManager.getToken(),
                false
        );
        try{
            socket = IO.socket(GlobalVariables.API_URL + "messages");
            socket.once(Socket.EVENT_CONNECT,onConnect);
            socket.once(Socket.EVENT_CONNECT_ERROR,onConnectError);
            socket.once(Socket.EVENT_CONNECT_TIMEOUT,onConnectErrorTO);
            socket.once(Socket.EVENT_DISCONNECT,onDisconnect);
            socket.on(this.NEW_JOIN,onNewJoinListener);
            socket.on(this.LEAVED,onLeaved);
            socket.on(this.MESSAGE_RECEIVE,onMessageReceived);
            messageScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }catch(URISyntaxException exception){
            Log.v("Socketxyz","Tınne");
        }
        socket.connect();
        joinRoom();
    }
    private void joinRoom(){
        String jsonString = gson.toJson(connectChatModel);
        try {
            connectChatJSONObject = new JSONObject(jsonString);
            socket.emit(this.JOIN_ROOM,connectChatJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*SOCKET FUNCTIONS*/

    /*BUTTON ONCLICK*/
    public void sendMessageOnClickListener(View v){
        if(!messageET.getText().toString().trim().equals("")){
            SendMessageModel sendMessageModel = new SendMessageModel(
                    "message",
                    messageET.getText().toString(),
                    myAccountManager.getUsername(),
                    myAccountManager.getToken(),
                    getIntent().getStringExtra("groupTitle")
            );
            String jsonString = gson.toJson(sendMessageModel);
            try {
                JSONObject sendMessageJSONObject = new JSONObject(jsonString);
                socket.emit(this.SEND_MESSAGE,sendMessageJSONObject);
                messageET.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToBottomOnClickListener(View v){
        messageScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}