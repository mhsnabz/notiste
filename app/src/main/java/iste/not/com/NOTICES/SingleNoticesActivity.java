package iste.not.com.NOTICES;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.FragmentDialogs.LessonsName;
import iste.not.com.Main.MainActivity;
import iste.not.com.POST.PostNoteActivity;
import iste.not.com.POST.UploadListAdapter;
import iste.not.com.QUESTIONS.SingleQuestionsActivity;
import iste.not.com.R;

import static android.content.ContentValues.TAG;

public class SingleNoticesActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int FILE_SELECT_CODE = 2;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private List<Double> fileProgres;
    private List<String> sizeLabel;
    private Toolbar toolbar;
    static String pushId;
    HashMap<Integer , String > DATAS ;
    HashMap<String,String> hashMap;
    HashMap<String,String> datasMap = new HashMap<>();
    private UploadListAdapter uploadListAdapter;
    UploadListAdapter adapter;
    private StorageReference mStorage;
    private MaterialEditText lessonSearch,post_not;
    public StorageReference fileToUpload, dataStorageReference;
    private FloatingActionButton mSelectBtn,post_click;
    String name,username;
    String Major;
    Query reference;
    String lessonKey;
    String teacherID;
    private RecyclerView LessonsName;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String UserID = firebaseUser.getUid();
    RelativeLayout relativeLayout_edittext,relativeLayout_post;
    CircleImageView circleImageView;
    private TextView lessonNAME_textview,tv_name;
    HashMap<String,String> user;
    HashMap<String ,String > notification = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notices);
        username=getIntent().getStringExtra("name");
        getMajor(UserID);
        post_click=(FloatingActionButton)findViewById(R.id.post_click);
        getRandomString(20);
        SetToolbar();
        DATAS = new HashMap<>();
        hashMap= new HashMap();
        datasMap= new HashMap<>();
        mStorage = FirebaseStorage.getInstance().getReference();
        tv_name=(TextView)findViewById(R.id.tv_username);
        notification.put("name",username);
        notification.put("send",UserID);
        notification.put("type"," Yeni Bir Duyuru Yaptı");
        user = new HashMap<>();
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        fileProgres = new ArrayList<>();
        sizeLabel = new ArrayList<>();
        lessonNAME_textview=(TextView)findViewById(R.id._tv_lessonName);
        relativeLayout_edittext=(RelativeLayout)findViewById(R.id.realLay_editText);
        post_not=(MaterialEditText)findViewById(R.id.post_not);
        relativeLayout_post=(RelativeLayout)findViewById(R.id.relLayout_post);
        relativeLayout_edittext.setVisibility(View.VISIBLE);
        relativeLayout_post.setVisibility(View.INVISIBLE);
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);
        lessonSearch=(MaterialEditText)findViewById(R.id.searchLesson_note);
        circleImageView=(CircleImageView)findViewById(R.id.profilePicture);
        loadProfile(tv_name,circleImageView,UserID);
        LessonsName =(RecyclerView)findViewById(R.id.recyclerView_LessonsName);
        LessonsName.setHasFixedSize(true);
        LessonsName.setLayoutManager(new LinearLayoutManager(SingleNoticesActivity.this));
        lessonSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                name = lessonSearch.getText().toString();
                Log.d(TAG, "onTextChanged: " + name);
                GetLessons(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        post_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setPublicPosts();
                setNotification();
                Intent intent = new Intent(SingleNoticesActivity.this,MainActivity.class);
                intent.putExtra("major",Major);
                startActivity(intent);
                finish();
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
    private void setPublicPosts()
    {
        /// get major

        long currentTime = Calendar.getInstance().getTimeInMillis();
        DatabaseReference MajorDb = FirebaseDatabase.getInstance().getReference().child("Students");
        MajorDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Major= dataSnapshot.child(UserID).child("major").getValue().toString();
                Log.d(TAG, "onDataChange: + Major");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        String comment =post_not.getText().toString();
        hashMap.put("yorum",comment);


        //  HashMap<HashMap<String,String>,HashMap<String,String>> map1 = new HashMap();
        // map1.put( hashMap , datasMap);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major)
                .child("Public_NOTICES")
                .child(pushId);

        databaseReference.child("comment").setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                    Log.d(TAG, "onComplete: "+"Yorumlar Kaydedildi");

                }
            }
        });
        databaseReference.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        databaseReference.child("lessonName").setValue(lessonNAME_textview.getText().toString());
        databaseReference.child("time").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: "+"Saat kaydedildi");
                }
            }
        });
        databaseReference.child("userId").setValue(UserID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                    Log.d(TAG, "onComplete: "+"UserId Kaydedildi");

                }
            }
        });
        databaseReference.child("key").setValue(pushId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {


                    //ChatUserMap.put("LastSEEN/" + currentUsers + "/" + ChatUser + "/", chatAddMap);

                }
            }
        });
        databaseReference.child("count").setValue( DATAS.size()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {


                    //ChatUserMap.put("LastSEEN/" + currentUsers + "/" + ChatUser + "/", chatAddMap);

                }
            }
        });
        databaseReference.child("lessonKey").setValue(lessonKey).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("lessonKey", "onComplete: "+lessonKey+" :eklendi");

            }
        });
        databaseReference.child("teacherID").setValue(teacherID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("teacherID", "onComplete: "+teacherID+" :eklendi");

            }
        });

        for (Map.Entry<Integer, String> entry : DATAS.entrySet()) {
            final String key = entry.getKey().toString();
            String value = entry.getValue();
            dataStorageReference = FirebaseStorage.getInstance().getReference();
            dataStorageReference.child(UserID).child("Images").child(value).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl = uri.toString();

                    datasMap.put(key, downloadUrl);
                    Log.d(TAG, "downloadUrl: " + downloadUrl);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major)
                            .child("Public_NOTICES")
                            .child(pushId);
                    databaseReference.child("data").push().child("image").setValue(downloadUrl);

                }
            });
            Log.d(TAG, "post: " + value + "  " + key);

        }

    }
    private void GetLessons(String Name)
    {
        reference =  FirebaseDatabase.getInstance().getReference().child(Major).child("Lessons").orderByChild("lessonName").startAt(Name).endAt(Name+"\uf8ff");
        FirebaseRecyclerOptions<iste.not.com.FragmentDialogs.LessonsName> options
                = new FirebaseRecyclerOptions.Builder<LessonsName>().setQuery(reference, iste.not.com.FragmentDialogs.LessonsName.class)
                .setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<LessonsName,SingleNoticesActivity.LessonsViewHolder> adapter
                = new FirebaseRecyclerAdapter<iste.not.com.FragmentDialogs.LessonsName, SingleNoticesActivity.LessonsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SingleNoticesActivity.LessonsViewHolder holder, final int position, @NonNull final iste.not.com.FragmentDialogs.LessonsName model)
            {
                holder.GetName(model.getLessonName());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        String name  = getRef(position).getKey().toString();
                        Log.d(TAG, "onClick: "+name);
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major).child("Lessons").child(name);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String name1 = model.getLessonName();
                                lessonNAME_textview.setText(name1);
                                lessonKey=model.getKey();
                                teacherID=model.getTeacherId();
                                notification.put("title",model.getLessonName());
                                sendNotification(lessonKey,teacherID,Major,UserID);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        relativeLayout_edittext.setVisibility(View.INVISIBLE);
                        relativeLayout_post.setVisibility(View.VISIBLE);


                    }
                });
            }

            @NonNull
            @Override
            public LessonsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new LessonsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_lessons_layout,viewGroup,false));
            }
        } ;

        LessonsName.setAdapter(adapter);
    }

    private void sendNotification(String _lessonKey, String _teacherID, String _major, final String _current_user)
    {
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("Teacher")
                .child(_teacherID).child(_lessonKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (!ds.getKey().equals(_current_user))
                    {
                        user.put(ds.getKey(),ds.getKey());
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public class LessonsViewHolder extends RecyclerView.ViewHolder
    {   View view;

        public LessonsViewHolder (View itemView)
        {
            super(itemView);
            view=itemView;
        }
        public void GetName (String _name)
        {
            if (_name!=null){
                TextView Tv_LessonName =(TextView)view.findViewById(R.id.TV_LessonName);
                Tv_LessonName.setText(_name);
            }


        }
        public void setUserId(String uid)
        {
            TextView teacherUid = (TextView)view.findViewById(R.id.teacher);
            teacherUid.setText(uid);
        }


    }
    private  void SetToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.MianToolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inLayoutInflater.inflate(R.layout.toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("Duyuru Yap");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d("randomStrign", "getRandomString: "+sb.toString());
        pushId=sb.toString();
        return sb.toString();
    }
    private void getMajor(final String keyX)
    {

        DatabaseReference MajorDb = FirebaseDatabase.getInstance().getReference().child("Students");
        MajorDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Major= dataSnapshot.child(keyX).child("major").getValue().toString();
                Log.d(TAG, "onDataChange: + Major");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadProfile(final TextView _name, final CircleImageView _image, String _currentUser)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students")
        .child(_currentUser);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .centerCrop().resize(512,512)
                        .placeholder(R.drawable.placeholder_image)
                        .into(_image);
                _name.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
