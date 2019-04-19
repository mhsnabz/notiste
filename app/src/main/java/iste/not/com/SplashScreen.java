package iste.not.com;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import iste.not.com.LoginRegisterForgetPassword.LoginActivity;
import iste.not.com.Main.MainActivity;
import iste.not.com.scientist.ScientistMainActivity;

public class SplashScreen extends AppCompatActivity {
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String CurrentUserId;
    ImageView logo;
    TextView labelConnection;

    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
    DatabaseReference databaseReference,reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        labelConnection=(TextView)findViewById(R.id.connectionLabel);
        logo =(ImageView)findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        logo.startAnimation(animation);


        Thread thread = new Thread()
        {
            public void run()
            {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {   CurrentUserId=FirebaseAuth.getInstance().getUid();
                    if(CurrentUserId==null)
                    {
                    Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    }
                    else
                    {

                        DatabaseReference databaseReference = FirebaseDatabase
                                .getInstance().getReference().child("Students");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(CurrentUserId))
                                {

                                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                                    i.putExtra("major",dataSnapshot.child(CurrentUserId).child("major").getValue().toString());
                                    startActivity(i);
                                    finish();


                                }
                                else
                                {

                                    Intent i = new Intent(SplashScreen.this,ScientistMainActivity.class);
                                    startActivity(i);
                                    finish();


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

            }

        };
        thread.start();
    }
    public boolean isConnectedToInternet()
    {
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected())
            {
                URL url = new URL("http://www.Google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200)  //Successful response.
                {
                    return true;
                }
                else
                {
                    Toast.makeText(this, "No connection to internet.", Toast.LENGTH_LONG).show();
                    Log.d("NO INTERNET", "NO INTERNET");
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Toast.makeText(this, "No connection to internet.", Toast.LENGTH_LONG).show();
        return false;
    }




}
