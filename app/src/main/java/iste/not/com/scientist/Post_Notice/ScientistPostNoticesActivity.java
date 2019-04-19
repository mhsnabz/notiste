package iste.not.com.scientist.Post_Notice;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.POST.UploadListAdapter;
import iste.not.com.R;

import static android.content.ContentValues.TAG;

public class ScientistPostNoticesActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    static String pushId;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int FILE_SELECT_CODE = 2;
    public StorageReference fileToUpload, dataStorageReference;
    private StorageReference mStorage;
    private UploadListAdapter uploadListAdapter;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    HashMap<Integer , String > DATAS ;
    HashMap<String,String> hashMap;
    HashMap<String ,String > notification = new HashMap<>();
    HashMap<String,String> datasMap = new HashMap<>();
    HashMap<String,String> user;

    String lessonName,major,currentUser,lessonKey,name,query,image;
    TextView _tv_lessonName,tv_username,upload_filename,labelData,labelPercent;
    CircleImageView profilePicture;
    RecyclerView upload_list;
    MaterialEditText post_not;
    FloatingActionButton post_click;
    FloatingActionButton selecte_image_button;
    RelativeLayout progress;
    ProgressBar vertical_progressBar;
    Map<String,Object> dbMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_post_notices);
        image=getIntent().getStringExtra("image");

        image=getIntent().getStringExtra("image");
        //  lessonName=getIntent().getStringExtra("lessonName");
        mStorage = FirebaseStorage.getInstance().getReference();
        major=getIntent().getStringExtra("major");
        currentUser=getIntent().getStringExtra("currentUser");
        lessonKey=getIntent().getStringExtra("lessonKey");
        name=getIntent().getStringExtra("name");
        query=getIntent().getStringExtra("query");
        _tv_lessonName =(TextView)findViewById(R.id._tv_lessonName);
        tv_username =(TextView)findViewById(R.id.tv_username);
        profilePicture=(CircleImageView)findViewById(R.id.profilePicture);
        post_not=(MaterialEditText)findViewById(R.id.post_not);
        post_click=(FloatingActionButton)findViewById(R.id.post_click);
        selecte_image_button=(FloatingActionButton)findViewById(R.id.selecte_image_button);

        upload_list=(RecyclerView)findViewById(R.id.upload_list);
        progress=(RelativeLayout)findViewById(R.id.progress);
        upload_filename=(TextView)findViewById(R.id.upload_filename);
        vertical_progressBar=(ProgressBar)findViewById(R.id.vertical_progressBar);
        labelData=(TextView)findViewById(R.id.labelData);
        labelPercent=(TextView)findViewById(R.id.labelPercent);


        getRandomString(20);
        datasMap= new HashMap<>();
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        DATAS = new HashMap<>();
        hashMap= new HashMap();
        user = new HashMap<>();
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);
        upload_list.setLayoutManager(new GridLayoutManager(ScientistPostNoticesActivity.this,3,GridLayoutManager.VERTICAL,false));
        upload_list.setHasFixedSize(true);

        notification.put("send",currentUser);
        notification.put("type"," Yeni Bir Duyuru Yaptı");

        upload_list.setAdapter(uploadListAdapter);
        selecte_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               /* final Dialog options = new Dialog(ScientistSinglePostActivity.this);
                options.setContentView(R.layout.post_dialog);
                options.show();*/
                openFileSelection();
            }
        });
        dbMap = new HashMap<>();
        dbMap.put("count",DATAS.size());
        dbMap.put("key",pushId);
        dbMap.put("major",major);
        dbMap.put("senderUid",currentUser);
        dbMap.put("lessonKey",lessonKey);
        dbMap.put("query","Teacher_Post");
        dbMap.put("type","Yeni Bir Duyuru Yaptı");
        dbMap.put("time",-1*Calendar.getInstance().getTimeInMillis());
        dbMap.put("seen","false");



        load(profilePicture,_tv_lessonName,tv_username,image,lessonKey,getIntent().getStringExtra("name"),major,getIntent().getStringExtra("currentUser"));
        post_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPublicPosts(major,name);
                setNotification(major);
                sendNotification(lessonKey,currentUser,major,currentUser);
                finish();
            }
        });
    }
    private void setNotification(String _major)
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
    private void openFileSelection()
    {
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
            Toast.makeText(ScientistPostNoticesActivity.this,"Bişeyler Ters Gitti",Toast.LENGTH_SHORT).show();
        }
    }

    private void setPublicPosts(final String Major, String username)
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String comment =post_not.getText().toString();
        hashMap.put("yorum",comment);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major)
                .child("Teacher") .child(currentUser)
                .child("myLessons")
                .child(lessonKey)
                .child("Notices")
                .child(pushId);

        databaseReference.child("comment").setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Log.d("setPublicPosts", "onComplete: "+"Yorumlar Kaydedildi");
            }
        });
        databaseReference.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        databaseReference.child("lessonName").setValue(_tv_lessonName.getText().toString());
        databaseReference.child("time").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    notification.put("name",tv_username.getText().toString());
                    notification.put("title",_tv_lessonName.getText().toString());
                    dbMap.put("sender",tv_username.getText().toString());
                    dbMap.put("title",_tv_lessonName.getText().toString());
                    dbMap.put("lessonName",_tv_lessonName.getText().toString());
                    Log.d("setPublicPosts", "onComplete: "+"Saat kaydedildi");
                }
            }
        });
        databaseReference.child("userId").setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                    Log.d("setPublicPosts", "onComplete: "+"UserId Kaydedildi");

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
                Log.d("lessonKey", "onComplete: "+lessonKey+" :eklendi");

            }
        });
        databaseReference.child("teacherID").setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("teacherID", "onComplete: "+currentUser+" :eklendi");

            }
        });

        for (Map.Entry<Integer, String> entry : DATAS.entrySet()) {
            final String key = entry.getKey().toString();
            String value = entry.getValue();
            dataStorageReference = FirebaseStorage.getInstance().getReference();
            dataStorageReference.child(currentUser).child("Images").child(value).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl = uri.toString();

                    datasMap.put(key, downloadUrl);
                    Log.d("setPublicPosts", "downloadUrl: " + downloadUrl);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major)
                            .child("Teacher") .child(currentUser)
                            .child("myLessons")
                            .child(lessonKey)
                            .child("Post")
                            .child(pushId);

                    databaseReference.child("data").push().child("image").setValue(downloadUrl);

                }
            });
            Log.d("setPublicPosts", "post: " + value + "  " + key);

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

                    fileToUpload = mStorage.child(currentUser).child("Images").child(fileName);

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
                            vertical_progressBar.setProgress((int)progresSize);
                            String progressText =taskSnapshot.getBytesTransferred()/1024+"KB/"+taskSnapshot.getTotalByteCount()/1024+"KB";
                            labelData.setText(progressText);
                            labelPercent.setText((int)progresSize+"%");
                        }
                    });


                }

                //  setHasMap();
                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null){

                Toast.makeText(ScientistPostNoticesActivity.this, "HATA", Toast.LENGTH_SHORT).show();

            }

        }



    }

    private void load(final CircleImageView pic, final TextView tv_lessonName, final TextView tv_username, final String image, String _lessonKey, final String name, String _major, String  _currentUser)
    {
        final ProgressBar progressBar =(ProgressBar)findViewById(R.id.progressBar);

        DatabaseReference Reference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(_currentUser);
        Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).centerCrop().resize(512,512)
                        .placeholder(R.drawable.scientist_black)
                        .into(pic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                tv_username.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major).child("Lessons")
                .child(_lessonKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                tv_lessonName.setText(dataSnapshot.child("lessonName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendNotification(String _lessonKey, String _teacherID, final String _major, final String _current_user)
    {
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notification");

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major).child("Teacher")
                .child(_teacherID).child("myLessons").child(_lessonKey).child("Uid");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (!ds.getKey().equals(_current_user))
                    {
                        user.put(dataSnapshot.getKey(),dataSnapshot.getKey());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(_major)
                                .child("databaseNotifications")
                                .child(ds.getKey())
                                .child("AllNotification")
                                .push();
                        databaseReference.setValue(dbMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                                            .child(_major)
                                            .child("databaseNotifications")
                                            .child(ds.getKey())
                                            .child("NotificationCount");
                                    reference2.child(pushId).child("New Notification").setValue("yeni")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification")
                                                            .child(ds.getKey()).push();
                                                    reference.setValue(notification);

                                                }
                                            });
                                }
                                else Log.w("setDBNotificationTeachr", "onComplete: ", task.getException());
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
}
