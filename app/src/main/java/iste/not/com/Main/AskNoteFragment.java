package iste.not.com.Main;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.FragmentDialogs.PostNoteDiaolog;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.NOTICES.LikedHelper;
import iste.not.com.POST.PostNoteActivity;
import iste.not.com.POST.SingleLessonsPostActivity;
import iste.not.com.Profile.ProfileActivity;
import iste.not.com.R;
import iste.not.com.SplashScreen;
import iste.not.com.Users.UserProfileActivity;
import static android.content.ContentValues.TAG;


public class AskNoteFragment extends Fragment
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
    private FloatingActionButton askNoteButton;
    private PostNoteDiaolog postNoteDiaolog;
    private RecyclerView notes;
    String Major,username ,mmajor;
    DatabaseReference MajorDb;
    SplashScreen splashScreen;
    String name="";
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    LikedHelper likedHelper;
    HashMap<String,String> user;
    HashMap<String ,String > notification = new HashMap<>();
    String pushId;
    String studentNumber,StudentPicture;

    private boolean isLoading = true;
    private int  pastVisibleItem,visibleItem,totolItemCount,previusCount =0;
    private int view_threadHold=10;
    public AskNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ask_note,container,false);
        user = new HashMap<>();

        likedHelper= new LikedHelper();
        splashScreen = new SplashScreen();
        search=(MaterialEditText) rootView.findViewById(R.id.search);
        mmajor=getActivity().getIntent().getStringExtra("major");
        search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             name = search.getText().toString();
                Log.d(TAG, "onTextChanged: " + name);
                getNotes(Major,name,search);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        notes =(RecyclerView)rootView.findViewById(R.id.recyclerViewNot);

        askNoteButton=(FloatingActionButton)rootView.findViewById(R.id.ask_note_button);
        current_user=firebaseUser.getUid();
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("Students").child(current_user);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.child("unvan").exists()) {
                    username = dataSnapshot.child("name").getValue().toString();
                    studentNumber = dataSnapshot.child("number").getValue().toString();
                    StudentPicture = dataSnapshot.child("image").getValue().toString();
                }
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


        askNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(),PostNoteActivity.class);
                intent.putExtra("name",username);
                intent.putExtra("query","Public_POST");
                startActivity(intent);
            }
        });

        notes.setHasFixedSize(true);
         layoutManager=new GridLayoutManager(getContext(),1);
        notes.setLayoutManager(layoutManager);
        notes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                visibleItem=layoutManager.getChildCount();
                totolItemCount=layoutManager.getItemCount();
              //  pastVisibleItem=layoutManager.find
            }
        });


        getNotes(mmajor,"x",search);
        return rootView;
    }

    private void getNotes(final String mMajor,String name,MaterialEditText search)
    {

        if (name.equals("x")||search.getText().toString().isEmpty())
        {
            postReference=FirebaseDatabase.getInstance().getReference().child(mMajor).child("Public_POST").orderByChild("time");
        }
        else
        {
             postReference= FirebaseDatabase.getInstance().getReference().child(mMajor).child("Public_POST").orderByChild("lessonName").startAt(name).endAt(name+"\uf8ff");
        }

       FirebaseRecyclerOptions<Notes> options
                =new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(postReference,Notes.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Notes,NotesViewHolder> adapter  = new FirebaseRecyclerAdapter<Notes, NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotesViewHolder holder, final int position, @NonNull final Notes model)
            {
                Log.d(TAG, "onBindViewHolder: " +model.getUserId());
              final   String Userid = model.getUserId();
                final String key  = getRef(position).getKey().toString();



                holder.setLessonName(model.getLessonName());
                final long saat = model.getTime();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(mmajor).child("Teacher").child(model.getTeacherID());
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
                holder.view.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.setLike(mmajor,current_user,key,model.getUserId());
                        getRandomString(20);

                        holder.sendLikeNotification(current_user,key,Userid,username," Senin Gönderini Beğendi",mmajor,pushId,model.getLessonName(),"Yeni Bir Beğeni","Public_POST",model.getCount(),Userid);
                       // holder.sendLikedNotification(Userid,current_user,key,username);
                    }
                });
                holder.setFallowButton(key,model.getLessonKey(),mMajor,model.getTeacherID());
                holder.setTime(getTimeAgo(-1*saat));

                holder.setYorum(model.getComment());
                holder.setCount(model.getCount());
                holder.setCommentVisibility(mmajor,key);

               final DatabaseReference fallowers = FirebaseDatabase.getInstance().getReference().child(mMajor).child("Teacher").child(model.getTeacherID()).child("students").child(current_user);
        //visible =0 , invisibele== 4

                holder.view.findViewById(R.id.liked).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.deleteLiked(mmajor,key);
                    }
                });
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
                          //  holder.setFallovers(current_user,fallowers);
                            holder.setLessonsStudents(mMajor,model.getTeacherID(),model.getLessonKey(),current_user);
                           holder. setMyLessons(model.getLessonKey(),current_user,model.getTeacherID());

                        }
                        else {
                            holder.setUnfallow();
                         // holder.setUnfallovers(current_user,model.getTeacherID(),mMajor);
                           holder. deleteLessonsStudents(mMajor,model.getTeacherID(),model.getLessonKey(),current_user);
                           holder.deleteMyLesson(model.getLessonKey(),current_user);

                        }

                    }
                });
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
                if (current_user.equals(model.getUserId()))
                {
                    holder.view.findViewById(R.id.deletePost).setVisibility(View.VISIBLE);
                }
                else
                    holder.view.findViewById(R.id.deletePost).setVisibility(View.GONE);
                holder.view.findViewById(R.id.deletePost).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                       holder.deletePost(model.getKey(),mMajor);
                    }
                });
                holder.setLikedVisibility(mmajor,key);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getContext(),SingleLessonsPostActivity.class);
                        intent.putExtra("key",getRef(position).getKey().toString());
                        intent.putExtra("current_user_id",current_user);
                        intent.putExtra("major",mMajor);
                        intent.putExtra("comment",model.getComment());
                        intent.putExtra("user_id",model.getUserId().toString());
                        intent.putExtra("lessonName",model.getLessonName());
                        intent.putExtra("dersHakkinde",model.getComment());
                        intent.putExtra("count",model.getCount());
                        intent.putExtra("query","Public_POST");
                        intent.putExtra("time",getTimeAgo(-1*saat));
                        intent.putExtra("notificationQuery","notes");
                        Log.d("key", "onClick: "+getRef(position).getKey().toString());
                        startActivity(intent);    }
                });
               // holder.setPoster(model.getUserId());
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
                            intent.putExtra("current_user_id",current_user);
                            intent.putExtra("major",mMajor);
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
        public void deletePost(String _key,String _major)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_POST");
            databaseReference.child(_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(),"Paylaşımın Silindi",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(),"Bir Hata Oluştu",Toast.LENGTH_SHORT).show();
                        Log.w("delete Post", "onComplete: ",task.getException() );
                    }
                }
            });
        }
      public void setLessonName(String name)
      {
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
            Picasso.get().load(name).resize(512,512).centerCrop().placeholder(R.drawable.placeholder_image).into(profilepic);

        }
        public void setName(String _name)
        {
            final TextView name=(TextView)itemView.findViewById(R.id.username);
            name.setText(_name);
        }
        public void setPoster(final String Userid)
        {
            final TextView name=(TextView)itemView.findViewById(R.id.username);
            final CircleImageView profilepic = (CircleImageView) view.findViewById(R.id.profilePic);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("Students").hasChild(Userid))
                    {
                        setStudentName(Userid);

                    String pic = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(pic).resize(512,512).centerCrop().placeholder(R.drawable.placeholder_image).into(profilepic);

                    }
                    else if(dataSnapshot.child("ScientistEmailAdreses").hasChild(Userid))
                    {
                        setTeacherNameE(Userid);
                       /* Log.d("TeacherUserrrIDDD", "onDataChange: "+Userid);
                       name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
                        String pic = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(pic).resize(512,512).centerCrop().placeholder(R.drawable.placeholder_image).into(profilepic);
*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        private void setStudentName(final String UserID)
        {
            final TextView name=(TextView)itemView.findViewById(R.id.username);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(UserID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.d("UserrrIDDD", "onDataChange: "+UserID);
                    name.setText( dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        private void setTeacherNameE(final String UserID)
        {
            final TextView name=(TextView)itemView.findViewById(R.id.username);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses").child(UserID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.d("UserrrIDDD", "onDataChange: "+UserID);
                    name.setText( dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
                    map.put("image",StudentPicture);
                    map.put("number",studentNumber);
                    reference1.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getContext(),"Artık Dersi Takip Ediyorsunuz",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setFallowButton(String key, final String lessonKey, String major, final String UserID)
        {
            Log.d("key", "onClick: "+key+"lessonkey");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(major).child("Teacher").child(UserID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!dataSnapshot.child("myLessons").child(lessonKey).child("number").child(studentNumber).exists()
                            &&!dataSnapshot.child("myLessons").child(lessonKey).child("Uid").child(current_user).exists()
                    )
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


        public void deleteLessonsStudents(String _major, String _teacherId, final String _lessonKey, final String _studentsId)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Teacher").child(_teacherId).child("myLessons").child(_lessonKey);

            reference.child("number").child(studentNumber).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
            reference.child("Uid").child(_studentsId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        public void deleteMyLesson(String _lessonKey,String _currentUserId)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_currentUserId).child("myLesson")
                    ;
            databaseReference.child(_lessonKey).removeValue();
        }
        public void setLessonsStudents(String _major, String _teacherId, final String _lessonKey, final String _studentsId)
        {
            final Map<String ,Object> map = new HashMap<>();

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Teacher").child(_teacherId).child("myLessons").child(_lessonKey);
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Students").child(_studentsId);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String Class=dataSnapshot.child("Class").getValue().toString();
                    map.put("name",name);
                    map.put("studentId",_studentsId);
                    map.put("Class",Class);
                    map.put("image",StudentPicture);
                    map.put("number",studentNumber);

                    reference.child("number").child(studentNumber).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    reference.child("Uid").child(_studentsId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        public void setMyLessons(String _lessonKey,String _currentUserID,String _teacherId)
        {
        Map<String , Object > map = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students")
                .child(_currentUserID).child("myLesson").child(_lessonKey);
        map.put("key",_lessonKey);
        map.put("teacherId",_teacherId);
        databaseReference.updateChildren(map);

        }

        public void setLike(final String _major, final String _currentUser, final String _lessonKey,String _userId)
        {
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            if (_userId.equals(_currentUser)) {
                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle("Kral Aramızda Kalacak ");
                b.setMessage("Kendi Gönderini Beğenmek İstiyor Musun? ");
                b.setPositiveButton("Evet Çok Güzelim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                                .child("Public_POST").child(_lessonKey);
                        likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                likeReference.child("Likes").push().child("likedId").setValue(_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("like", "onComplete: " + "like eklendi");

                                        }

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                b.setNegativeButton("Hayır Çok Yakışıklıyım", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                                .child("Public_POST").child(_lessonKey);
                        likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                likeReference.child("Likes").push().child("likedId").setValue(_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("like", "onComplete: " + "like eklendi");

                                        }

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                AlertDialog alert = b.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setIcon(R.drawable.kral);
                alert.show();
            }
            else
            {
                final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                        .child("Public_POST").child(_lessonKey);
                likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        likeReference.child("Likes").push().child("likedId").setValue(_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("like", "onComplete: " + "like eklendi");

                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        }

        public void setLikedVisibility(String _major, String _lessonKey)
        {
            final ImageView not_like = (ImageView)itemView.findViewById(R.id.like);
            final ImageView liked = (ImageView)itemView.findViewById(R.id.liked);
            final TextView likeCount =(TextView)itemView.findViewById(R.id.likeCount);
            final TextView likedCount =(TextView)itemView.findViewById(R.id.likedCount);
            final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Public_POST").child(_lessonKey).child("Likes");
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
                    .child("Public_POST").child(_lessonKey).child("Likes");
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
                    .child("Public_POST").child(_lessonKey).child("comments");
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



        public void sendLikedNotification(String _getterUserId,String _currentUserID,String _lessonKey ,String _username)
        {
            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

            notification.put("name",_username);
            notification.put("type"," Senin Gönderini Beğendi");
            notification.put("title","Yeni Bir Beğeni ");
            notification.put("send",_getterUserId);
            reference1.child(_getterUserId).push().setValue(notification);


        }
        public void sendNotification(String _lessonKey, String _teacherID, String _major, final String _current_user)
        {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("Teacher")
                    .child(_teacherID).child(_lessonKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if (!ds.getKey().equals(_current_user))
                            user.put(ds.getKey(),ds.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        private void setNotification()
        {

            for (Map.Entry<String, String> entry : user.entrySet())
            {
                final String key = entry.getKey().toString();
                String  value = entry.getValue();

                Log.d(TAG, "post: "+value+"  "+key);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification")
                        .child(key).push();
                reference.setValue(notification);
            }


        }



        public void sendLikeNotification(final String _currentUserID, final String _lessonKey, final String _getterUserId, final String _username
                , final String _type, final String _major, final String _pushId, final String _lessonName, final String _title
                , final String _query,final long _count,final String _getterUserID)
        {
            setDatabaseNotification(_currentUserID,_lessonKey,_getterUserId,_username,_type,_major,_pushId,_lessonName,_title,_query,_count, _getterUserID);

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
                , String _query, long _count,final  String _userId)
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
}
