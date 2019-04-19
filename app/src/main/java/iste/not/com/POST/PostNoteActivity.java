package iste.not.com.POST;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.FragmentDialogs.LessonsName;
import iste.not.com.FragmentDialogs.PostNoteDiaolog;
import iste.not.com.Main.MainActivity;
import iste.not.com.R;
import iste.not.com.scientist.Post_Notice.ScientistSinglePostActivity;

import static android.content.ContentValues.TAG;

public class PostNoteActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int FILE_SELECT_CODE = 2;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private List<Double> fileProgres;
    private List<String> sizeLabel;
    String query;

     HashMap<String,String> datasMap = new HashMap<>();
    private UploadListAdapter uploadListAdapter;
    UploadListAdapter adapter;
    private StorageReference mStorage;
    private RecyclerView mUploadList;
    private FloatingActionButton  mSelectBtn,post_click;
    private MaterialEditText lessonSearch;
    private View RootView;
    FirebaseUser uid;
    private CardView cardView;
    private Toolbar toolbar;
    String Major;
   public StorageReference fileToUpload, dataStorageReference;
    private String name,username;
    private RecyclerView LessonsName;
    Query reference;
    RecyclerView.LayoutManager layoutManager;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String UserID = firebaseUser.getUid();
    RelativeLayout relativeLayout_edittext,relativeLayout_post;
    private TextView lessonNAME_textview,tv_name;
    private CircleImageView profilePicture;
    StorageTask getmStorage;
    DatabaseReference UserReference,UserPost;
    HashMap<Integer , String > DATAS ;
    MaterialEditText post_not;
   HashMap<String,String> hashMap;
    HashMap<Object, Object> map;
    CircleImageView proPic;
    static String pushId;
    String lessonKey;
    String teacherID;
    String type;
    HashMap<String,String> user;
    HashMap<String ,String > notification = new HashMap<>();
    RelativeLayout progress ;
    ProgressBar progressBar;
    TextView labelData,labelPercent;
    private ProgressDialog progressDialog;
    String  notificationQuery;
    Map<String,Object> dB_map = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fost_note);
        getMajor(UserID);

        SetToolbar();
        progressDialog = new ProgressDialog(this);
        query=getIntent().getStringExtra("query");
        notificationQuery=getIntent().getStringExtra("notificationQuery");
        username=getIntent().getStringExtra("name");
        UserReference = FirebaseDatabase.getInstance().getReference();
        UserPost =FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
       post_click=(FloatingActionButton)findViewById(R.id.post_click);
        post_not=(MaterialEditText)findViewById(R.id.post_not);
        profilePicture = (CircleImageView)findViewById(R.id.profilePicture);
        tv_name=(TextView)findViewById(R.id.tv_username);
        map = new HashMap();
        notification.put("name",username);
        notification.put("send",UserID);
        notification.put("type"," Yeni Bir Not Paylaştı");
        user = new HashMap<>();
        progress=(RelativeLayout)findViewById(R.id.progress);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        labelData=(TextView)findViewById(R.id.labelData);
        labelPercent=(TextView)findViewById(R.id.labelPercent);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(UserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .centerCrop()
                        .resize(75,75)

                        .into(profilePicture);
                tv_name.setText(dataSnapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
        getRandomString(20);
        datasMap= new HashMap<>();
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        fileProgres = new ArrayList<>();
         sizeLabel = new ArrayList<>();
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);
        DATAS = new HashMap<>();
        hashMap= new HashMap();
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);

        mUploadList.setLayoutManager(new GridLayoutManager(PostNoteActivity.this,3,GridLayoutManager.VERTICAL,false));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);
        mSelectBtn=(FloatingActionButton)findViewById(R.id.selecte_image_button);

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileSelection();

            }
        });




        lessonNAME_textview=(TextView)findViewById(R.id._tv_lessonName);
        relativeLayout_edittext=(RelativeLayout)findViewById(R.id.realLay_editText);

        relativeLayout_post=(RelativeLayout)findViewById(R.id.relLayout_post);
    relativeLayout_edittext.setVisibility(View.VISIBLE);
        relativeLayout_post.setVisibility(View.INVISIBLE);
        lessonSearch=(MaterialEditText)findViewById(R.id.searchLesson_note);
        LessonsName =(RecyclerView)findViewById(R.id.recyclerView_LessonsName);
        LessonsName.setHasFixedSize(true);
        LessonsName.setLayoutManager(new LinearLayoutManager(PostNoteActivity.this));


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

                progressDialog.setMessage("Lütfen Bekleyiniz  ");

                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                setPublicPosts();
                setNotification();

                for (Map.Entry<String, String> entry : user.entrySet())
                {
                    final String key = entry.getKey().toString();
                    String value = entry.getValue();
                    setDatabaseNotification(Major,key,pushId,"Public_POST","false","NOT","Yeni Bir Not Paylaştı");

                }

                Intent intent = new Intent(PostNoteActivity.this,MainActivity.class);
                intent.putExtra("major",Major);
                startActivity(intent);
                finish();



            }
        });
    }

    private void openFileSelection()
    {

      /*  final Dialog options = new Dialog(PostNoteActivity.this);
        options.setContentView(R.layout.post_dialog);
        options.show();*/

       Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(Intent.createChooser(intent,"Dosya Seçiniz"),FILE_SELECT_CODE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this,"Bişeyler Ters Gitti",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){

            if(data.getClipData() != null){

                int totalItemsSelected = data.getClipData().getItemCount();

                for(int i = 0; i < totalItemsSelected; i++){

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    final String fileName = getFileName(fileUri);
                    DATAS.put(i,fileName);
                    fileNameList.add(fileName);

                    fileDoneList.add("uploading");


                  //  uploadListAdapter.notifyDataSetChanged();

                 fileToUpload = mStorage.child(UserID).child("Images").child(fileName);

                    final int finalI = i;
                    final int finalI1 = i;
                    fileToUpload.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {
                            if(task.isSuccessful())
                            {

                                fileDoneList.remove(finalI);
                                fileDoneList.add(finalI, "done");

                                uploadListAdapter.notifyDataSetChanged();
                                progress.setVisibility(View.INVISIBLE);

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progress.setVisibility(View.VISIBLE);
                            double progresSize = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int)progresSize);
                            String progressText =taskSnapshot.getBytesTransferred()/1024+"KB/"+taskSnapshot.getTotalByteCount()/1024+"KB";
                            labelData.setText(progressText);
                            labelPercent.setText((int)progresSize+"%");
                        }
                    });


                }

              //  setHasMap();
                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null){

                Toast.makeText(PostNoteActivity.this, "Selected Single File", Toast.LENGTH_SHORT).show();

            }

        }



    }
    private void GetLessons(String Name)
    {
        reference =  FirebaseDatabase.getInstance().getReference().child(Major).child("Lessons").orderByChild("lessonName").startAt(Name).endAt(Name+"\uf8ff");
        FirebaseRecyclerOptions<iste.not.com.FragmentDialogs.LessonsName> options
                = new FirebaseRecyclerOptions.Builder<LessonsName>().setQuery(reference, iste.not.com.FragmentDialogs.LessonsName.class)
                .setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<iste.not.com.FragmentDialogs.LessonsName,LessonsViewHolder> adapter
                = new FirebaseRecyclerAdapter<iste.not.com.FragmentDialogs.LessonsName, LessonsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LessonsViewHolder holder, final int position, @NonNull final iste.not.com.FragmentDialogs.LessonsName model)
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

    public class LessonsViewHolder extends RecyclerView.ViewHolder
    {   View view;

        public LessonsViewHolder (View itemView)
        {
            super(itemView);
            view=itemView;
        }
        public void GetName (String _name)
        {
            TextView  Tv_LessonName =(TextView)view.findViewById(R.id.TV_LessonName);
            Tv_LessonName.setText(_name);
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
        actionBar.setTitle("Not Paylaş");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
    private void setPublicPosts()
    {
         final long currentTime = Calendar.getInstance().getTimeInMillis();
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

         final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major)
                 .child(query)
                .child(pushId);

         databaseReference.child("comment").setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()) Log.d(TAG, "onComplete: "+"Yorumlar Kaydedildi");
             }
         });
         databaseReference.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                }

             }
         });
         databaseReference.child("lessonName").setValue(lessonNAME_textview.getText().toString())
         .addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful())
                 {
                 }
             }
         })

         ;
         databaseReference.child("time").setValue(-1*currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                 }
             }
         });
         databaseReference.child("count").setValue( DATAS.size()).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful())
                 {

                 }
             }
         });
         databaseReference.child("lessonKey").setValue(lessonKey).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Log.d("lessonKey", "onComplete: "+lessonKey+" :eklendi");
                 }


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
                             .child(query)
                             .child(pushId);
                     databaseReference.child("data").push().child("image").setValue(downloadUrl);

                 }
             });
             Log.d(TAG, "post: " + value + "  " + key);

         }


     }
     private void setDatabaseNotification(String _major, String _getterUid, final String  _key,
                                          String _query, String _seen, String _title, String _type
                                          )
     {
         final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                 .child("databaseNotifications")
                 .child(_getterUid);
         dB_map.put("getterUid",_getterUid);
         dB_map.put("query",_query);
         dB_map.put("seen",_seen);
         dB_map.put("title",_title);
         dB_map.put("type",_type);
         dB_map.put("senderUid",UserID);
         dB_map.put("sender",username);
         dB_map.put("time",-1*Calendar.getInstance().getTimeInMillis());
         dB_map.put("count",DATAS.size());
         dB_map.put("lessonName",lessonNAME_textview.getText().toString());
         dB_map.put("lessonKey",lessonKey);
         dB_map.put("key",pushId);

         databaseReference.child("AllNotification").child(pushId).updateChildren(dB_map).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task)
             {
                if (task.isSuccessful())
                {
                    databaseReference.child("NotificationCount").child(pushId).child("New Notification").setValue("yeni")
                    ;
                }
             }
         });

     }
    private void setNotification()
    {

            for (Map.Entry<String, String> entry : user.entrySet())
            {
                final String key = entry.getKey().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification")
                        .child(key).child(pushId);
                reference.setValue(notification);

            }


        }
        List<String> students = new ArrayList<>();

    private void sendNotification(String _lessonKey, String _teacherID, String _major, final String _current_user)
    {

            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("Teacher")
                    .child(_teacherID).child("myLessons").child(_lessonKey).child("Uid");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (final DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if (!ds.getKey().equals(_current_user)&&!ds.getKey().equals(teacherID))
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

}
