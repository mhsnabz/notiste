package iste.not.com.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Helper.MainViewPager;
import iste.not.com.NotificationService.FirebaseInstanceService;
import iste.not.com.POST.ImageLoaderActivity;
import iste.not.com.Profile.ProfileActivity;
import iste.not.com.R;

public class MainActivity extends AppCompatActivity {
    private static final int izin = 1;
    private FirebaseAuth auth;

    String currentUser;
    DatabaseReference databaseReference,reference;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    LinearLayout messegesLin,notificationLin ;
    CircleImageView profile;
    TextView askNote,askQuestions,Notice;
    ViewPager MainViewPager;
    FirebaseInstanceService firebaseInstanceService;
    private iste.not.com.Helper.MainViewPager pagerViewAdapeter;
    String major;
    TextView notificationCount,messageCount;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermisson();
        messegesLin=(LinearLayout)findViewById(R.id.messaheNotLayout) ;
        notificationLin=(LinearLayout)findViewById(R.id.notificationLayout) ;
        notificationCount=(TextView)findViewById(R.id.notificationCount);

        messageCount=(TextView)findViewById(R.id.messageCount);
        major=getIntent().getStringExtra("major");
        currentUser=firebaseUser.getUid();
        profile =(CircleImageView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ProfileIntent = new Intent(MainActivity.this,ProfileActivity.class);
                ProfileIntent.putExtra("major",major);
                startActivity(ProfileIntent);
                finish();
            }
        });
        askNote=(TextView)findViewById(R.id.askNote);
        askQuestions=(TextView)findViewById(R.id.askQuestions);
        Notice=(TextView)findViewById(R.id.notice);
        MainViewPager = (ViewPager)findViewById(R.id.mainViewPager);
        pagerViewAdapeter= new iste.not.com.Helper.MainViewPager(getSupportFragmentManager());
        MainViewPager.setAdapter(pagerViewAdapeter);

        loadImage(currentUser);
        setMessageNotificationCount(currentUser,messageCount,messegesLin);
        setNotificationCount(major,currentUser,notificationCount,notificationLin);

        MainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        askNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                MainViewPager.setCurrentItem(0);
            }
        });
        askQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainViewPager.setCurrentItem(1);
            }
        });
        Notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainViewPager.setCurrentItem(2);
            }
        });
    }
    private void changeTabs(int i)
    {

        String TAG ="changeTabsFunc";
        if(i==0)
        {

                askNote.setTextColor(getColor(R.color.colorPrimary));

            askNote.setTextSize(22);
            askQuestions.setTextColor(getColor(R.color.Ligth));
            askQuestions.setTextSize(16);
            Notice.setTextColor(getColor(R.color.Ligth));
            Notice.setTextSize(16);
        }
        if(i==1)
        {
            askNote.setTextColor(getColor(R.color.Ligth));
            askNote.setTextSize(16);
            askQuestions.setTextColor(getColor(R.color.colorPrimary));
            askQuestions.setTextSize(22);
            Notice.setTextColor(getColor(R.color.Ligth));
            Notice.setTextSize(16);
        }
        if(i==2)
        {

                askNote.setTextColor(getColor(R.color.Ligth));

            askNote.setTextSize(16);
            askQuestions.setTextColor(getColor(R.color.Ligth));
            askQuestions.setTextSize(16);
            Notice.setTextColor(getColor(R.color.colorPrimary));
            Notice.setTextSize(22);
        }

        Log.d(TAG, "changeTabs: ");

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
    protected void onStart()
    {

        super.onStart();
    }

    @Override
    protected void onStop()
    {

        super.onStop();
    }

    private void loadImage(final String keyX)
    {
        final DatabaseReference UserInfo = FirebaseDatabase.getInstance().getReference().child("Students").child(keyX);
        UserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    String TAG ="image";
                    String image =dataSnapshot.child("image").getValue().toString();
                    Log.d(TAG, "onDataChange: "+image);
                    Picasso.get().load(image).resize(256,256).placeholder(R.drawable.placeholder_image).centerCrop().into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        if (ContextCompat.checkSelfPermission(MainActivity.this,permissisons[0])==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(MainActivity.this,permissisons[1])==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(MainActivity.this,permissisons[2])==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(MainActivity.this,permissisons[3])==PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,permissisons,izin);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermisson();
    }

    private void setMessageNotificationCount(String _currentUser, final TextView _text, final LinearLayout _linLayNotifCount)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DatabaseMessagesNotifcation")

                .child(_currentUser)
      ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.getChildrenCount()>0)
                {   if (dataSnapshot.getChildrenCount()<=9)
                {
                    _text.setText("+"+Long.toString(dataSnapshot.getChildrenCount()));
                    _linLayNotifCount.setVisibility(View.VISIBLE);
                }
                else
                    _text.setText(Long.toString(dataSnapshot.getChildrenCount()));
                    _text.setVisibility(View.VISIBLE);
                    _linLayNotifCount.setVisibility(View.VISIBLE);


                }
                else
                {
                    _text.setVisibility(View.INVISIBLE);
                    _linLayNotifCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setNotificationCount(String _major, String _currentUser, final TextView _text, final LinearLayout _linLayNotifCount)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("databaseNotifications")
                .child(_currentUser)
                .child("NotificationCount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.getChildrenCount()>0)

                {
                    if (dataSnapshot.getChildrenCount()<=9)
                    {
                        _text.setText("+"+Long.toString(dataSnapshot.getChildrenCount()));
                        _linLayNotifCount.setVisibility(View.VISIBLE);

                    }
                    else
                        _text.setText(Long.toString(dataSnapshot.getChildrenCount()));
                    _linLayNotifCount.setVisibility(View.VISIBLE);
                    _text.setVisibility(View.VISIBLE);


                }
                else
                {
                    _text.setVisibility(View.INVISIBLE);
                    _linLayNotifCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
