package iste.not.com;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import iste.not.com.LoginRegisterForgetPassword.LoginActivity;
import iste.not.com.Main.MainActivity;

public class NotIste extends Application
{
    Map <String,Object > token = new HashMap<>();
    private FirebaseAuth auth,firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String currentUser;
     DatabaseReference databaseReference,reference;

    @Override
    public void onCreate() {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);




        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null)
                    {
                        auth=FirebaseAuth.getInstance();
                        currentUser=auth.getCurrentUser().getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("IsOnline").child(currentUser);
                        reference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUser);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String deviceToken = instanceIdResult.getToken();
                                token.put("tokenId",deviceToken);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserToken").child(currentUser);
                                reference.updateChildren(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (!task.isSuccessful())
                                            Log.w("token", "onComplete: ",task.getException() );
                                    }
                                });
                                // i.e. store it on SharedPreferences or DB
                                // or directly send it to server
                            }
                        });
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot!=null)
                                {
                                    databaseReference.child("online").onDisconnect().setValue("false");
                                    databaseReference.child("online").setValue("true");
                                    databaseReference.child("lastOnline").onDisconnect().setValue(-1*Calendar.getInstance().getTimeInMillis());
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    });


        super.onCreate();

    }

    private void isOnline()
    {

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot!=null)
                        {
                            databaseReference.child("online").onDisconnect().setValue("false");
                            databaseReference.child("online").setValue("true");
                            databaseReference.child("lastOnline").onDisconnect().setValue(-1*Calendar.getInstance().getTimeInMillis());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
    }



}
