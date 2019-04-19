package iste.not.com.scientist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;
import iste.not.com.SplashScreen;
import iste.not.com.scientist.GetMyStudent.LessonsStudentsActivity;
import iste.not.com.scientist.Post_Notice.ChooseNoticesLessonActivity;
import iste.not.com.scientist.menu.ChoosLessonActivity;
import iste.not.com.scientist.menu.ExamsResultActivity;
import iste.not.com.scientist.menu.PostNoteScientistActivity;
import iste.not.com.scientist.menu.ScientistMessagesActivity;
import iste.not.com.settingAndEdit.SettingActivity;

public class ScientistMainActivity extends AppCompatActivity {
    private static final int izin = 1;
    Toolbar toolbar;
    CircleImageView pro_pic;
    TextView name,priotiry ;
    Button logout;
    FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
    String UserID = current_user.getUid();
    ProgressBar progressBar;
    CardView postNoteCard;
    String major,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_main);
        checkPermisson();
        toolbar = (Toolbar) findViewById(R.id.MianToolBar);
        UserID = current_user.getUid();
        setSupportActionBar(toolbar);
        name=(TextView)findViewById(R.id.name);
        priotiry=(TextView)findViewById(R.id.priotiry);
        final ActionBar actionBar = getSupportActionBar();
        logout=(Button)findViewById(R.id.logout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ScientistMainActivity.this,SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });
       /// actionBar.setDisplayHomeAsUpEnabled(false);
        pro_pic=(CircleImageView)findViewById(R.id.profileimage);
        pro_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(ScientistMainActivity.this,ScientistProfileActivity.class);
                intent.putExtra("major",major);
            startActivity(intent);
            }
        });
        setToolbar(pro_pic,name,priotiry);
    }

    private void checkPermisson()
    {
        Log.d("permission", "checkPermisson: "+"asking users");
        String[] permissisons = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA
        };
        if (ContextCompat.checkSelfPermission(ScientistMainActivity.this,permissisons[0])== PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(ScientistMainActivity.this,permissisons[1])==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(ScientistMainActivity.this,permissisons[2])==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(ScientistMainActivity.this,permissisons[3])==PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(ScientistMainActivity.this,permissisons,izin);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermisson();
    }
    private void setToolbar(final CircleImageView pro_pic, final TextView _name,final TextView _priotiry)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(UserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                image=dataSnapshot.child("image").getValue().toString();
                major=dataSnapshot.child("major").getValue().toString();
                if (dataSnapshot.child("image").getValue().toString().equals("null"))       progressBar.setVisibility(View.GONE);
                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                .resize(512,512)
                .centerCrop()
                .placeholder(R.drawable.scientist_white)
                .into(pro_pic, new Callback() {
                    @Override
                    public void onSuccess()
                    {

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                _name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());

                if(dataSnapshot.child("priority").getValue()!=null)
                { _priotiry.setText("#"+dataSnapshot.child("priority").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void postNoteClick(View view)
    {
        Intent intent = new Intent(ScientistMainActivity.this, PostNoteScientistActivity.class);
        intent.putExtra("major",major);
        intent.putExtra("currentUser",UserID);
        intent.putExtra("name",name.getText().toString());
        intent.putExtra("image",image);
        startActivity(intent);
    }

    public void sinavSonuclariniPaylas(View view)
    {
            Intent intent = new Intent(ScientistMainActivity.this, ChoosLessonActivity.class);
            intent.putExtra("major",major);
             intent.putExtra("currentUser",UserID);
             intent.putExtra("name",name.getText().toString());
             intent.putExtra("image",image);
            startActivity(intent);
    }

    public void getStudents(View view)
    {
            Intent intent = new Intent(ScientistMainActivity.this, LessonsStudentsActivity.class);
            intent.putExtra("major",major);
            intent.putExtra("currentUser",UserID);
            startActivity(intent);
    }

    public void makeNotices(View view)
    {
        Intent intent = new Intent(ScientistMainActivity.this, ChooseNoticesLessonActivity.class);
        intent.putExtra("major",major);
        intent.putExtra("currentUser",UserID);
        intent.putExtra("name",name.getText().toString());
        intent.putExtra("image",image);
        startActivity(intent);
    }

    public void messages(View view)
    {
        Intent intent = new Intent(ScientistMainActivity.this, ScientistMessagesActivity.class);
        startActivity(intent);
    }
}
