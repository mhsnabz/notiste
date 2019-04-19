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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;

public class RequestActivity extends AppCompatActivity
{ private FirebaseAuth auth;
    RecyclerView requestList;
    String currentUser;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        SetToolbar();
        requestList=(RecyclerView)findViewById(R.id.requestList);
        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new GridLayoutManager(RequestActivity.this,1));

        currentUser=getIntent().getStringExtra("currentUser");
        setList(currentUser);


    }

    private void setList(final String _currentUser)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child("requestCount").child(_currentUser);
        FirebaseRecyclerOptions<setGet> options = new FirebaseRecyclerOptions.Builder<setGet>()
                .setLifecycleOwner(this)
                .setQuery(ref,setGet.class).build();
        FirebaseRecyclerAdapter<setGet,ViewHolder> adapter = new FirebaseRecyclerAdapter<setGet, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull setGet model)
            {
                String key = getRef(position).getKey();
                holder.setImage(key);
                holder.setName(key);
                holder.setAcceptOrCancel(_currentUser,key);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_request,viewGroup,false);
                return new ViewHolder(view);
            }
        };
        requestList.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }


        public void setImage(String _key)
        {
            final CircleImageView image = (CircleImageView)itemView.findViewById(R.id.profilePic) ;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                    .resize(75,75)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(image);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void setName (String _key)
        {
            final TextView name = (TextView)itemView.findViewById(R.id.name);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setAcceptOrCancel(final String _currentUser, final String _key)
        {
            ImageButton accept = (ImageButton)itemView.findViewById(R.id.accept);
            ImageButton cancel =(ImageButton)itemView.findViewById(R.id.cancel);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child("request");
                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            reference.child(_currentUser).child(_key).child("accept").setValue("true")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(RequestActivity.this,"İstek Kabul Edildi",Toast .LENGTH_LONG).show();
                                                sendAcceptNotification(_key,_currentUser);
                                                setMessage(_key,_currentUser);
                                                deleteRequestCount(_key,_currentUser);
                                            }
                                        }
                                    });
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(RequestActivity.this,"İstek Kabul Edilmedi",Toast .LENGTH_LONG).show();
                            sendDissmisNotification(_key,_currentUser);
                            deleteRequestCount(_key,_currentUser);
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
        actionBar.setTitle("Mesaj İstekleri");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    else if (dataSnapshot.child("messagesRequest").getValue().toString().equals("false"))
                        return;
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
    private void setMessage(String _userId,String _currentUser)
    {
        Map<String,Object> map = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child("messegasTime");
        map.put("seen","false");
        map.put("time", Calendar.getInstance().getTimeInMillis());
        databaseReference.child(_userId).child(_currentUser).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (!task.isSuccessful())
                {
                    Log.w("setMessage", "onComplete: ",task.getException() );
                }
            }
        });
        databaseReference.child(_currentUser).child(_userId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (!task.isSuccessful())
                {
                    Log.w("setMessage", "onComplete: ",task.getException() );
                }
            }
        });

    }
    private void deleteRequestCount(String _userId,String _currentUser)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child("requestCount");
               databaseReference.child(_currentUser).child(_userId).removeValue();
    }
}
