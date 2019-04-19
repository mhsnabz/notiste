package iste.not.com.scientist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;

public class ScientisLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    CircleImageView profilePic;
    MaterialEditText Emaillogin,password;
    Button next,loginButton;
    String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth= FirebaseAuth.getInstance();
        setContentView(R.layout.activity_scientis_login);

        loginButton=(Button)findViewById(R.id.login);
        Emaillogin=(MaterialEditText)findViewById(R.id.scientisEmail);
        password=(MaterialEditText)findViewById(R.id.scientisPassword);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogin();
            }
        });

    }


    private void setLogin()
    {
        String email = Emaillogin.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Emaillogin.setError("Lütfen Geçerli Bir E-Posta Adresi Giriniz ");
            Emaillogin.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            Emaillogin.setError("Lütfen Geçerli Bir E-Posta Adresi Giriniz ");
            Emaillogin.requestFocus();
            return;
        }
        final String Scientistpassword = password.getText().toString();
        if (Scientistpassword.isEmpty())
        {
            password.setError("Lütfen Şifrenizi Giriniz ");
            password.requestFocus();
        }
            mAuth.signInWithEmailAndPassword(email,Scientistpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                    currentUser=firebaseUser.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses").child(currentUser);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                Intent intent = new Intent(ScientisLoginActivity.this,ScientistMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                             {
                              Emaillogin.setError("Buraya Erişim İzniniz Yok");
                              Emaillogin.requestFocus();
                              password.setText("");
                              return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(ScientisLoginActivity.this,ScientistMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

    }

}
