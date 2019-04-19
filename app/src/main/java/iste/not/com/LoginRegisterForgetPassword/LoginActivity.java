package iste.not.com.LoginRegisterForgetPassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import iste.not.com.Main.MainActivity;
import iste.not.com.R;
import iste.not.com.scientist.ScientisLoginActivity;
import iste.not.com.scientist.ScientistMainActivity;

public class LoginActivity extends AppCompatActivity {
    Button createAccount;
    private ProgressDialog progressDialog;
    ImageView logo;
    MaterialEditText email,password;
    Button loginButton;
    TextView forgetPassword;
    RelativeLayout rel1,rel2;
    private FirebaseAuth mAuth;
    Context context=this;
    ScientisEmailAdres scientisEmailAdres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        logo=(ImageView)findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ScientistMainActivity.class);
                startActivity(intent);

            }
        });
        createAccount=(Button)findViewById(R.id.goRegister);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerFonk();
            }
        });
        forgetPassword=(TextView)findViewById(R.id.forgerPassWord) ;



        mAuth= FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        email=(MaterialEditText)findViewById(R.id.email);
        password=(MaterialEditText)findViewById(R.id.password);
        loginButton=(Button)findViewById(R.id.loginClick);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFonk();
            }
        });

    }
    public void registerFonk()
    {
        Intent createAccount = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(createAccount);
    }
    private void LoginFonk() {


        String Useremail=email.getText().toString();
        String Userpassword = password.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(Useremail).matches())
        {
            email.setError("Lütfen Geçerli Bir E-Posta Adresi Giriniz ");
            email.requestFocus();
            return;
        }
        if (!Useremail.isEmpty())
        {
            getEmailAdreses(Useremail,Useremail,Userpassword,email,password);
        }
        if (Useremail.isEmpty())
        {
            email.setError("Lütfen E-Posta Adresini Giriniz ");
            email.requestFocus();
            return;
        }
        if (Userpassword.isEmpty())
        {
            password.setError("Lütfen Şifrenizi Giriniz ");
            password.requestFocus();
        }
        progressDialog.setMessage("Lütfen Bekleyiniz ! ");
        progressDialog.setTitle("Giriş Yapılıyor");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    public void  myfonk(View v){
        if(v.getId() == R.id.goRegister){
            registerFonk();
        }
        if (v.getId()== R.id.logo)
        {
            Intent intent = new Intent(LoginActivity.this,ScientisLoginActivity.class);
            startActivity(intent);
        }
        if (v.getId()==R.id.forgerPassWord)
        {
            Intent forgetpassword = new Intent(context,ForgetPassword.class);
            startActivity(forgetpassword);
        }
    }

    private void getEmailAdreses(final String Email , final String Useremail, final String Userpassword,final MaterialEditText email, final  MaterialEditText password)
    {
       scientisEmailAdres = new ScientisEmailAdres();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey().toString();
                  DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses").child(key);
                  databaseReference1.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                      {
                       String eposta = dataSnapshot.child("email").getValue().toString();
                          Log.i("email", "onDataChange: "+eposta);
                          if (eposta.equals(Email))
                          {
                              Intent intent = new Intent(LoginActivity.this,ScientisLoginActivity.class);
                              startActivity(intent);
                              finish();
                          }
                          else
                          {
                              mAuth.signInWithEmailAndPassword(Useremail,Userpassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task)
                                  {
                                      if(task.isSuccessful())
                                      {    FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                                          String UserID = current_user.getUid();
                                          String tokenId = FirebaseInstanceId.getInstance().getToken();
                                          DatabaseReference TokenID = FirebaseDatabase.getInstance().getReference().child("UserToken");
                                          TokenID.child(UserID).child("tokenId").setValue(tokenId);

                                          DatabaseReference reference=FirebaseDatabase.getInstance()
                                                  .getReference().child("Students").child(UserID);
                                          reference.addValueEventListener(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                  String major =dataSnapshot.child("major").getValue().toString();
                                                  Intent LoginIntent = new Intent(LoginActivity.this,MainActivity.class);
                                                  LoginIntent.putExtra("major",major);
                                                  startActivity(LoginIntent);
                                                  finish();
                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });


                                      }
                                      else {
                                          progressDialog.setMessage("Lütfen Bekleyiniz ! ");
                                          progressDialog.setTitle("Giriş Yapılıyor");
                                          progressDialog.setCanceledOnTouchOutside(false);
                                          progressDialog.show();
                                          email.setError("E-posta Yada Şifreniz Hatalı");
                                          email.requestFocus();

                                          password.setError("E-posta Yada Şifreniz Hatalı");
                                          password.requestFocus();
                                          progressDialog.cancel();
                                      }

                                  }
                              });
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
