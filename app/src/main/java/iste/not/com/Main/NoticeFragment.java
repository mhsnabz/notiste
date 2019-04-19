package iste.not.com.Main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.FragmentDialogs.PostNoteDiaolog;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.NOTICES.SingleNoticesActivity;
import iste.not.com.POST.PostNoteActivity;
import iste.not.com.POST.SingleLessonsPostActivity;
import iste.not.com.Profile.ProfileActivity;
import iste.not.com.R;
import iste.not.com.SplashScreen;
import iste.not.com.Users.UserProfileActivity;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment
{
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    Query postReference;
    private MaterialEditText search;
    public String current_user;
    FirebaseUser uid;
    RecyclerView.LayoutManager layoutManager;
    Query reference;
    private View rootView;
    private FloatingActionButton noticesButton;
    private PostNoteDiaolog postNoteDiaolog;
    private RecyclerView notes;
    String query;
    String Major,username ,mmajor;
    DatabaseReference MajorDb;
    SplashScreen splashScreen;
    String name="";
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String pushId;

    HashMap<String ,String > notification = new HashMap<>();
    public NoticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_notice,container,false);

        notes =(RecyclerView)rootView.findViewById(R.id.recyclerViewNot);
        splashScreen = new SplashScreen();
        search=(MaterialEditText)rootView.findViewById(R.id.search);
        mmajor=getActivity().getIntent().getStringExtra("major");
        current_user=firebaseUser.getUid();
        noticesButton=(FloatingActionButton)rootView.findViewById(R.id.notice_button);
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("Students").child(current_user);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                username=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        MajorDb = FirebaseDatabase.getInstance().getReference().child("Students");
        MajorDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Major= dataSnapshot.child(current_user).child("major").getValue().toString();
                Log.d(TAG, "onDataChange: + Major");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = search.getText().toString();
                Log.d(TAG, "onTextChanged: " + name);
                getNotes(Major,name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noticesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SingleNoticesActivity.class);
                intent.putExtra("name",username);
                intent.putExtra("notificationType"," Yeni Bir Duyuru Yaptı");
                intent.putExtra("query","Public_NOTICES");
                startActivity(intent);
            }
        });
        getNotes(mmajor,"x");
        return rootView;
    }

    private void getNotes(final String mMajor,String name)
    {

        if (name.equals("x"))
        {
            postReference=FirebaseDatabase.getInstance().getReference().child(mMajor).child("Public_NOTICES");
        }
        else
        {
            postReference= FirebaseDatabase.getInstance().getReference().child(mMajor).child("Public_NOTICES").orderByChild("lessonName").startAt(name).endAt(name+"\uf8ff");;
        }

        FirebaseRecyclerOptions<Notes> options
                =new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(postReference,Notes.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Notes,NoticeFragment.NotesViewHolder> adapter  = new FirebaseRecyclerAdapter<Notes, NoticeFragment.NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NoticeFragment.NotesViewHolder holder, final int position, @NonNull final Notes model)
            {
                Log.d(TAG, "onBindViewHolder: " +model.getUserId());
                final   String Userid = model.getUserId();
                final String key  = getRef(position).getKey().toString();
                holder.setName(model.getUserId());
                holder.setLessonName(model.getLessonName());
                long saat = model.getTime();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(mMajor).child("Teacher").child(model.getTeacherID());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String unvan = dataSnapshot.child("unvan").getValue().toString();
                        holder.setTeacherName(unvan+" "+name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (current_user.equals(model.getUserId()))
                {
                    holder.view.findViewById(R.id.deletePost).setVisibility(View.VISIBLE);
                }
                holder.setFallowButton(key,model.getLessonKey(),mMajor,model.getTeacherID());
                holder.setTime(getTimeAgo(saat));
                holder.setYorum(model.getComment());
                holder.setCount(model.getCount());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(Userid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        holder.setName(name);
                        String pic = dataSnapshot.child("image").getValue().toString();
                        holder.setProfile(pic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final DatabaseReference fallowers = FirebaseDatabase.getInstance().getReference().child(mMajor).child("Teacher").child(model.getTeacherID()).child("students").child(current_user);

                holder.view.findViewById(R.id.fallow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        String key =model.getLessonKey();
                        Log.d("key", "onClick: "+key+"lessonkey");
                        TextView textView=(TextView)  holder.view.findViewById(R.id.fallow);

                        if (textView.getText().equals("Takip Et"))
                        {

                            holder.setFalow();
                            holder.setFallovers(current_user,fallowers);
                            holder.setLessonsStudents(mMajor,model.getTeacherID(),model.getLessonKey(),current_user);

                        }
                        else {
                            holder.setUnfallow();
                            holder.setUnfallovers(current_user,model.getTeacherID(),mMajor);
                            holder. deleteLessonsStudents(mMajor,model.getTeacherID(),model.getLessonKey(),current_user);
                        }

                    }
                });
                holder.view.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.setLike(mmajor,current_user,key);
                        getRandomString(20);
                        holder.sendLikeNotification(current_user,key,Userid,username," Senin Gönderini Beğendi",mmajor,pushId,model.getLessonName(),"Yeni Bir Beğeni","Public_NOTICES");
                    }
                });
                holder.view.findViewById(R.id.liked).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.deleteLiked(mmajor,key);
                    }
                });
                holder.setLikedVisibility(mmajor,key);
                holder.setCommentVisibility(mmajor,key);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getContext(),SingleLessonsPostActivity.class);
                        intent.putExtra("key",getRef(position).getKey().toString());
                        intent.putExtra("current_user_id",current_user);
                        intent.putExtra("major",mMajor);
                        intent.putExtra("user_id",model.getUserId().toString());
                        intent.putExtra("lessonName",model.getLessonName());
                        intent.putExtra("dersHakkinde",model.getComment());
                        intent.putExtra("count",model.getCount());
                        intent.putExtra("query","Public_NOTICES");
                        intent.putExtra("notificationQuery","notices");
                        Log.d("key", "onClick: "+getRef(position).getKey().toString());
                        startActivity(intent);    }
                });
                holder.setYorum(model.getComment());
                holder.view.findViewById(R.id.profilePic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {   String key = model.getUserId().toString();
                        Log.d(TAG, "onClick: "+key);
                        Intent intent = new Intent(getContext(), UserProfileActivity.class);
                        intent.putExtra("current_user_id",current_user);
                        intent.putExtra("user_id",model.getUserId().toString());
                        if (current_user.equals(model.getUserId()))
                        {
                            Intent profieIntent = new Intent(getContext(),ProfileActivity.class);
                            startActivity(profieIntent);
                        }
                        else  startActivity(intent);



                    }
                });
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new NotesViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_layout_viewholder,viewGroup,false));
            }
        };

        notes.setHasFixedSize(true);
        notes.setLayoutManager(new GridLayoutManager(getContext(),1));
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


        public void setLessonName(String name) {
            TextView LessonName = (TextView) view.findViewById(R.id.lessonName);
            LessonName.setText(name);

        }
        public void setTime(String time)
        {
            TextView _time = (TextView)view.findViewById(R.id.time);
            _time.setText(time);
        }
        public void setProfile(String name) {
            CircleImageView profilepic = (CircleImageView) view.findViewById(R.id.profilePic);
            Picasso.get().load(name).resize(256,256).centerCrop().placeholder(R.drawable.placeholder_image).into(profilepic);


        }
        public void setName(String name) {
            TextView LessonName = (TextView) view.findViewById(R.id.username);
            LessonName.setText(name);

        }

        public void setYorum(String yorum)
        {
            TextView _tvYorum = (TextView)view.findViewById(R.id.dersHakinda);
            _tvYorum.setText(yorum);
        }
        public void setCount(long count)
        {
            TextView _count = (TextView)view.findViewById(R.id.datasCount);
            _count.setText(String.valueOf(count)+" Tane Dosya");
        }
        public void setFalow()
        {
            ImageButton fallowButton =(ImageButton)itemView.findViewById(R.id.fallowButton);
            ImageButton fallowedButton =(ImageButton)itemView.findViewById(R.id.fallowedButton);

            TextView fallow = (TextView)itemView.findViewById(R.id.fallow);
            fallowButton.setVisibility(View.GONE);
            fallowedButton.setVisibility(View.VISIBLE);
            fallow.setText("Takip Ediliyor");

        }
        public void setUnfallow()
        {
            ImageButton fallowButton =(ImageButton)itemView.findViewById(R.id.fallowButton);
            ImageButton fallowedButton =(ImageButton)itemView.findViewById(R.id.fallowedButton);

            TextView fallow = (TextView)itemView.findViewById(R.id.fallow);
            fallowButton.setVisibility(View.VISIBLE);
            fallowedButton.setVisibility(View.GONE);
            fallow.setText("Takip Et");

        }
        public void setTeacherName(String name)
        {
            TextView view = (TextView)itemView.findViewById(R.id.teacher);
            view.setText(name);
        }
        public  void setUnfallovers(String currentUserId,String teacherID,String _Major)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_Major).child("Teacher").child(teacherID).child("students");
            reference.child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Log.d("remove value", "onComplete: "+"slindi");
                    }
                    else
                        Log.w(TAG, "onComplete: ",task.getException() );
                }
            });

        }
        public void setFallovers(String currentUserId, final DatabaseReference reference1)
        {
            final HashMap<String,String> map = new HashMap<>();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUserId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String Class =dataSnapshot.child("Class").getValue().toString();
                    map.put("name",name);
                    map.put("Class",Class);
                    reference1.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getContext(),"Artık Dersi Takip Ediyorsunuz",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setFallowButton(String key, String lessonKey, String major, final String UserID)
        {
            Log.d("key", "onClick: "+key+"lessonkey");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(major).child("Teacher").child(UserID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!dataSnapshot.child("students").child(current_user).exists())
                    {
                        setUnfallow();

                    }
                    else
                    {
                        setFalow();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void deleteLessonsStudents(String _major,String _teacherId,String _lessonKey,String _studentsId)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Teacher").child(_teacherId).child(_lessonKey);
            reference.child(_studentsId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Log.d("Öğrenci silindi", "onComplete: ");
                    }
                    else
                        Log.w("oörenci silinemedi", "onComplete: ",task.getException() );
                }
            });
        }
        public void setLessonsStudents(String _major,String _teacherId,String _lessonKey,String _studentsId)
        {
            final HashMap<String ,String> map = new HashMap<>();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Teacher").child(_teacherId).child(_lessonKey).child(_studentsId);
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Students").child(_studentsId);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String Class=dataSnapshot.child("Class").getValue().toString();
                    map.put("name",name);
                    map.put("Class",Class);
                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.d("Öğrenci Derse Eklendi", "onComplete: " );
                            }
                            else
                                Log.w("Öğrenci Derse Eklendi", "onComplete: ",task.getException() );
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setLike(String _major, final String _currentUser, String _lessonKey)
        {
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_NOTICES").child(_lessonKey);
            likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {

                    likeReference.child("Likes").push().child("likedId").setValue(_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.d("like", "onComplete: "+"like eklendi");

                            }

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setLikedVisibility(String _major, String _lessonKey)
        {
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            final TextView likeCount =(TextView)itemView.findViewById(R.id.likeCount);
            final TextView likedCount =(TextView)itemView.findViewById(R.id.likedCount);
            final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_NOTICES").child(_lessonKey).child("Likes");
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
        public void deleteLiked(String _major, String _lessonKey)
        {
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            final TextView likeCount =(TextView)itemView.findViewById(R.id.likeCount);
            final TextView likedCount =(TextView)itemView.findViewById(R.id.likedCount);
            final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_NOTICES").child(_lessonKey).child("Likes");
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
        public void setCommentVisibility(String _major, String _lessonKey)
        {

            final ImageView commentImageDefault = (ImageView)itemView.findViewById(R.id.commentClickdefault);
            final TextView commentCountDefeault =(TextView)itemView.findViewById(R.id.commentCountdefult);
            final TextView commentCount =(TextView)itemView.findViewById(R.id.comemntCount);
            final ImageView commentClick =(ImageView)itemView.findViewById(R.id.commentClick);
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            final TextView likeCount =(TextView)itemView.findViewById(R.id.likeCount);
            final TextView likedCount =(TextView)itemView.findViewById(R.id.likedCount);
            final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_NOTICES").child(_lessonKey).child("comments");
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
        public void sendLikeNotification(String _getterUserId,String _currentUserID,String _lessonKey ,String _username)
        {
            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

            notification.put("name",_username);
            notification.put("type"," Senin Gönderini Beğendi");
            notification.put("title","Yeni Bir Beğeni ");
            notification.put("send",_getterUserId);
            reference1.child(_getterUserId).push().setValue(notification);


        }




        public void sendLikeNotification(final String _currentUserID, final String _lessonKey, final String _getterUserId, final String _username
                , final String _type, final String _major, final String _pushId, final String _lessonName, final String _title
                , final String _query)
        {
            setDatabaseNotification(_currentUserID,_lessonKey,_getterUserId,_username,_type,_major,_pushId,_lessonName,_title,_query);

            DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("NotificationSettings").child(_getterUserId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("like").exists())
                    {
                        if (dataSnapshot.child("like").getValue().toString().equals("true"))
                        {
                            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

                            notification.put("name",_username);
                            notification.put("type",_type);
                            notification.put("title",_title);
                            notification.put("send",_getterUserId);
                            reference1.child(_getterUserId).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {


                                        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

                                        notification.put("name",_username);
                                        notification.put("type",_type);
                                        notification.put("title",_title);
                                        notification.put("send",_getterUserId);
                                        reference1.child(_getterUserId).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {


                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                        else if (dataSnapshot.child("like").getValue().toString().equals("false")) return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        public void setDatabaseNotification(String _currentUserID, String _lessonKey, final String _getterUserId, String _username
                , String _type, final String _major, final String _pushId, String _lessonName, String _title
                , String _query)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("databaseNotifications")
                    .child(_getterUserId).child("AllNotification");
            HashMap<String , String >map = new HashMap<>();
            map.put("key",_pushId);
            map.put("type",_type);
            map.put("sender",_username);
            map.put("senderUid",_currentUserID);
            map.put("lessonKey",_lessonKey);
            map.put("lessonName",_lessonName);
            map.put("title",_title);
            map.put("query",_query);
            map.put("seen","false");
            String  time =Long.toString(Calendar.getInstance().getTimeInMillis()) ;
            map.put("time",time);
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


}
