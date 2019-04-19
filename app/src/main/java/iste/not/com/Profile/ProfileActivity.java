package iste.not.com.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.MainActivity;
import iste.not.com.Messages.MessagesActivity;
import iste.not.com.Profile.ExamsResults.ProfileExamsResultsActivity;
import iste.not.com.Profile.ScientistPosted.MyLessonActivity;
import iste.not.com.R;
import iste.not.com.settingAndEdit.EditActivity;
import iste.not.com.settingAndEdit.SettingActivity;

public class ProfileActivity extends AppCompatActivity
{
    private static final int GALLERY=1;
    ImageButton back;
    CircleImageView profileImage,add_image;
    TextView name,Class,major;
    ViewPager profileViewPager;
    ImageButton edit,setting,scientist;
    CardView notificationCard,messagesCard,myPostCard,examsResults,noticesCard,notesCard;
    TextView notificationCount,messagesCount;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String CurrentUserId;
    DatabaseReference dateBaseUserInfo;
    ProgressDialog progressDialog;
    private StorageReference imageStorage;
    DatabaseReference imageDatabaseReference;
    String UserMajor;
    String image;

    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
    DatabaseReference databaseReference,reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        notificationCard=(CardView)findViewById(R.id.notificationCard);
        currentUser=getIntent().getStringExtra("current_user_id");
        messagesCard=(CardView)findViewById(R.id.messagesCard);
        myPostCard=(CardView)findViewById(R.id.myPostCard);
        examsResults=(CardView)findViewById(R.id.examsResults);
        notesCard=(CardView)findViewById(R.id.notesCard);
        noticesCard=(CardView)findViewById(R.id.noticesCard);
        notificationCount=(TextView)findViewById(R.id.notificationCount);
        messagesCount=(TextView)findViewById(R.id.messagesCount);
        CurrentUserId=FirebaseAuth.getInstance().getUid();
        imageStorage= FirebaseStorage.getInstance().getReference();
        add_image=(CircleImageView)findViewById(R.id.add_image);
     //   UserMajor=getIntent().getStringExtra("major");
        name=(TextView)findViewById(R.id.name) ;
        Class=(TextView)findViewById(R.id.Class);
        major=(TextView)findViewById(R.id.major);
        back =(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("major",UserMajor);
                startActivity(intent);
                finish();
            }
        });
        profileImage =(CircleImageView)findViewById(R.id.profilePic);
        edit=(ImageButton)findViewById(R.id.edit);
        scientist=(ImageButton)findViewById(R.id.scientist);
        imageDatabaseReference=FirebaseDatabase.getInstance().getReference().child("Students").child(CurrentUserId);
        setting=(ImageButton)findViewById(R.id.settings);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ProfileActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        scientist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ProfileActivity.this,scientistActivity.class);
                intent.putExtra("currentUser",CurrentUserId);
                intent.putExtra("major",UserMajor);
                startActivity(intent);
            }
        });
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"RESİM SEÇ"),GALLERY);

            }
        });
        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,NotificationActivity.class);
                intent.putExtra("major",UserMajor);
                intent.putExtra("current_user",CurrentUserId);
                startActivity(intent);
            }
        });
        myPostCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,MyPostActivity.class);
                intent.putExtra("major",UserMajor);
                intent.putExtra("current_user",CurrentUserId);
                intent.putExtra("name",name.getText().toString());
                startActivity(intent);
            }
        });
        messagesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,MessagesActivity.class);
                intent.putExtra("current_user",CurrentUserId);
                startActivity(intent);
            }
        });
        notesCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProfileActivity.this, MyLessonActivity.class);
                intent.putExtra("current_user",CurrentUserId);
                intent.putExtra("major",UserMajor);
                startActivity(intent);
            }
        });
        LoadProfileInfo(CurrentUserId);
        setNotificationCount(CurrentUserId,notificationCount);
        setMessagesCount(CurrentUserId,messagesCount);



    }



    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void LoadProfileInfo(final String keyX)
    {
        final DatabaseReference UserInfo = FirebaseDatabase.getInstance().getReference().child("Students").child(keyX);
        UserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String TAG ="image";
                    String image =dataSnapshot.child("image").getValue().toString();
                    Log.d(TAG, "onDataChange: "+image);
                    Picasso.get().load(image).resize(1024,1024).centerCrop().placeholder(R.drawable.placeholder_image).into(profileImage);
                    String Name=dataSnapshot.child("name").getValue().toString();
                    name.setText(Name);
                    String UserClass =dataSnapshot.child("Class").getValue().toString();
                    Class.setText(UserClass);
                   UserMajor = dataSnapshot.child("major").getValue().toString();
                    if(UserMajor.equals("ComputerEngineering"))
                        major.setText("Bilgisayar Mühendisliği");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY&& resultCode==RESULT_OK )
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                progressDialog= new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("RESİM YÜKLENİYOR");
                progressDialog.setMessage("Lütfen Bekleyiniz ");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = imageStorage.child(CurrentUserId).child("images").child(CurrentUserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful()) {
                            imageStorage.child(CurrentUserId).child("images").child(CurrentUserId+".jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {
                                            String dwonloadUri=uri.toString();
                                            imageDatabaseReference.child("image").setValue(dwonloadUri).addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    }) ;
                        }

                    }
                });
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error =result.getError();
            }
        }
    }

    private void setMessagesCount( String _currentUser, final TextView _text)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DatabaseMessagesNotifcation")
                .child(_currentUser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChildren()){
                    if (dataSnapshot.getChildrenCount()>0)
                    {                    _text.setVisibility(View.VISIBLE);

                        _text.setText(Long.toString(dataSnapshot.getChildrenCount()));

                    }
                    else _text.setVisibility(View.INVISIBLE);
                    }
                    else _text.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setNotificationCount(final String _currentUser, final TextView _text)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_currentUser);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(dataSnapshot.child("major").getValue().toString())
                        .child("databaseNotifications")
                        .child(_currentUser)
                        .child("NotificationCount");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.getChildrenCount()>0)
                        {                    _text.setVisibility(View.VISIBLE);

                            _text.setText(Long.toString(dataSnapshot.getChildrenCount()));

                        }
                        else _text.setVisibility(View.INVISIBLE);
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

    public void setExamsResults(View view)
    {
        Intent intent = new Intent(ProfileActivity.this, ProfileExamsResultsActivity.class);
        intent.putExtra("major",UserMajor);
        intent.putExtra("currentUser",getIntent().getStringExtra("current_user_id"));
        startActivity(intent);

    }
}
