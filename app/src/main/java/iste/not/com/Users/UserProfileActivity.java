package iste.not.com.Users;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Messages.CreateNewMessageActivity;
import iste.not.com.Messages.OneToOneChatActivity;
import iste.not.com.R;

public class UserProfileActivity extends AppCompatActivity {
    String  user_id;
    String  current_user;
    CircleImageView profileImage;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView name,Class,major,contact;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        user_id =getIntent().getStringExtra("user_id");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        current_user=firebaseUser.getUid();
        profileImage =(CircleImageView)findViewById(R.id.profilePic);
        name=(TextView)findViewById(R.id.name);
        Class=(TextView)findViewById(R.id.Class);
        major=(TextView)findViewById(R.id.major);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setProfile(profileImage,name,major,Class,user_id);
        acceptOrCancel(current_user,user_id);
        sendRequest(current_user,user_id);
        dismissReques(current_user,user_id);
        sendMessage(current_user,user_id);
        setRequestAnswer(current_user,user_id);
    }
    private void setProfile(final CircleImageView image, final TextView name, final TextView major, final TextView Class, String userid)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String _image = dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(_image).resize(1024,1024).placeholder(R.drawable.placeholder_image).centerCrop().into(image);
                String _name = dataSnapshot.child("name").getValue().toString();
                name.setText(_name);
                String _major = dataSnapshot.child("major").getValue().toString();
                major.setText(_major);
                String _Class = dataSnapshot.child("Class").getValue().toString();
                Class.setText(_Class);
                actionBar.setTitle(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void acceptOrCancel(final String _currentUser, final String _userId)
    {
        final ImageButton sendMessage= (ImageButton)findViewById(R.id.send_message);
        final LinearLayout linay = (LinearLayout)findViewById(R.id.accep_cancel);
        final LinearLayout linLaySendMessage = (LinearLayout)findViewById(R.id.linLaySendMessage);
        final TextView request = (TextView)findViewById(R.id.request);
        final  TextView waiting_request = (TextView)findViewById(R.id.wait_request);
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
        reference.child(_userId).child(_currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot)
            {
                reference1.child(_currentUser).child(_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds)
                    {
                        if (dataSnapshot.getValue()!=null && ds.getValue()!=null)
                        {
                            if (dataSnapshot.child("accept").getValue().toString()
                                    .equals("true") && ds.child("accept").getValue().toString()
                                    .equals("true")) {
                                request.setVisibility(View.INVISIBLE);
                                waiting_request.setVisibility(View.INVISIBLE);
                                linay.setVisibility(View.INVISIBLE);
                                sendMessage.setVisibility(View.VISIBLE);
                                linLaySendMessage.setVisibility(View.VISIBLE);
                            } else if (dataSnapshot.child("accept").getValue().toString()
                                    .equals("false") && ds.child("accept").getValue().toString()
                                    .equals("true")) {
                                request.setVisibility(View.INVISIBLE);
                                waiting_request.setVisibility(View.VISIBLE);
                                linay.setVisibility(View.INVISIBLE);
                                sendMessage.setVisibility(View.INVISIBLE);
                                linLaySendMessage.setVisibility(View.INVISIBLE);
                            } else if (dataSnapshot.child("accept").getValue().toString()
                                    .equals("true") && ds.child("accept").getValue().toString()
                                    .equals("false")) {
                                request.setVisibility(View.INVISIBLE);
                                waiting_request.setVisibility(View.INVISIBLE);
                                linay.setVisibility(View.VISIBLE);
                                sendMessage.setVisibility(View.INVISIBLE);
                                linLaySendMessage.setVisibility(View.INVISIBLE);
                            }
                            else if (dataSnapshot.child("accept").getValue().toString()
                                    .equals("false") && ds.child("accept").getValue().toString()
                                    .equals("false")) {
                                request.setVisibility(View.VISIBLE);
                                waiting_request.setVisibility(View.INVISIBLE);
                                linay.setVisibility(View.INVISIBLE);
                                sendMessage.setVisibility(View.INVISIBLE);
                                linLaySendMessage.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendRequest(final String _currentUser, final String _userId)
    {

        final TextView request = (TextView)findViewById(R.id.request);
        final  TextView waiting_request = (TextView)findViewById(R.id.wait_request);
        if (_currentUser.equals(_userId)){
            request.setVisibility(View.GONE);
            waiting_request.setVisibility(View.GONE);

        }
        request.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
                reference.child(_currentUser).child(_userId).child("accept").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        request.setVisibility(View.INVISIBLE);
                        waiting_request.setVisibility(View.VISIBLE);
                    }
                });
                reference.child(_userId).child(_currentUser).child("accept").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

            }
        });
    }

    public void dismissReques(final String _currentUser, final String _userId)
    {
        final TextView request = (TextView)findViewById(R.id.request);
        final TextView waiting_request = (TextView)findViewById(R.id.wait_request);
        waiting_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
                reference.child(_currentUser).child(_userId).child("accept").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        request.setVisibility(View.VISIBLE);
                        waiting_request.setVisibility(View.INVISIBLE);
                    }
                });
                reference.child(_userId).child(_currentUser).child("accept").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
    }
    public void sendMessage(final String _currentUser, final String _userId)
    {
        ImageButton sendMessage =(ImageButton)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this,OneToOneChatActivity.class);
                intent.putExtra("id",_userId);
                intent.putExtra("currentUser",_currentUser);
                startActivity(intent);
            }
        });


    }
    public void setRequestAnswer(final String _currentUser, final String _userId)
    {
        final ImageButton sendMessage= (ImageButton)findViewById(R.id.send_message);
        final ImageButton accept= (ImageButton)findViewById(R.id.accept);
        final ImageButton cancel= (ImageButton)findViewById(R.id.cancel);
        final LinearLayout linay = (LinearLayout)findViewById(R.id.accep_cancel);
        final TextView request = (TextView)findViewById(R.id.request);
        final  TextView waiting_request = (TextView)findViewById(R.id.wait_request);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages")
                        .child("request");
                reference.child(_currentUser).child(_userId).child("accept").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            linay.setVisibility(View.INVISIBLE);
                            sendMessage.setVisibility(View.VISIBLE);
                            request.setVisibility(View.INVISIBLE);
                            waiting_request.setVisibility(View.INVISIBLE);
                            sendAcceptNotification(_userId,_currentUser);
                        }
                    }
                });

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages")
                        .child("request");
                reference.child(_userId).child(_currentUser).child("accept").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            linay.setVisibility(View.INVISIBLE);
                            sendMessage.setVisibility(View.INVISIBLE);
                            request.setVisibility(View.VISIBLE);
                            waiting_request.setVisibility(View.INVISIBLE);
                            sendDissmisNotification(_userId, _currentUser);
                        }
                    }
                });
            }
        });


    }
    private void sendAcceptNotification(String _userId, final String _currentUser)
    {
        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(_userId).push();
        final HashMap<String,String> newNotification = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                newNotification.put("name",dataSnapshot.child("name").getValue().toString());
                newNotification.put("send", _currentUser);
                newNotification.put("type","Senin Mesaj İsteğini Kabul Etti");
                newNotification.put("title","Mesaj");
                notification.setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            Log.w("hata", "onComplete: ",task.getException() );
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendDissmisNotification(String _userId, final String _currentUser)
    {
        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(_userId).push();
        final HashMap<String,String> newNotification = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                newNotification.put("name",dataSnapshot.child("name").getValue().toString());
                newNotification.put("send", _currentUser);
                newNotification.put("type","Senin Mesaj İsteğini Kabul Etmedi");
                newNotification.put("title","Mesaj");
                notification.setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            Log.w("hata", "onComplete: ",task.getException() );
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
