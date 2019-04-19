package iste.not.com.ScientistOneToOneChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Messages.MessageAdapter;
import iste.not.com.Messages.Messages;
import iste.not.com.R;

public class ScientistOneToOneChatActivity extends AppCompatActivity {
    String currentUser;
    DatabaseReference databaseReference,reference;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    String userName;
    String userid;
    String currentUserId;
    Toolbar toolbar;
    CircleImageView profilePic;
    TextView username,online;
    MaterialEditText newMessage;
    ImageButton sendButton;
    HashMap<String,Object> map,requestMapSender,requestMapGetter,mapTime;
    RecyclerView messagesRec;
    private final List<Messages> messages = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_one_to_one_chat);
        messagesRec=(RecyclerView)findViewById(R.id.message);

        map= new HashMap<>();
        mapTime= new HashMap<>();
        requestMapSender= new HashMap<>();
        requestMapGetter=new HashMap<>();
        userid=getIntent().getStringExtra("id");
        currentUserId=getIntent().getStringExtra("currentUser");
        newMessage=(MaterialEditText)findViewById(R.id.new_message_edittex);
        sendButton=(ImageButton)findViewById(R.id.sendButton);
        messageAdapter = new MessageAdapter(messages,ScientistOneToOneChatActivity.this);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesRec.setAdapter(messageAdapter);
        messagesRec.setHasFixedSize(true);
        messagesRec.setLayoutManager(linearLayoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new  ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("ScientistEmailAdreses").child(currentUserId).exists())
                {
                    userName=dataSnapshot.child("ScientistEmailAdreses").child(currentUserId).child("unvan").getValue().toString()
                    +dataSnapshot.child("ScientistEmailAdreses").child(currentUserId).child("name").getValue().toString()
                    ;
                }
                else {
                    userName=dataSnapshot.child("Students").child(currentUserId).child("name").getValue().toString() ;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setToolbar();
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count)
            {
                Map<String,Object> map = new HashMap<>();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("isTexting").child(currentUser);
                if (before>0&&count-before<0)
                {if (count==0){
                    map.put("yaziyor","false");
                    map.put("siliyor","false");
                    map.put("notting","true");
                    Log.d("onTextChanged", "onTextChanged: "+"bişey yapmıyor->"+"true");
                    databaseReference.updateChildren(map);}
                else if(count-before<0)
                {
                    map.put("yaziyor","false");
                    map.put("siliyor","true");
                    map.put("notting","false");
                    Log.d("onTextChanged", "onTextChanged: "+"siliyor->"+"true");
                }
                }
                else if (before>0&&count-before>0)
                {

                    map.put("yaziyor","true");
                    map.put("siliyor","false");
                    map.put("notting","false");
                    Log.d("onTextChanged", "onTextChanged: "+"yazıyor->"+"true");
                    databaseReference.updateChildren(map);

                }
                else if (before==0&&count==0)
                {
                    map.put("yaziyor","false");
                    map.put("siliyor","false");
                    map.put("notting","true");
                    Log.d("onTextChanged", "onTextChanged: "+"bişey yapmıyor->"+"true");
                    databaseReference.updateChildren(map);
                }


            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage(userid,currentUserId ,newMessage);
            }
        });
        loadMessages();
    }

    private void sendNotification( String _getterId,String _senderName,String _senderId,String _title, String _type)
    {
        HashMap<String,String > notification = new HashMap<>();
        notification.put("name",_senderName);
        notification.put("send",_senderId);
        notification.put("title",_title);
        notification.put("type",_type);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(_getterId).push();
        reference.setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Log.d("message notification", "onComplete: "+"sent");
                }
                else
                    Log.w("message notification", "onComplete: ",task.getException() );
            }
        });

    }
    private void loadMessages()
    {
        final Query OneToOneChat = FirebaseDatabase.getInstance().getReference().child("Messages")
                .child("SinleMessages").child(currentUserId).child(userid).orderByChild("time");
        OneToOneChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages1 = dataSnapshot.getValue(Messages.class);
                messages.add(messages1);
                messageAdapter.notifyDataSetChanged();
                messagesRec.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendMessage(String userid, String currentUserId, MaterialEditText newMessage) {
    }

    private void setToolbar()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.scientist_one_to_one_chat_bar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("");
        final Map<String,Object> map = new HashMap<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("isTexting").child(currentUserId);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                map.put("yaziyor","false");
                map.put("siliyor","false");
                map.put("notting","true");
                databaseReference.updateChildren(map);
                setNotSeen (currentUser,userid);
                finish();}
        });
        DatabaseReference referenceOnline = FirebaseDatabase.getInstance().getReference().child("IsOnline").child(userid);
        referenceOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue()!=null)
                {
                    long x = (long) dataSnapshot.child("lastOnline").getValue();

                    if (dataSnapshot.child("online").getValue().toString().equals("true"))
                    {
                        String status = "Çevirim İçi";

                        isTexting(userid,online,status);
                        Log.d("current_user", "onDataChange: "+currentUser);
                        Log.d("userId", "onDataChange: "+userid);
                        // online.setText("Çevirim İçi");
                    }


                    else if (dataSnapshot.child("online").getValue().toString().equals("false"))

                    {
                        String status = "Son Görülme : "+getTimeAgo(-1*x);
                        online.setText("Son Görülme : "+getTimeAgo(-1*x));
                        //isTexting(userid,online,status);
                    }
                }
                else {
                    online.setText("Çevirim Dışı");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setDatabaseMessagesNotifcation(final String _userId, final String _currentUser)
    {
        final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("DatabaseMessagesNotifcation");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime")
                .child(_userId).child(_currentUser);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue()!=null){


                    if (dataSnapshot.child("seen").getValue().toString().equals("false"))
                    {
                        messageRef.child(_currentUser).child(_userId).push().child("new").setValue("new message").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d("MessagesNotifcation", "onComplete: "+"database messages added");
                                }
                                else
                                {
                                    Log.w("MessagesNotifcation", "onComplete: ",task.getException());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void setNotSeen(String currentUser, String userid) {
    }
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Şimdi";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "bir kaç dk önce";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " dk önce";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 saat önce";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " saat önce";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Dün";
        } else {
            return diff / DAY_MILLIS + " gün önce";
        }
    }
    private void isTexting(final String _userid, final TextView _text, final String text)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("isTexting").child(_userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue()!=null)
                {
                    if (dataSnapshot.child("siliyor").equals("true")
                    )
                    {
                        _text.setText("Siliyor...");
                    }
                    else if(dataSnapshot.child("yaziyor").getValue().equals("true"))
                    {
                        _text.setText("Yazıyor...");
                    }

                    else if(dataSnapshot.child("notting").getValue().equals("true")
                    )
                    {
                        _text.setText(text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









    }
    private void refresh(String _currentUser,String _userId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child("messegasTime")
                .child(_currentUser)
                .child(_userId);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                finish();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        setNotSeen(currentUserId,userid);
    }
    @Override
    protected void onResume()
    {

        super.onResume();
        online("true");
        refresh(currentUserId,userid);
    }
    @Override
    protected void onPause() {
        super.onPause();
        online("false");
    }

    private void online(String aFalse) {
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setSeen(currentUserId,userid);
    }

    private void setSeen(String currentUserId, String userid) {
    }
}
