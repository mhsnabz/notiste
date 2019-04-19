package iste.not.com;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import iste.not.com.LoginRegisterForgetPassword.RegisterActivity;
import iste.not.com.Main.MainActivity;

public class NewScientistActivity extends AppCompatActivity {
    MaterialEditText unvan, name,passwordAgain, password, email, major;
    private FirebaseAuth auth;
    Button registerClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scientist);
        unvan =(MaterialEditText) findViewById(R.id.unvan);
        name = (MaterialEditText) findViewById(R.id.name);
        email=(MaterialEditText)findViewById(R.id.email);
        password=(MaterialEditText)findViewById(R.id.password);
        passwordAgain=(MaterialEditText) findViewById(R.id.password_again);
        major=(MaterialEditText)findViewById(R.id.major);
        auth=FirebaseAuth.getInstance();
        registerClick=(Button)findViewById(R.id.registerClick);
        registerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRegister();
            }
        });
    }

    private  void setRegister()
    {
        final String Unvan = unvan.getText().toString();
        final  String Email,Password,PasswordAgain,Major,Name;
        Email=email.getText().toString();
        Password=password.getText().toString();
        PasswordAgain=passwordAgain.getText().toString();
        Major = major.getText().toString();
        Name=name.getText().toString();
        if(Unvan.isEmpty())
        {
            unvan.setError("Lütfen Kullanıcı Adınızı Giriniz ");
            unvan.requestFocus();
            return;
        }
        if(Name.isEmpty())
        {
            name.setError("Lütfen Adınızı Giriniz ");
            name.requestFocus();
            return;
        }


        if(Major.isEmpty())
        {
            major.setError("Lütfen Öğrenici Numaranızı Giriniz ");
            major.requestFocus();
            return;
        }

        if(Email.isEmpty())
        {
            email.setError("Lütfen E-posta Adresinizi Giriniz ");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            email.setError("Lütfen Geçerli Bir E-posta Adresi Giriniz ");
            email.requestFocus();
            return;
        }
        if(Password.isEmpty())
        {
            password.setError("Lütfen Şifrenizi Giriniz ");
            password.requestFocus();
            return;
        }
        if(PasswordAgain.isEmpty())
        {
            passwordAgain.setError("Lütfen Şifrenizi Giriniz ");
            passwordAgain.requestFocus();
            return;
        }

        if (!Password.equals(PasswordAgain))
        {
            password.setError("Şifreleriniz Aynı Değil ");
            password.requestFocus();
            passwordAgain.setError("Şifreleriniz Aynı Değil");
            passwordAgain.requestFocus();


            return;
        }

        auth.createUserWithEmailAndPassword(Email,Password).
                addOnCompleteListener(NewScientistActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Map newUser = new HashMap<>();
                            newUser.put("name",Name);
                            newUser.put("unvan",Unvan);
                            newUser.put("image","");
                            newUser.put("email",Email);

                            String image = "null";
                            FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                            String UserID = current_user.getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major).child("Teacher").child(UserID);
                            databaseReference .setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Toast.makeText(NewScientistActivity.this,"Yeni Kullanıcı Oluşturuldu",Toast.LENGTH_LONG).show();

                                        }
                                    });

                        }
                    }
                });

    }
}
