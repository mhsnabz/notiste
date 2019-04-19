package iste.not.com.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.AskNoteFragment;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.POST.CommentHelper.SwipeController;
import iste.not.com.POST.CommentHelper.SwipeControllerActions;
import iste.not.com.POST.SingleLessonsPostActivity;
import iste.not.com.Profile.ScientistPosted.PostActivity;
import iste.not.com.Profile.notificaitonActivities.NotificaitionActiviesActivity;
import iste.not.com.R;

public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    String major,current_user;
    TextView notficationCount,deleteAllNotificaitions;
    SwipeController swipeController = null;
    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
    DatabaseReference databaseReference,reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        SetToolbar();
        deleteAllNotificaitions=(TextView)findViewById(R.id.deleteAllNotificaiton);

        recyclerView=(RecyclerView)findViewById(R.id.notificationRec);
        major=getIntent().getStringExtra("major");
        current_user=getIntent().getStringExtra("current_user");
        notficationCount=(TextView)findViewById(R.id.notificationText);
        setNotificationCount(major,current_user,notficationCount);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(NotificationActivity.this,1));
        recyclerView.addItemDecoration(new DividerItemDecoration(NotificationActivity.this, DividerItemDecoration.VERTICAL));
        setRecylerView();
        deleteAllNotificaitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(major).child("databaseNotifications");
                databaseReference.child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("deletedNotificaiton", "onComplete: "+"bildirimler silindi");
                            Toast.makeText(NotificationActivity.this,"Bütün Bildirimler Silindi",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void setRecylerView()
    {
        Query ref = FirebaseDatabase.getInstance().getReference().child(major)
                .child("databaseNotifications")
                .child(current_user).child("AllNotification").orderByChild("time");
        FirebaseRecyclerOptions<GetterSetter> options
                =new FirebaseRecyclerOptions.Builder<GetterSetter>()
                .setQuery(ref,GetterSetter.class)
                .setLifecycleOwner(this)
                .build();
     FirebaseRecyclerAdapter<GetterSetter,ViewHolder> adapter
                = new FirebaseRecyclerAdapter<GetterSetter, ViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final GetterSetter model)
            {
                String key = model.getKey();
                String lessonKey = model.getLessonKey();
                String lessonName = model.getLessonName();
                String query = model.getQuery();
                String seen = model.getSeen();
                String sender = model.getSender();
                String senderUid = model.getSenderUid();
                String title = model.getTitle();
                String type = model.getType();
                holder.setProfileImage(model.getSenderUid());
                holder.setName(model.getSender());
                holder.setType(model.getType());
               holder.convertime(model.getTime());
                holder.setSeen(model.getSeen());

                if (model.getQuery().equals("Public_ASK")|| model.getQuery().equals("Public_POST")) {
                    holder.rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(NotificationActivity.this, SingleLessonsPostActivity.class);
                            intent.putExtra("current_user_id", current_user);
                           // intent.putExtra("current_user_id", getIntent().getStringExtra("current_user"));
                            Log.d("currentUSEEr", "onClick: " + getIntent().getStringExtra("current_user"));
                            intent.putExtra("key", model.getKey());
                            intent.putExtra("major", major);
                            intent.putExtra("query", model.getQuery());
                            intent.putExtra("user_id", model.getSenderUid());
                            intent.putExtra("count", model.getCount());
                            intent.putExtra("lessonName", model.getLessonName());
                            intent.putExtra("context", "Notification.java");
                            startActivity(intent);

                            if (model.getSeen().equals("false")) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(major)
                                        .child("databaseNotifications")
                                        .child(current_user)
                                        .child("NotificationCount");
                                reference.child(model.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            setSeen(major, "databaseNotifications", current_user, "AllNotification", getRef(position).getKey());
                                            setNotificationCount(major, current_user, notficationCount);
                                            Log.d("notifcaitonIsSeen", "onComplete: " + "tamamlandı");
                                            Intent intent = new Intent(NotificationActivity.this, SingleLessonsPostActivity.class);
                                            intent.putExtra("currentUser", current_user);
                                            intent.putExtra("key", model.getKey());
                                            intent.putExtra("major", major);
                                            intent.putExtra("query", model.getQuery());
                                          //  intent.putExtra("userID", model.getUserId());
                                            intent.putExtra("count", model.getCount());
                                            intent.putExtra("user_id", model.getSenderUid());
                                            model.setSeen("true");
                                            startActivity(intent);


                                        } else
                                            Log.w("notifcaitonIsSeen", "onComplete: ", task.getException());


                                    }
                                });
                            }
                        }
                    });
                }
                if (model.getQuery().equals("Teacher_Post"))
                {
                    holder.rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //key,count,major,teacherId,userId,lessonKey,
                            Intent intent = new Intent(NotificationActivity.this, PostActivity.class);
                            intent.putExtra("key",model.getKey());
                            intent.putExtra("count",model.getCount());
                            intent.putExtra("major",major);
                            intent.putExtra("teacherId",model.getSenderUid());
                            //intent.putExtra()
                            intent.putExtra("lessonKey",model.getLessonKey());
                            startActivity(intent);
                        }
                    });

                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_notification_layout,viewGroup,false);
                  return new ViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        public ViewHolder(@NonNull View itemView)
        {
                super(itemView);
                rootView=itemView;

        }

        public void setProfileImage(final String _senderUid)
        {
            final CircleImageView image = (CircleImageView)itemView.findViewById(R.id.profileImage);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students")
                    .child(_senderUid);
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child(major).child("Teacher")
                    .child(_senderUid);
            reference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                             {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                            .resize(75,75)
                            .centerCrop().into(image);
                             }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                                .resize(75,75)
                                .centerCrop().into(image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setName(String _name)
        {
            TextView name =(TextView)itemView.findViewById(R.id.username);
            name.setText(_name);
        }
        public void setType(String _type)
        {
            TextView type= (TextView)itemView.findViewById(R.id.type);
            type.setText(_type);
        }
        public void setSeen(String seen)
        {
            TextView view = (TextView)itemView.findViewById(R.id.seen);
            if (seen.equals("true"))
            {
                view.setVisibility(View.INVISIBLE);
            }
            else
            {
                view.setVisibility(View.VISIBLE);
            }
        }
        public void convertime(Long milliseconds)
        {
            String dakika;
            TextView _time = (TextView)itemView.findViewById(R.id.time);

            int seconds = (int) (-milliseconds / 1000) % 60 ;
            int minutes = (int) ((-milliseconds / (1000*60)) % 60);
            int hours   = (int) ((-milliseconds / (1000*60*60)) % 24);
            if (minutes<=9)
            {
             dakika = "0"+String.valueOf(minutes);
            }
            else
            {
               dakika = String.valueOf(minutes);
            }
            String saat = String.valueOf(3+hours);
            String saniye = String.valueOf(seconds);
            String time= saat+":"+dakika;
            _time.setText(time);

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
        actionBar.setTitle("Bildirimler");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setSeen(String _major,String _databaseNotifications,String _currentUserId,String _AllNotification,String _holderKey)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child(_databaseNotifications)
                .child(_currentUserId)
                .child(_AllNotification)
                .child(_holderKey);
        reference.child("seen").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Log.d("SetSeen", "onComplete: "+"Seen false->true");
                }
                else
                    Log.w("SetSeen", "onComplete: ", task.getException());
            }
        });
    }
    private void setNotificationCount(String _major, String _currentUser, final TextView _text)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("databaseNotifications")
                .child(_currentUser)
                .child("NotificationCount");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0)
                {                    _text.setVisibility(View.VISIBLE);

                    _text.setText(Long.toString(dataSnapshot.getChildrenCount() ) +" Tane Yeni Bildirim");

                }
                else _text.setText("Yeni Bildirim Yok");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        return;

    }
}
