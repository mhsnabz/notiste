package iste.not.com.Messages;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Profile.NotificationActivity;
import iste.not.com.R;

import static android.content.ContentValues.TAG;

public class CreateNewMessageActivity extends AppCompatActivity {

    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference databaseReference,reference;
    Toolbar toolbar;
    MaterialEditText ara;
    RecyclerView users;
    Query ref;
    String currentUser;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_message);
        currentUser=getIntent().getStringExtra("current_user");
        ara=(MaterialEditText)findViewById(R.id.ara);
        users=(RecyclerView)findViewById(R.id.users);
        users.setHasFixedSize(true);
        users.setLayoutManager(new GridLayoutManager(CreateNewMessageActivity.this,1));
        users.addItemDecoration(new DividerItemDecoration(CreateNewMessageActivity.this, DividerItemDecoration.VERTICAL));
        SetToolbar();
        //SetUserRec();

        ara.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = ara.getText().toString();
                Log.d(TAG, "onTextChanged: " + name);
                SetUserRec(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void SetUserRec(String _name)
    {
        ref=FirebaseDatabase.getInstance().getReference().child("Students").orderByChild("name").startAt(_name).endAt(_name+"\uf8ff");
        FirebaseRecyclerOptions<UserSetGet> options = new FirebaseRecyclerOptions.Builder<UserSetGet>()
                .setLifecycleOwner(this)
                .setQuery(ref,UserSetGet.class)
                .build();
        FirebaseRecyclerAdapter<UserSetGet,ViewHolder> adapter= new FirebaseRecyclerAdapter<UserSetGet, ViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final UserSetGet model)
            {
                String key = getRef(position).getKey();

                if (!getRef(position).getKey().equals(currentUser))
                {
                    holder.setImage(model.getImage());
                    holder.setName(model.getName());
                    holder.setMajor(model.getMajor());
                    holder.sendRequest(currentUser,key);
                    holder.removeBlocked(currentUser,key);
                   // holder.setRequestVisibility(currentUser,key);
                    holder.dismissReques(currentUser,key);
                    holder.acceptOrCancel(currentUser,key);
                    holder.sendMessage(currentUser,key);
                    holder.setRequestAnswer(currentUser,key);

                }

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.new_chat_user,viewGroup,false);
                return new CreateNewMessageActivity.ViewHolder(view);
            }
        };
        users.setAdapter(adapter);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {   View view;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view=itemView;
        }
        public void setRequestAnswer(final String _currentUser, final String _userId)
        {
            final ImageButton sendMessage= (ImageButton)itemView.findViewById(R.id.send_message);
            final ImageButton accept= (ImageButton)itemView.findViewById(R.id.accept);
            final ImageButton cancel= (ImageButton)itemView.findViewById(R.id.cancel);
            final LinearLayout linay = (LinearLayout)itemView.findViewById(R.id.accep_cancel);
            final TextView request = (TextView)itemView.findViewById(R.id.request);
            final  TextView waiting_request = (TextView)itemView.findViewById(R.id.wait_request);
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

                                   Map<String,Object> map = new HashMap<>();
                                   linay.setVisibility(View.INVISIBLE);
                                   sendMessage.setVisibility(View.VISIBLE);
                                   request.setVisibility(View.INVISIBLE);
                                   waiting_request.setVisibility(View.INVISIBLE);
                                   sendAcceptNotification(_userId,_currentUser);
                                   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime");
                                   map.put("time",-1*Calendar.getInstance().getTimeInMillis());
                                   map.put("seen","false");
                                   databaseReference.child(_userId).child(_currentUser).updateChildren(map);
                                   databaseReference.child(_currentUser).child(_userId).updateChildren(map);

                                   DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Messages").child("requestQount")
                                           .child(_currentUser);
                                   databaseReference1.child(_userId).removeValue();


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

                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Messages").child("requestQount")
                                        .child(_currentUser);
                                databaseReference1.child(_userId).removeValue();
                            }
                        }
                    });
                }
            });


        }
        public void acceptOrCancel(final String _currentUser, final String _userId)
        {
            final ImageButton sendMessage= (ImageButton)itemView.findViewById(R.id.send_message);

            final LinearLayout linay = (LinearLayout)itemView.findViewById(R.id.accep_cancel);
            final TextView request = (TextView)itemView.findViewById(R.id.request);
            final ImageButton blocked= (ImageButton)itemView.findViewById(R.id.blocked);
            final TextView remove_blocked = (TextView)itemView.findViewById(R.id.remove_blocked);
            final  TextView waiting_request = (TextView)itemView.findViewById(R.id.wait_request);
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
                                        remove_blocked.setVisibility(View.INVISIBLE);
                                        blocked.setVisibility(View.INVISIBLE);
                                    } else if (dataSnapshot.child("accept").getValue().toString()
                                            .equals("false") && ds.child("accept").getValue().toString()
                                            .equals("true")) {
                                        request.setVisibility(View.INVISIBLE);
                                        waiting_request.setVisibility(View.VISIBLE);
                                        linay.setVisibility(View.INVISIBLE);
                                        sendMessage.setVisibility(View.INVISIBLE);
                                        remove_blocked.setVisibility(View.INVISIBLE);
                                        blocked.setVisibility(View.INVISIBLE);
                                    } else if (dataSnapshot.child("accept").getValue().toString()
                                            .equals("true") && ds.child("accept").getValue().toString()
                                            .equals("false")) {
                                        request.setVisibility(View.INVISIBLE);
                                        waiting_request.setVisibility(View.INVISIBLE);
                                        linay.setVisibility(View.VISIBLE);
                                        sendMessage.setVisibility(View.INVISIBLE);
                                        remove_blocked.setVisibility(View.INVISIBLE);
                                        blocked.setVisibility(View.INVISIBLE);
                                    }
                                    else if (dataSnapshot.child("accept").getValue().toString()
                                            .equals("false") && ds.child("accept").getValue().toString()
                                            .equals("false")) {
                                        request.setVisibility(View.VISIBLE);
                                        waiting_request.setVisibility(View.INVISIBLE);
                                        linay.setVisibility(View.INVISIBLE);
                                        sendMessage.setVisibility(View.INVISIBLE);
                                        remove_blocked.setVisibility(View.INVISIBLE);
                                        blocked.setVisibility(View.INVISIBLE);
                                    }
                                    else if (ds.child("accept").getValue().toString()
                                            .equals("blocked"))
                                    {
                                        request.setVisibility(View.INVISIBLE);
                                        waiting_request.setVisibility(View.INVISIBLE);
                                        linay.setVisibility(View.INVISIBLE);
                                        sendMessage.setVisibility(View.INVISIBLE);
                                        remove_blocked.setVisibility(View.VISIBLE);
                                        blocked.setVisibility(View.INVISIBLE);
                                    }
                                    else if (dataSnapshot.child("accept").getValue().toString()
                                            .equals("blocked"))
                                    {
                                        request.setVisibility(View.INVISIBLE);
                                        waiting_request.setVisibility(View.INVISIBLE);
                                        linay.setVisibility(View.INVISIBLE);
                                        sendMessage.setVisibility(View.INVISIBLE);
                                        blocked.setVisibility(View.VISIBLE);
                                        remove_blocked.setVisibility(View.INVISIBLE);
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
        public void setRequestVisibility(final String _currentUser, final String _userId)
        {
            final ImageButton sendMessage= (ImageButton)itemView.findViewById(R.id.send_message);
            final LinearLayout linay = (LinearLayout)itemView.findViewById(R.id.accep_cancel);
            final TextView request = (TextView)itemView.findViewById(R.id.request);
            final  TextView waiting_request = (TextView)itemView.findViewById(R.id.wait_request);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(_currentUser).child(_userId).child("accept").getValue().equals("true")
                            &&
                            dataSnapshot.child(_currentUser).child(_userId).child("accept").getValue().equals("true")
                            )
                    {
                        request.setVisibility(View.INVISIBLE);
                        waiting_request.setVisibility(View.INVISIBLE);
                        linay.setVisibility(View.INVISIBLE);
                        sendMessage.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        request.setVisibility(View.VISIBLE);
                        waiting_request.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        linay.setVisibility(View.INVISIBLE);
                        sendMessage.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void sendRequest(final String _currentUser, final String _userId)
        {

            final TextView request = (TextView)itemView.findViewById(R.id.request);
          final  TextView waiting_request = (TextView)itemView.findViewById(R.id.wait_request);
          if (_currentUser.equals(_userId)){
              request.setVisibility(View.GONE);
              waiting_request.setVisibility(View.GONE);

          }
            request.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child("requestCount");
                    databaseReference.child(_userId).child(_currentUser).push().child("yeni").setValue("new request");

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
                    reference.child(_currentUser).child(_userId).child("accept").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            request.setVisibility(View.INVISIBLE);
                            waiting_request.setVisibility(View.VISIBLE);
                            sendMessageRequest(_userId,_currentUser);
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
            final TextView request = (TextView)itemView.findViewById(R.id.request);
            final TextView waiting_request = (TextView)itemView.findViewById(R.id.wait_request);
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
        public void removeBlocked(final String _currentUser, final String _userId)
        {

            final TextView remove_blocked = (TextView)itemView.findViewById(R.id.remove_blocked);

            remove_blocked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
                    reference.child(_currentUser).child(_userId).child("accept").setValue("false");
                    reference.child(_userId).child(_userId).child("accept").setValue("false");

                }
            });
        }
        public void setImage(String _image)
        {

                CircleImageView image = (CircleImageView) itemView.findViewById(R.id.profilePic);
                Picasso.get().load(_image).resize(75, 75).centerCrop()
                        .placeholder(R.drawable.placeholder_image).into(image);

        }
        public void setName(String _name)
        {
            TextView name =(TextView)itemView.findViewById(R.id.name);

            name.setText(_name);
        }
        public void setMajor(String _major)
        {
            TextView major =(TextView)itemView.findViewById(R.id.major);
            if (_major.equals("ComputerEngineering"))
                    major.setText("Bilgisayar Mühendisliği");
            if (_major.equals("electronicEngineer"))
                    major.setText("Elektirik ve Elektronik Mühendisliği");

        }
        public void sendMessage(final String _currentUser, final String _userId)
        {
            ImageButton sendMessage =(ImageButton)itemView.findViewById(R.id.send_message);
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CreateNewMessageActivity.this,OneToOneChatActivity.class);
                    intent.putExtra("id",_userId);
                    intent.putExtra("currentUser",_currentUser);
                    startActivity(intent);
                }
            });


        }
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
        actionBar.setTitle("Öğrenci Ara");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        online("true");
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

    private void sendDissmisNotification(final String _userId, final String _currentUser)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("NotificationSettings").child(_userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("messagesRequest").exists())
                {
                    if (dataSnapshot.child("messagesRequest").getValue().toString().equals("true"))
                    {final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(_userId).push();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendAcceptNotification(final String _userId, final String _currentUser)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("NotificationSettings").child(_userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("messagesRequest").exists())
                {
                    if (dataSnapshot.child("messagesRequest").getValue().toString().equals("true"))
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
                        });;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendMessageRequest(final String _userId, final String _currentUser)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("NotificationSettings").child(_userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("messagesRequest").exists())
                {
                    if (dataSnapshot.child("messagesRequest").getValue().toString().equals("true"))
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
                                newNotification.put("type","Size Mesaj Göndermek İstiyor");
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
                    else if (dataSnapshot.child("messagesRequest").getValue().toString().equals("false"))
                        return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}




