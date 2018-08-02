package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lucsoninfotech.cmi.cmiworld.Adapter.ChatMessageAdapter;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.ChatMessageModel;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.lucsoninfotech.cmi.cmiworld.other.HttpHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private static final String[] eatFoodyImages = {
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg",
    };
    private static String sent_time;
    private static String sent_date;
    private static int RESULT_LOAD_IMG = 1;
    private final Runnable history = new Runnable() {
        public void run() {
            // Your code for doing something
            new setHistory().execute();
        }
    };
    private final Runnable doSomething = new Runnable() {
        public void run() {
            // Your code for doing something
            new ReceiveMessage().execute();
        }
    };
    int pos;
    Handler handler = new Handler();
    MediaRecorder mRecorder;
    String fileName = Environment.getExternalStorageDirectory().getPath() + "/Quranology/QuranologyAudio" + "/audio" + System.currentTimeMillis() + ".m4a";
    Boolean isRecording;
    SeekBar seekBar;
    ProgressBar progressBar;
    int MAX_DURATION = 900000;
    Dialog dialog;
    LinearLayout layoutRecordAllButtons, layoutCancel, layoutRecord, layoutStop, layoutSendAllButtons, layoutSend, layoutSendCancel,
            layoutMiddleRecord, layoutMiddlePlay, layoutView;
    ImageView ivPlay, ivPause;
    MediaPlayer mPlayer;
    int playTime, recordTime, pauseTime;
    Chronometer starttime;
    Chronometer tvAudioDuration;
    boolean playOnce = true;
    long timeWhenStopped = 0;
    int second, minutes;
    String chatDpUrl;
    TextView tvChatContactsStatus;
    private EditText etMessage;
    private ProgressDialog pDialog;
    private String message;
    private ListView lvMessageContainer;
    private ArrayList<ChatMessageModel> chatHistory;
    private ChatMessageAdapter adapter;
    private SimpleDateFormat simpleDateFormat;
    private String time;
    private String date;
    private Calendar calander;
    private LinearLayout layout_detail;
    private String url;
    private String receiverName;
    private String timezone_id;
    private boolean notify = false;
    private URI uri;
    private String urlEncoded;
    private String urlSafe;
    private String newMessage;
    private int count = 0;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        System.out.println("toolbar" + toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_button);
        layout_detail = toolbar.findViewById(R.id.layout_detail);
        TextView tvChatContactName = toolbar.findViewById(R.id.chatContactName);
        ImageView chatDp = toolbar.findViewById(R.id.chatContactDp);
        //tvChatContactsStatus = (TextView) toolbar.findViewById(R.id.chatContactStatus);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Bundle b = getIntent().getExtras();
        flag = b.getString("flag");

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        timezone_id = tz.getID();
        Constant.TIMEZONE = timezone_id;


        tvChatContactName.setText(Constant.SEM_NAME);


        Picasso.get().load(Constant.SEM_DP)
                .placeholder(R.drawable.camera).into(chatDp);
        try {
            initializeComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initializeComponents() {

        etMessage = findViewById(R.id.messageEdit);
        ImageButton btnSend = findViewById(R.id.chatSendButton);
        lvMessageContainer = findViewById(R.id.messagesContainer);
        RelativeLayout container = findViewById(R.id.container);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.list_item_section, lvMessageContainer,
                false);
        lvMessageContainer.addHeaderView(header, null, false);

        loadDummyHistory();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                try {
                    urlSafe = URLEncoder.encode(message, "UTF-8");
                    System.out.println("URL safe:::" + urlSafe);
                    uri = new URI(urlSafe.replace(" ", "%20"));
                    System.out.println("URI:::::::::" + uri.toString());
                    urlEncoded = Uri.encode(uri.toString(), ALLOWED_URI_CHARS);
                    System.out.println("urlEncoded::::" + urlEncoded);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                url = Constant.PrivateChatUrl + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID + "&message_type=1&message=" + urlEncoded + "&timezone=" + timezone_id + "&flag=" + flag;
                System.out.println("URL is private::::::" + url);

                /*try {
                    uri = new URI(url.replace(" ", "%20"));
                    System.out.println("URI:::::::::"+uri.toString());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/



                /*urlEncoded = Uri.encode(uri.toString(),ALLOWED_URI_CHARS);
                System.out.println("urlEncoded::::"+urlEncoded);*/

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new SendMessage().execute();
                    }
                });

                ChatMessageModel chatMessage = new ChatMessageModel();
                chatMessage.setId(111); // dummy
                chatMessage.setMessage(message);
                chatMessage.setDateTime(getTime());
                chatMessage.setMe(true);
                chatMessage.setMessageType(0);
                etMessage.setText("");

                displayMessage(chatMessage);
            }
        });
    }

    private void displayMessage(ChatMessageModel chatMessage) {
        adapter.add(chatMessage);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        lvMessageContainer.setSelection(lvMessageContainer.getCount() - 1);
    }

    private void loadDummyHistory() {
        chatHistory = new ArrayList<>();

        setHistoryInChat();

        call_timer();

//        adapter = new ChatMessageAdapter(GroupChat.this,chatHistory,eatFoodyImages);
//        adapter.notifyDataSetChanged();
////        for(int i=0; i<chatHistory.size(); i++) {
////            ChatMessageModel message = chatHistory.get(i);
////            displayMessage(message);
////        }
//        lvMessageContainer.setAdapter(adapter);

    }

    private void setHistoryInChat() {
        this.runOnUiThread(history);
    }

    private void call_timer() {
        int t = 5000;
        Timer myt = new Timer();
        myt.schedule(new TimerTask() {

            @Override
            public void run() {
                timerMethod();
            }
        }, 0, t);
    }

    private void timerMethod() {
        this.runOnUiThread(doSomething);
    }

    private void showNotification() {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent();

        PendingIntent pIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, 0);
        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new NotificationCompat.Builder(this, "my_channel_id_01")
                .setContentTitle("CMI")
                .setContentText(newMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setNumber(++count)
                .setSound(soundUri)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(0, mNotification);
    }

    private String getTime() {
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        time = simpleDateFormat.format(calander.getTime());
        return time;
    }

    private String getDate() {
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDateFormat.format(calander.getTime());
        //System.out.println("Current date"+date);
        return date;
    }

    private String getTime1() {
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        time = simpleDateFormat.format(calander.getTime());
        return time;
    }

    public String getUTCTime() {
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = simpleDateFormat.format(calander.getTime());
        System.out.println("Current date" + date);
        return date;
    }

    @SuppressLint("StaticFieldLeak")
    private class SendMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    System.out.println("jsonObj:::: in send" + jsonObj);
                    // Getting JSON Array node
                    //JSONArray data = jsonObj.getJSONArray("data");
                    //System.out.println("jsonArray::::"+data);

                    // looping through All Contacts
                    //for (int i = 0; i < data.length(); i++) {
                    JSONObject c = jsonObj.getJSONObject("data");

                    String receiver_id = c.getString("id");
                    String sent_time = c.getString("IST");
                    String[] splited_time = sent_time.split("\\s+");
                    System.out.println("time in send:::::" + splited_time[1]);
                    System.out.println("Response::::::::::::" + receiver_id);

                    //}
                } catch (final JSONException e) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println(e.getMessage());
                        }
                    });
                }
            } else {
                Log.e("error", "Couldn't get json from server.");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class setHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChatActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String urlHistory;
            //http://lucsoninfotech.in/cmi/ws/history.php?sender_id=1&receiver_id=2&timezone=America/New_York
            urlHistory = Constant.HistoryChatUrl + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID + "&timezone=" + timezone_id;
            //urlReceive = urlReceive.replaceAll(" ","%20");
            System.out.println("urlHistory::::::" + urlHistory);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlHistory);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    System.out.println("jsonObj:::: in history" + jsonObj);
                    // Getting JSON Array node
                    int error_code = jsonObj.getInt("error_code");
                    if (error_code == 0) {
                        JSONArray data = jsonObj.getJSONArray("data");
                        System.out.println("jsonArray:::: in history" + data);

                        // looping through All Contacts
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject c = data.getJSONObject(i);
                            String sender_id = c.getString("sender_id");
                            String message = c.getString("message");
                            String time = c.getString("IST");
                            String message_image = c.getString("message_image");
                            String message_thumb = c.getString("message_thumb");
                            String message_video = c.getString("message_video");
                            String message_video_thumb = c.getString("message_video_thumb");
                            String message_audio = c.getString("message_audio");
                            String[] splited = time.split("\\s+");
                            String sent_time_local = c.getString("sent_time");
                            String[] splited_sent_time = sent_time_local.split("\\s+");
                            sent_time = splited_sent_time[1];
                            sent_date = splited_sent_time[0];
                            System.out.println("Message history::::" + message);
                            System.out.println("image::" + message_image);
                            System.out.println("video::" + message_video_thumb);
                            System.out.println("audio::" + message_audio);
                            ChatMessageModel msg = new ChatMessageModel();
                            msg.setId(1);
                            if (sender_id.equalsIgnoreCase(Constant.USER_ID)) {
                                msg.setMe(true);
                            } else {
                                msg.setMe(false);
                            }
                            msg.setMessage(message);
                            msg.setMessage_image(message_image);
                            msg.setMessage_thumb(message_thumb);
                            msg.setMessage_video(message_video);
                            msg.setMessage_video_thumb(message_video_thumb);
                            msg.setMessage_audio(message_audio);
                            if (!TextUtils.isEmpty(message_image)) {
                                msg.setHistory(true);
                                msg.setReceive(false);
                                msg.setSend(false);
                                msg.setMessageType(1);
                            } else if (!TextUtils.isEmpty(message_video)) {
                                msg.setHistory(true);
                                msg.setReceive(false);
                                msg.setSend(false);
                                msg.setMessageType(3);
                            } else if (!TextUtils.isEmpty(message_audio)) {
                                msg.setHistory(true);
                                msg.setReceive(false);
                                msg.setSend(false);
                                msg.setMessageType(2);
                            } else {
                                msg.setMessageType(0);
                            }
                            msg.setDateTime(splited[1]);
                            msg.setUserName(receiverName);
                            chatHistory.add(msg);
                        }
                    } else {
                        sent_time = getTime1();
                        sent_date = getDate();
                    }

                    System.out.println("Sent_time::::::::" + sent_time);
                } catch (final JSONException e) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                            System.out.println(e.getMessage());
                        }
                    });
                }
            } else {
                Log.e("error", "Couldn't get json from server.");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            adapter = new ChatMessageAdapter(ChatActivity.this, chatHistory, eatFoodyImages);
            adapter.notifyDataSetChanged();
            lvMessageContainer.setAdapter(adapter);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class ReceiveMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String urlReceive = Constant.MessgaeChatUrl + Constant.USER_ID + "&receiver_id=" + Constant.RECEIVER_ID + "&time=" + sent_date + "q" + sent_time + "&timezone=" + timezone_id;
            //urlReceive = urlReceive.replaceAll(" ","%20");
            System.out.println("urlReceive::::" + urlReceive);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlReceive);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    System.out.println("jsonObj:::: in receive message" + jsonObj);
                    // Getting JSON Array node
                    int error_code = jsonObj.getInt("error_code");
                    System.out.println("json object error code:::::::" + error_code);
                    JSONArray data = jsonObj.getJSONArray("data");
                    System.out.println("jsonArray:::: in receive message" + data);

                    notify = error_code == 0;

                    // looping through All Contacts
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject c = data.getJSONObject(i);
                        String sender_id = c.getString("sender_id");
                        newMessage = c.getString("message");
                        String time = c.getString("IST");
                        String message_image = c.getString("message_image");
                        String message_thumb = c.getString("message_thumb");
                        String message_video = c.getString("message_video");
                        String message_video_thumb = c.getString("message_video_thumb");
                        String message_audio = c.getString("message_audio");
                        String[] splited = time.split("\\s+");
                        String sent_time_local = c.getString("sent_time");
                        String[] splited_sent_time = sent_time_local.split("\\s+");
                        sent_time = splited_sent_time[1];
                        sent_date = splited_sent_time[0];
                        //System.out.println("splited time::"+splited[1]);
                        System.out.println("New Message::::" + newMessage);
                        ChatMessageModel msg = new ChatMessageModel();
                        msg.setId(1);
                        if (sender_id.equalsIgnoreCase(Constant.USER_ID)) {
                            msg.setMe(true);
                        } else {
                            msg.setMe(false);
                        }
                        msg.setMessage(newMessage);
                        msg.setMessage_image(message_image);
                        msg.setMessage_thumb(message_thumb);
                        msg.setMessage_video(message_video);
                        msg.setMessage_video_thumb(message_video_thumb);
                        msg.setMessage_audio(message_audio);
                        if (!TextUtils.isEmpty(message_image)) {
                            msg.setHistory(false);
                            msg.setReceive(true);
                            msg.setSend(false);
                            msg.setMessageType(1);
                        } else if (!TextUtils.isEmpty(message_video)) {
                            msg.setHistory(false);
                            msg.setReceive(true);
                            msg.setSend(false);
                            msg.setMessageType(3);
                        } else if (!TextUtils.isEmpty(message_audio)) {
                            msg.setHistory(false);
                            msg.setReceive(true);
                            msg.setSend(false);
                            msg.setMessageType(2);
                        } else {
                            msg.setMessageType(0);
                        }
                        msg.setDateTime(splited[1]);
                        msg.setUserName(receiverName);
                        chatHistory.add(msg);
                    }
                } catch (final JSONException e) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                            System.out.println(e.getMessage());
                        }
                    });
                }
            } else {
                Log.e("error", "Couldn't get json from server.");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (notify) {
                adapter = new ChatMessageAdapter(ChatActivity.this, chatHistory, eatFoodyImages);
                adapter.notifyDataSetChanged();
                scroll();
                showNotification();
            }

        }

    }

}
