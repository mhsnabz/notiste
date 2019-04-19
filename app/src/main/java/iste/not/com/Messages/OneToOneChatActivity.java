package iste.not.com.Messages;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.POST.CommentHelper.CommentAdapter;
import iste.not.com.Profile.NotificationActivity;
import iste.not.com.R;
import iste.not.com.Users.UserProfileActivity;

public class OneToOneChatActivity extends AppCompatActivity {

    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
    DatabaseReference databaseReference,reference;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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
    Query ref;
    ImageButton accept,cancel;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);
        messagesRec=(RecyclerView)findViewById(R.id.message);

        map= new HashMap<>();
        mapTime= new HashMap<>();
        requestMapSender= new HashMap<>();
        requestMapGetter=new HashMap<>();
        userid=getIntent().getStringExtra("id");
        currentUserId=getIntent().getStringExtra("currentUser");
        newMessage=(MaterialEditText)findViewById(R.id.new_message_edittex);
        sendButton=(ImageButton)findViewById(R.id.sendButton);
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
        messageAdapter = new MessageAdapter(messages,OneToOneChatActivity.this);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesRec.setAdapter(messageAdapter);
        messagesRec.setHasFixedSize(true);
        messagesRec.setLayoutManager(linearLayoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUserId);
        reference.addValueEventListener(new  ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        userName=dataSnapshot.child("name").getValue().toString();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
    private void sendMessage(final String _getterID, final String _senderID, final MaterialEditText _edittext)
    {
        final long time = Calendar.getInstance().getTimeInMillis();

        if (!_edittext.getText().toString().isEmpty()) {

            final DatabaseReference messegasTime = FirebaseDatabase.getInstance().getReference().child("Messages")
                    .child("messegasTime");
            mapTime.put("seen","false");
            mapTime.put("time", time);
            messegasTime.child(_senderID).child(_getterID).child("time").setValue(-1*time)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("messageTime", "onComplete: " + "eklendi");
                            } else
                                Log.w("messageTime", "onComplete: ", task.getException());
                        }
                    });

            messegasTime.child(_getterID).child(_senderID).child("time").setValue(-1*time)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("messageTime", "onComplete: " + "eklendi");
                            } else
                                Log.w("messageTime", "onComplete: ", task.getException());
                        }
                    });


            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages")
                    .child("SinleMessages");
            map.put("from", _senderID);
            map.put("time", time);
            map.put("message", _edittext.getText().toString());
            map.put("seen", "false");
            reference.child(_senderID).child(_getterID).push().setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("message", "onComplete: " + "eklendi");
                                _edittext.setText("");

                                setDatabaseMessagesNotifcation(_senderID, _getterID);
                            } else
                                Log.w("message", "onComplete: ", task.getException());
                        }
                    });
            reference.child(_getterID).child(_senderID).push().setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("message", "onComplete: " + "eklendi");
                                _edittext.setText("");
                            } else
                                Log.w("message", "onComplete: ", task.getException());
                        }
                    });

        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("NotificationSettings").child(_getterID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("messages").exists())
                {
                    if (dataSnapshot.child("messages").getValue().toString().equals("true"))
                    {
                        sendNotification(_getterID, userName, currentUserId, "Mesaj", "Size Yeni Bir Mesaj Gönderdi");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setToolbar()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.one_to_one_chat_bar, null);
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

        profilePic=(CircleImageView)findViewById(R.id.profilePic);
        username=(TextView)findViewById(R.id.name) ;
        online=(TextView)findViewById(R.id.online);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(OneToOneChatActivity.this,UserProfileActivity.class)   ;
                intent.putExtra("user_id",userid);
                intent.putExtra("currentUser",currentUserId);
                startActivity(intent);
            }
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(image).resize(256,256).centerCrop().placeholder(R.drawable.placeholder_image)
                        .into(profilePic);
                String name = dataSnapshot.child("name").getValue().toString();
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    private void sendNotification(final String _getterId, final String _senderName, final String _senderId, final String _title, final String _type)
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
    @Override
    protected void onResume()
    {

        super.onResume();
        online("true");
        refresh(currentUserId,userid);
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
    protected void onPause() {
        super.onPause();
        online("false");
    }
    private void online(final String status)
    {
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("IsOnline").child(currentUser);
        reference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUser);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot!=null)
                            {
                                // databaseReference.child("online").onDisconnect().setValue(_false);
                                databaseReference.child("online").setValue(status);
                                if (status.equals("false"))
                                {
                                //    setNotSeen(currentUser,userid);
                                }
                                databaseReference.child("lastOnline").setValue(-1*Calendar.getInstance().getTimeInMillis());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

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
    protected void onStart()
    {
        super.onStart();
        setSeen(currentUserId,userid);
    }

    public void setSeen (final String _currentUser, final String _userId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child("messegasTime");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(_currentUser).child(_userId).exists()&&
                        dataSnapshot.child(_userId).child(_currentUser).exists())
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime")
                            .child(_userId).child(_currentUser);
                    reference.child("seen").setValue("true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void setNotSeen (final String _currentUser, final String _userId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child("messegasTime");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(_currentUser).child(_userId).exists()&&
                        dataSnapshot.child(_userId).child(_currentUser).exists())
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime")
                            .child(_userId).child(_currentUser);
                    reference.child("seen").setValue("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public void options(View view)
    {
        CharSequence secenekler[] = new CharSequence[]{ "Engelle","Şikayet Et"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(OneToOneChatActivity.this);
        builder.setTitle("Şeçenekler");
        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("value is", "" + which);
                switch (which)
                {
                    case 0:


                        final Dialog block = new Dialog(OneToOneChatActivity.this);
                        block.setContentView(R.layout.dailog_interface);
                        TextView block_message = (TextView) block.findViewById(R.id.message);
                        TextView block_title = (TextView) block.findViewById(R.id.title);
                        block_title.setText("UYARI");
                        String html_block = getString(R.string.englle);
                        block_message.setText(Html.fromHtml(html_block));
                        Button block_button = (Button) block.findViewById(R.id.ok);
                        block_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                       .child("Messages")
                                       .child("request")
                                       .child(currentUserId)
                                       .child(userid);
                               databaseReference.child("accept")
                                       .setValue("blocked")
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                                if (task.isSuccessful())
                                                {
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                            .child("Messages")
                                                            .child("request")
                                                            .child(userid)
                                                            .child(currentUser);
                                                    databaseReference.child("accept")
                                                            .setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference()
                                                                        .child("Messages").child("SinleMessages");
                                                                messageReference.child(currentUserId).child(userid).removeValue();
                                                                messageReference.child(userid).child(currentUserId).removeValue();
                                                                DatabaseReference messageReference1 = FirebaseDatabase.getInstance().getReference()
                                                                        .child("Messages").child("messegasTime");
                                                                messageReference1.child(currentUserId).child(userid).removeValue();
                                                                messageReference1.child(userid).child(currentUserId).removeValue();
                                                                finish();
                                                            }
                                                        }
                                                    });



                                                    block.dismiss();
                                                }
                                           }
                                       });
                            }
                        });

                        block.show();
                    case 1:


                        break;


                }
            }
        });
        builder.setNegativeButton("İptal", null);

        builder.show();


    }
}
