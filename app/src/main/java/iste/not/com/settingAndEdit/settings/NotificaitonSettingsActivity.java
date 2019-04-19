package iste.not.com.settingAndEdit.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import iste.not.com.R;

public class NotificaitonSettingsActivity extends AppCompatActivity
{


    Toolbar toolbar;
    SwitchCompat messageSwitch,commentSwitch,messageRequestSwitch,noteSwicth,noticesSwitch,askSwitch,likeSwitch;
    DatabaseReference databaseReference,reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaiton_settings);
        currentUser=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("NotificationSettings")
                .child(getIntent().getStringExtra("currentUser"));
        reference=FirebaseDatabase.getInstance().getReference().child("NotificationSettings")
                .child(getIntent().getStringExtra("currentUser"));
        messageSwitch=(SwitchCompat)findViewById(R.id.messageSwitch);
        commentSwitch=(SwitchCompat)findViewById(R.id.commentSwitch);
        noteSwicth=(SwitchCompat)findViewById(R.id.noteSwicth);
        noticesSwitch=(SwitchCompat)findViewById(R.id.noticesSwitch);
        likeSwitch=(SwitchCompat)findViewById(R.id.likeSwitch);
        askSwitch=(SwitchCompat)findViewById(R.id.askSwitch);
        messageRequestSwitch=(SwitchCompat)findViewById(R.id.messageRequestSwitch);
        setMessageSwitch("messages",messageSwitch);
        setmessageRequestSwitch("messagesRequest",messageRequestSwitch);
        setcommentSwitch("comment",commentSwitch);
        setnoteSwicth("notes",noteSwicth);
        setnoticesSwitch("notices",noticesSwitch);
        setaskSwitch("ask",askSwitch);
        setlikeSwitch("like",likeSwitch);

        commentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("comment").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("comment").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
        messageRequestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("messagesRequest").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("messagesRequest").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
        messageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    databaseReference.child("messages").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("messages").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
        noteSwicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("notes").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("notes").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
        noticesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("notices").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("notices").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }

        });
        likeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("like").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("like").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
        askSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    databaseReference.child("ask").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    });
                else if (!isChecked)
                {
                    databaseReference.child("ask").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }

            }
        });
        SetToolbar();
    }

    private void setlikeSwitch(final String like, final SwitchCompat likeSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(like).exists()) {
                    if (dataSnapshot.child(like).getValue().toString().equals("true"))
                        likeSwitch.setChecked(true);
                        else if (dataSnapshot.child(like).getValue().toString().equals("false"))
                        likeSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setaskSwitch(final String ask, final SwitchCompat askSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(ask).exists()) {
                    if (dataSnapshot.child(ask).getValue().toString().equals("true"))
                        askSwitch.setChecked(true);
                    else if (dataSnapshot.child(ask).getValue().toString().equals("false"))
                        askSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setnoticesSwitch(final String notices, final SwitchCompat noticesSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(notices).exists()) {
                    if (dataSnapshot.child(notices).getValue().toString().equals("true"))
                        noticesSwitch.setChecked(true);
                    else if (dataSnapshot.child(notices).getValue().toString().equals("false"))
                        noticesSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setnoteSwicth(final String notes, final SwitchCompat noteSwicth) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(notes).exists()) {
                    if (dataSnapshot.child(notes).getValue().toString().equals("true"))
                        noteSwicth.setChecked(true);
                    else if (dataSnapshot.child(notes).getValue().toString().equals("false"))
                        noteSwicth.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setmessageRequestSwitch(final String messages, final SwitchCompat messageSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(messages).exists()) {
                    if (dataSnapshot.child(messages).getValue().toString().equals("true"))
                        messageSwitch.setChecked(true);
                    else if (dataSnapshot.child(messages).getValue().toString().equals("false"))
                        messageSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setcommentSwitch(final String messages, final SwitchCompat messageSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(messages).exists()) {
                    if (dataSnapshot.child(messages).getValue().toString().equals("true"))
                        messageSwitch.setChecked(true);
                    else if (dataSnapshot.child(messages).getValue().toString().equals("false"))
                        messageSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setMessageSwitch(final String messages, final SwitchCompat messageSwitch)
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(messages).exists()) {
                    if (dataSnapshot.child(messages).getValue().toString().equals("true"))
                        messageSwitch.setChecked(true);
                    else if (dataSnapshot.child(messages).getValue().toString().equals("false"))
                        messageSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void SetToolbar()
    {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inLayoutInflater.inflate(R.layout.toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("Anlık Bildirim Ayarları");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
