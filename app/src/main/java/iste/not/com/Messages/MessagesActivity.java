package iste.not.com.Messages;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;
import iste.not.com.ScientistOneToOneChat.ScientistOneToOneChatActivity;

public class MessagesActivity extends AppCompatActivity
{
    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
    DatabaseReference databaseReference,reference;
    ImageButton newMessage;
    Toolbar toolbar;
    TextView messagesRequest;
    RecyclerView messagesRecylerView;
    String NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        messagesRecylerView=(RecyclerView)findViewById(R.id.messages);
        messagesRequest=(TextView)findViewById(R.id.messagesRequest);

        messagesRecylerView.setHasFixedSize(true);
        messagesRecylerView.setLayoutManager(new GridLayoutManager(MessagesActivity.this,1));
        currentUser=getIntent().getStringExtra("current_user");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        newMessage=(ImageButton)findViewById(R.id.messageButton);
        SetMessagesRequest(currentUser,messagesRequest);
        messagesRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MessagesActivity.this,RequestActivity.class);
                intent.putExtra("currentUser",currentUser);
                startActivity(intent);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        newMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagesActivity.this,CreateNewMessageActivity.class);
                intent.putExtra("current_user",currentUser);
                startActivity(intent);
            }
        });
        getteMessages();
    }
    private void getteMessages()
    {
        Query reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime").child(currentUser).orderByChild("time");

        FirebaseRecyclerOptions<MessagesSendTimeHelper> options = new FirebaseRecyclerOptions.Builder<MessagesSendTimeHelper>()
                .setLifecycleOwner(this)
                .setQuery(reference,MessagesSendTimeHelper.class)

                .build();
        FirebaseRecyclerAdapter<MessagesSendTimeHelper,ViewHolder> adapter = new FirebaseRecyclerAdapter<MessagesSendTimeHelper, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final MessagesSendTimeHelper model)
            {
                final String key = getRef(position).getKey();
                Log.d("deneme", "onBindViewHolder: "+key);
             holder.setProfileImage(key);

             holder.setNotficationCount(currentUser,key);

             holder.view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view)
                 {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(key).exists())
                            {
                                Intent intent = new Intent(MessagesActivity.this, ScientistOneToOneChatActivity.class);
                                intent.putExtra("id",key);
                                intent.putExtra("currentUser",currentUser);
                                holder.deleteDatabaseNotification(currentUser,key);
                                model.setSeen("true");
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(MessagesActivity.this,OneToOneChatActivity.class);
                                intent.putExtra("id",key);
                                intent.putExtra("currentUser",currentUser);

                                holder.deleteDatabaseNotification(currentUser,key);
                                model.setSeen("true");

                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                 }
             });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_new_message_layout,viewGroup,false);
                return new MessagesActivity.ViewHolder(view);
            }
        };

        messagesRecylerView.setAdapter(adapter);
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setProfileImage(final String _userId)
        {
            final TextView name = (TextView)itemView.findViewById(R.id.name);
            final CircleImageView profilePic=(CircleImageView)itemView.findViewById(R.id.profilePic);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(_userId).exists())
                    {

                        String image = dataSnapshot.child(_userId).child("image").getValue().toString();
                        Picasso.get().load(image).resize(75,75).placeholder(R.drawable.placeholder_image)
                                .centerCrop().into(profilePic);
                        name.setText(dataSnapshot.child(_userId).child("name").getValue().toString());
                    }
                    else
                    {
                        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses").child(_userId);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String image = dataSnapshot.child(_userId).child("image").getValue().toString();
                                Picasso.get().load(image).resize(75,75).placeholder(R.drawable.scientist_black)
                                        .centerCrop().into(profilePic);
                                name.setText(dataSnapshot.child(_userId).child("name").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setNotficationCount(String _currentUser,String _userId)
        {
            final TextView notificationCount =(TextView)itemView.findViewById(R.id.notificationCount);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DatabaseMessagesNotifcation")
                    .child(_currentUser).child(_userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                            long count = dataSnapshot.getChildrenCount();
                            if (count==0)
                                notificationCount.setVisibility(View.INVISIBLE);
                           else if (count<=9)
                            {
                                notificationCount.setVisibility(View.VISIBLE);
                                notificationCount.setText("+"+Long.toString(count));
                            }

                            else
                            {
                                notificationCount.setVisibility(View.VISIBLE);
                                notificationCount.setText(Long.toString(count));
                            }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void deleteDatabaseNotification(String _currentUser,String _userId)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DatabaseMessagesNotifcation")
                    .child(_currentUser).child(_userId);
            reference.removeValue();
        }
        public void setSeen (String _currentUser,String _userId)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime")
                    .child(_currentUser).child(_userId);
            reference.child("seen").setValue("true");
        }

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

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
    private void SetMessagesRequest(String _currentUser, final TextView _requestTextView)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Messages").child("requestCount").child(_currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue()!=null)
                {
                    _requestTextView.setText("+"+Long.toString(dataSnapshot.getChildrenCount())+" Mesaj İsteği");
                    _requestTextView.setVisibility(View.VISIBLE);
                }
                else
                    _requestTextView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
