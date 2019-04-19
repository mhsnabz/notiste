package iste.not.com.Profile.notificaitonActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.POST.CommentHelper.CommentAdapter;
import iste.not.com.POST.CommentHelper.CommentHelper;
import iste.not.com.POST.CommentHelper.SwipeController;
import iste.not.com.POST.CommentHelper.SwipeControllerActions;
import iste.not.com.POST.ImageLoaderActivity;
import iste.not.com.POST.SingleLessonsPostActivity;
import iste.not.com.Profile.NotificationActivity;
import iste.not.com.R;

public class NotificaitionActiviesActivity extends AppCompatActivity {
    String  key;
    long count;
    SwipeController swipeController = null;
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    MaterialEditText comment_editText;
    ImageButton sendButton;
    private RecyclerView notes,commentRec;
    String  current_user,userid,_lessonName,_dersHakkinde,major,_time;
    TextView username,lessonName,likecount,commentCount,time;
    CircleImageView profileImage;
    DatabaseReference databaseReference;
    Context context = this;
    RecyclerView.LayoutManager manager;
    ArrayList<String> images;
    String query;
    List<CommentHelper> comment = new ArrayList<>();
    CommentAdapter commentAdapter;
    String pushId;
    String senderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaition_activies);
        Bundle b = getIntent().getExtras();
        current_user=getIntent().getStringExtra("currentUser");
        userid=getIntent().getStringExtra("userID");
        key=getIntent().getStringExtra("key");
        major=getIntent().getStringExtra("major");
        query=getIntent().getStringExtra("query");
        commentRec=(RecyclerView)findViewById(R.id.commentRec);
        sendButton=(ImageButton)findViewById(R.id.sendButton);
        count=b.getLong("count");

        comment_editText=(MaterialEditText)findViewById(R.id.comment_edittext);
        notes=(RecyclerView)findViewById(R.id.data_recyclerView);
        time=(TextView)findViewById(R.id.time);
        images= new ArrayList<>();
        notes.setHasFixedSize(true);
        manager= new GridLayoutManager(NotificaitionActiviesActivity.this,1,GridLayoutManager.HORIZONTAL,false);
        notes.setLayoutManager(manager);
        commentAdapter = new CommentAdapter(comment,major);
        commentRec.setAdapter(commentAdapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        commentRec.setHasFixedSize(true);
        commentRec.setLayoutManager(manager1);
        setupRecyclerView(commentRec);
        final ImageView liked = (ImageView)findViewById(R.id.liked);
        setCommentVisibility(major,key,query);
        setLikedVisibility(major,key,query);
        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLiked(major,key,query);
            }
        });
        final ImageView not_like = (ImageView)findViewById(R.id.like);




       /* if (count<=4)
        {

        }
        else if (5 <= count && count<=8)
        {
            manager= new GridLayoutManager(NotificaitionActiviesActivity.this,2,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }
        else  if (9 <= count && count<=12)
        {
            manager= new GridLayoutManager(NotificaitionActiviesActivity.this,3,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }
        else if (13<=count)
        {
            manager= new GridLayoutManager(NotificaitionActiviesActivity.this,4,GridLayoutManager.HORIZONTAL,false);
            notes.setLayoutManager(manager);
        }*/

        setCommentLayout(key);

        username=(TextView)findViewById(R.id.username);
        profileImage=(CircleImageView)findViewById(R.id.profilePic);
        likecount=(TextView)findViewById(R.id.likeCount);
        commentCount=(TextView)findViewById(R.id.comemntCount);
        lessonName=(TextView)findViewById(R.id.lessonName);
        lessonName.setText(_lessonName);
       // getNotes(major,key);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendComment(key,current_user);
            }
        });
        databaseReference= FirebaseDatabase.getInstance().getReference().child(query)
                .child(key).child("data");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(current_user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("name").getValue().toString());
                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .resize(512,512)
                        .centerCrop()
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final Query postReference= FirebaseDatabase.getInstance().getReference().child(major).child(query).child(key).child("data");

        FirebaseRecyclerOptions<Notes> options = new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(postReference,Notes.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Notes,NotificaitionActiviesActivity.NotesViewHolder> adapter=  new FirebaseRecyclerAdapter<Notes, NotificaitionActiviesActivity.NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificaitionActiviesActivity.NotesViewHolder holder, final int position, @NonNull final Notes model) {
                holder.setDatas(model.getImage());

                images.add(model.getImage());

                holder.view.findViewById(R.id.images).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        final Intent i = new Intent(NotificaitionActiviesActivity.this, ImageLoaderActivity.class);
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
            public NotificaitionActiviesActivity.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_lesson_post_view_holder,viewGroup,false);


                return new NotificaitionActiviesActivity.NotesViewHolder(view);

            }

        };
        notes.setAdapter(adapter);


    }

    private void sendComment(final String key, final String current_user)
    {
        String comment = comment_editText.getText().toString();
        if (!TextUtils.isEmpty(comment))
        {
            String sendTime = String.valueOf(-1* Calendar.getInstance().getTimeInMillis());
            HashMap<String,String> timeMap = new HashMap();
            timeMap.put("sendTime",sendTime);
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(major).child(query).child(key).child("comments").push();

            Map messageMap = new HashMap();
            messageMap.put("comment",comment);
            messageMap.put("from",current_user);
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
                                senderName=dataSnapshot.child("name").getValue().toString();
                                newNotification.put("name",dataSnapshot.child("name").getValue().toString());
                                newNotification.put("send", current_user);
                                newNotification.put("type","Senin Gönderine Yorum Yaptı");
                                newNotification.put("title","YORUM");
                                notification.setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        getRandomString(20);
                                      //  setDatabaseNotification(current_user,key,userid,senderName.toString(),"Senin Gönderine Yorum Yaptı",major,pushId,_lessonName,"YORUM",query);
                                    }
                                });

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

    private void getNotes(String major, String key)
    {

    }
    public class NotesViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public NotesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view=itemView;
        }
        public void setDatas(String url)
        {
            ImageView view = (ImageView)itemView.findViewById(R.id.datas);
            Picasso.get().load(url).resize(150,150).centerCrop().placeholder(R.drawable.images).into(view);
        }
    }


    private void deleteLiked(String major, String _lessonKey, String query)
    {
        final ImageView not_like = (ImageView)findViewById(R.id.like);
        final ImageView liked = (ImageView)findViewById(R.id.liked);
        final TextView likeCount =(TextView)findViewById(R.id.likeCount);
        final TextView likedCount =(TextView)findViewById(R.id.likedCount);
        final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(major)
                .child(query).child(_lessonKey).child("Likes");
        likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if (ds.child("likedId").getValue().toString().equals(current_user))
                        {
                            Log.d("exit", "onDataChange: "+current_user);
                            ds.getKey();
                            likeReference.child(ds.getKey()).removeValue();

                        }

                    }


                    likedCount.setVisibility(View.GONE);

                    likeCount.setText(Long.toString(dataSnapshot.getChildrenCount())+" kişi beğendi" );
                    likeCount.setVisibility(View.VISIBLE);
                }
                else
                {

                    not_like.setVisibility(View.VISIBLE);
                    liked.setVisibility(View.GONE);
                    likedCount.setVisibility(View.VISIBLE);
                    likeCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setLikedVisibility(String _major, String _lessonKey,String query)
    {
        final ImageView not_like = (ImageView)findViewById(R.id.like);
        final ImageView liked = (ImageView)findViewById(R.id.liked);
        final TextView likeCount =(TextView)findViewById(R.id.likeCount);
        final TextView likedCount =(TextView)findViewById(R.id.likedCount);
        final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child(query).child(_lessonKey).child("Likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.getChildrenCount()>0)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if (ds.child("likedId").getValue().toString().equals(current_user))
                        {
                            Log.d("exit", "onDataChange: "+current_user);

                            not_like.setVisibility(View.INVISIBLE);
                            liked.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            not_like.setVisibility(View.VISIBLE);
                            liked.setVisibility(View.INVISIBLE);
                        }
                    }


                    likedCount.setVisibility(View.GONE);

                    likeCount.setText(Long.toString(dataSnapshot.getChildrenCount())+" kişi beğendi" );
                    likeCount.setVisibility(View.VISIBLE);
                }
                else
                {

                    not_like.setVisibility(View.VISIBLE);
                    liked.setVisibility(View.GONE);
                    likedCount.setVisibility(View.VISIBLE);
                    likeCount.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setCommentLayout(String ref)
    {

        final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(major).child(query).child(ref).child("comments");
        commentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentHelper helper = dataSnapshot.getValue(CommentHelper.class);
                comment.add(helper);
                commentAdapter.notifyDataSetChanged();
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
    private void setCommentVisibility(String _major, String _lessonKey, String query)
    {
        final ImageView commentImageDefault = (ImageView)findViewById(R.id.commentClickdefault);
        final TextView commentCountDefeault =(TextView)findViewById(R.id.commentCountdefult);
        final TextView commentCount =(TextView)findViewById(R.id.comemntCount);
        final ImageView commentClick =(ImageView)findViewById(R.id.commentClick);

        final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child(query).child(_lessonKey).child("comments");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.getChildrenCount()>0)
                {

                    commentClick.setVisibility(View.VISIBLE);

                    commentCountDefeault.setVisibility(View.GONE);
                    commentImageDefault.setVisibility(View.GONE);
                    commentCount.setVisibility(View.VISIBLE);
                    commentCount.setText(Long.toString(dataSnapshot.getChildrenCount())+" tane yorum " );
                }
                else
                {

                    commentCountDefeault.setVisibility(View.VISIBLE);
                    commentImageDefault.setVisibility(View.VISIBLE);
                    commentCount.setVisibility(View.GONE);
                    commentClick.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendLike(String key, final String current_user)
    {
        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification").child(userid).push();
        final HashMap<String,String> newNotification = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(current_user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                newNotification.put("name",dataSnapshot.child("name").getValue().toString());
                newNotification.put("send", current_user);
                newNotification.put("type","Senin Gönderini Beğendi");
                newNotification.put("title","Yeni Bir Kalp");
                notification.setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d("yeni bir beğeni", "onComplete: "+"beğeni bildirimi gönderildi");
                        else Log.w("hata", "onComplete: ",task.getException() );
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void setupRecyclerView(RecyclerView view)
    {

        swipeController= new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position)
            {

                commentAdapter.commentHelpers.remove(position);
                commentAdapter.notifyItemRemoved(position);
                commentAdapter.notifyItemRangeChanged(position,commentAdapter.getItemCount());
                super.onRightClicked(position);
            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(view);

        view.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
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
            , String _query)
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

}
