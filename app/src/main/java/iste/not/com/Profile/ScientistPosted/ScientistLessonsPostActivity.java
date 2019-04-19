package iste.not.com.Profile.ScientistPosted;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.POST.CommentHelper.CommentHelper;
import iste.not.com.POST.ImageLoaderActivity;
import iste.not.com.R;

public class ScientistLessonsPostActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    String teacherId;
    TextView yorum;
    CircleImageView profilePic;
    TextView teacherName,priotiry,lessonName;
    String major;
    long count;
    String senderName,pushId;
    RecyclerView notes,commentRec;
    MaterialEditText comment_editText;
    RecyclerView.LayoutManager manager;
    ArrayList<String> images;
     String userid;
     ImageButton sendButton;
    String currentUser;

   FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

   CommentScientistAdapter adapter;
    List<CommentHelper> comment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_lessons_post);
        lessonName=(TextView)findViewById(R.id.lessonName);
        profilePic=(CircleImageView)findViewById(R.id.profileimage) ;
        teacherName=(TextView)findViewById(R.id.name);
        priotiry=(TextView)findViewById(R.id.priotiry) ;
        count=getIntent().getLongExtra("count",count);
        major=getIntent().getStringExtra("major");
        teacherId=getIntent().getStringExtra("teacherId");
        userid=getIntent().getStringExtra("user_id");
        currentUser=firebaseUser.getUid();
        commentRec=(RecyclerView)findViewById(R.id.commentRec);
        comment_editText=(MaterialEditText)findViewById(R.id.comment_edittext);
        notes=(RecyclerView)findViewById(R.id.data_recyclerView);
        notes.setHasFixedSize(true);
        yorum=(TextView)findViewById(R.id.yorum);
        yorum.setText(getIntent().getStringExtra("comment"));
        adapter= new CommentScientistAdapter(comment,major,teacherId,getIntent().getStringExtra("key"),getIntent().getStringExtra("lessonKey"));
        commentRec.setAdapter(adapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        commentRec.setHasFixedSize(true);
        commentRec.setLayoutManager(manager1);
        images= new ArrayList<>();
        sendButton=(ImageButton) findViewById(R.id.sendButton);
        setToolbar(profilePic,teacherName,priotiry);
        setCommentLayout(getIntent().getStringExtra("key"),teacherId,getIntent().getStringExtra("lessonKey"));
        if (count<=4)
        {
            manager= new GridLayoutManager(ScientistLessonsPostActivity.this,1,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }
        else if (5 <count && count<=8)
        {
            manager= new GridLayoutManager(ScientistLessonsPostActivity.this,2,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }
        else  if (9 <count && count<=12)
        {
            manager= new GridLayoutManager(ScientistLessonsPostActivity.this,3,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }
        else if (13<count)
        {
            manager= new GridLayoutManager(ScientistLessonsPostActivity.this,4,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(major).child("Lessons")
                .child(getIntent().getStringExtra("lessonKey"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonName.setText(dataSnapshot.child("lessonName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setDatas(major,teacherId,getIntent().getStringExtra("lessonKey"),getIntent().getStringExtra("key"));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(
                        getIntent().getStringExtra("key"),
                        currentUser,
                        teacherId,getIntent().getStringExtra("lessonKey")
                );
            }
        });
    }
    private void sendComment(final String key, final String current_user,String _teacherId,String _lessonKey)
    {
        String comment = comment_editText.getText().toString();
        if (!TextUtils.isEmpty(comment))
        {
            String sendTime = String.valueOf(-1* Calendar.getInstance().getTimeInMillis());
            HashMap<String,String> timeMap = new HashMap();
            timeMap.put("sendTime",sendTime);
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(major)
                    .child("Teacher")
                    .child(_teacherId)
                    .child("myLessons")
                    .child(_lessonKey)
                    .child("Post")
                    .child(key).child("comments").push();

            Map messageMap = new HashMap();
            messageMap.put("comment",comment);
            messageMap.put("from",current_user);
            Log.d("currennnntuser", "sendComment: "+current_user);
            messageMap.put("sendTime",sendTime);
            reference.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(userid).push();
                        final HashMap<String,String> newNotification = new HashMap<>();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(current_user);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                {

                                    senderName=dataSnapshot.child("name").getValue().toString();
                                    newNotification.put("name",dataSnapshot.child("name").getValue().toString());
                                    newNotification.put("send", userid);
                                    newNotification.put("type","Senin Gönderine Yorum Yaptı");
                                    newNotification.put("title","YORUM");
                                    notification.setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getRandomString(20);
                                         //   setDatabaseNotification(current_user, key, userid, senderName, "Senin Gönderine Yorum Yaptı", major, pushId, _lessonName, "YORUM", query, userid, count);
                                        }

                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        comment_editText.setText("");
                    }
                }
            });


        }
    }

    private void setDatas(String _major,String _teacherId,String _lessonKey,String _key)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("Teacher")
                .child(_teacherId)

                .child("myLessons")
                .child(_lessonKey)
                .child("Post")
                .child(_key)
                .child("data");

        FirebaseRecyclerOptions<Notes> options = new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(databaseReference,Notes.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Notes,NotesViewHolder> adapter=  new FirebaseRecyclerAdapter<Notes, NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder holder, final int position, @NonNull final Notes model) {
                holder.setDatas(model.getImage());
                Log.d("images_bind", "onBindViewHolder: "+model.getImage());
                images.add(model.getImage());


                holder.view.findViewById(R.id.datas).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent i = new Intent(ScientistLessonsPostActivity.this, ImageLoaderActivity.class);
                        if (images.size()>0) {
                            i.putExtra("list", images);
                            i.putExtra("key", getRef(position).getKey().toString());
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_lesson_post_view_holder,viewGroup,false);


                return new NotesViewHolder(view);

            }

        };
        notes.setAdapter(adapter);
    }
    public class NotesViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public NotesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view=itemView;
        }
        public void setDatas(final String url)
        {
            final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressbar) ;
            final ImageView view = (ImageView)itemView.findViewById(R.id.datas);
            Picasso.get().load(url).resize(150,150).centerCrop().placeholder(R.drawable.images).networkPolicy(NetworkPolicy.OFFLINE).into(view, new Callback() {
                @Override
                public void onSuccess()
                {
                    progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e)
                {
                    Picasso.get().load(url).resize(150,150).centerCrop().placeholder(R.drawable.images).into(view, new Callback() {
                        @Override
                        public void onSuccess()
                        {
                            progressbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e)
                        {
                            Log.e("error_bind", "onError: ",e.getCause() );
                        }
                    });
                    Log.e("error_bind", "onError: ",e.getCause() );
                }
            });
        }
    }
    private void setToolbar(final CircleImageView pro_pic, final TextView _name, final TextView _priotiry)
    {
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(teacherId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .resize(512,512)
                        .centerCrop()
                        .placeholder(R.drawable.scientist)
                        .into(pro_pic, new Callback() {
                            @Override
                            public void onSuccess()
                            {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                _name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());

                if(dataSnapshot.child("priority").getValue()!=null)
                { _priotiry.setText("#"+dataSnapshot.child("priority").getValue().toString());
                }
                else
                    _priotiry.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d("randomStrign", "getRandomString: "+sb.toString());
        pushId=sb.toString();
        return sb.toString();
    }
    public void setDatabaseNotification(String _currentUserID, String _lessonKey, final String _getterUserId, String _username
            , String _type, final String _major, final String _pushId, String _lessonName, String _title
            , String _query,String _userId,long _count)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("databaseNotifications")
                .child(_getterUserId).child("AllNotification");
        HashMap<String , Object >map = new HashMap<>();
        map.put("key",_pushId);
        map.put("type",_type);
        map.put("sender",_username);
        map.put("senderUid",_currentUserID);
        map.put("lessonKey",_lessonKey);
        map.put("lessonName",_lessonName);
        map.put("title",_title);
        map.put("query",_query);
        map.put("seen","false");
        map.put("userId",_userId);
        map.put("count",_count);
        map.put("getterUid",_userId);
        map.put("time",-1*Calendar.getInstance().getTimeInMillis());
        reference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.d("databaseNotification", "onComplete: "+"Eklendi ");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("databaseNotifications")
                            .child(_getterUserId).child("NotificationCount");
                    reference.child(_pushId).child("New Notification").setValue("yeni").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                                Log.d("notificationCount", "onComplete: "+"Eklendi ");
                            else
                                Log.w("notificationCount", "onComplete: ",task.getException() );

                        }
                    });
                }
                else
                    Log.w("databaseNotification", "onComplete: ",task.getException() );
            }
        });


    }


    private void setCommentLayout(String _key,String _teacherId,String _lessonKey)
    {

        final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(major)
                .child("Teacher")
                .child(_teacherId)
                .child("myLessons")
                .child(_lessonKey)
                .child("Post")
                .child(_key).child("comments");
        commentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentHelper helper = dataSnapshot.getValue(CommentHelper.class);
                comment.add(helper);
                adapter.notifyDataSetChanged();
                commentRec.scrollToPosition(comment.size()-1);
                //commentRef.child(dataSnapshot.getKey()).removeValue();
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

}
