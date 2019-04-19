package iste.not.com.LoginRegisterForgetPassword;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import java.util.regex.Pattern;

import iste.not.com.Main.MainActivity;
import iste.not.com.R;

public class RegisterActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    private MaterialEditText username,name,email,password,studentNumber,passwordAgain;
    String major;
    private FirebaseAuth auth;
    private GridLayout mainGrid;
    Spinner spinner,sinif;
    private Button register;
    private Button registerButton;
    Map user = new HashMap();

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        mainGrid = (GridLayout) findViewById(R.id.mainGrid);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        registerButton=(Button)findViewById(R.id.registerClick);
        user = new HashMap();
        sinif = (Spinner)findViewById(R.id.spinner);

        ChooseMajor(mainGrid);
        LoadSpinner(RegisterActivity.this,sinif);
        username =(MaterialEditText) findViewById(R.id.userName);
        name = (MaterialEditText) findViewById(R.id.name);
        email=(MaterialEditText)findViewById(R.id.email);
        password=(MaterialEditText)findViewById(R.id.password);
        passwordAgain=(MaterialEditText) findViewById(R.id.password_again);
        studentNumber=(MaterialEditText)findViewById(R.id.student_number);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRegister();
            }
        });

    }

    private void LoadSpinner(Context context, Spinner spinner)
    {
       ArrayAdapter adapter = ArrayAdapter.createFromResource(context,R.array.classes,R.layout.support_simple_spinner_dropdown_item);
       spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        if (sinif.getSelectedItemPosition()<0)
        {
            sinif.setSelection(1);
        }
    }
    private void ChooseMajor(GridLayout gridLayout) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int index = i;
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (index == 0) {
                        major="ComputerEngineering";
                        user.put("major", "computerEngineer");
                        viewFlipper.showNext();
                    }
                    if (index == 1) {
                        major="electronicEngineer";
                        user.put("major", "electronicEngineer");
                        viewFlipper.showNext();
                    }
                    if (index == 2) {
                        major="machinalEngineer";
                        user.put("major", "machinalEngineer");
                        viewFlipper.showNext();
                    }
                    if (index == 3) {
                        major="civilEngineer";
                        user.put("major", "civilEngineer");
                        viewFlipper.showNext();
                    }
                }
            });
        }
    }
    private  void setRegister()
    {
        final String userName = username.getText().toString();
        final  String Email,Password,PasswordAgain,StudentNumber,Name;
        Email=email.getText().toString();
        Password=password.getText().toString();
        PasswordAgain=passwordAgain.getText().toString();
        StudentNumber = studentNumber.getText().toString();
        Name=name.getText().toString();
        if(userName.isEmpty())
        {
            username.setError("Lütfen Kullanıcı Adınızı Giriniz ");
            username.requestFocus();
            return;
        }
        if(Name.isEmpty())
        {
            name.setError("Lütfen Adınızı Giriniz ");
            name.requestFocus();
            return;
        }


        if(StudentNumber.isEmpty())
        {
            studentNumber.setError("Lütfen Öğrenici Numaranızı Giriniz ");
            studentNumber.requestFocus();
            return;
        }
        if(StudentNumber.length()!=9)
        {
            studentNumber.setError("Öğrenci Numaranız Doğru Değil");
            studentNumber.requestFocus();
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

        if (PasswordStrength.calculateStrength(password.getText().toString()).getValue() < PasswordStrength.STRONG.getValue())
        {
            password.setError("En az 6 Karakter , 1 tane Büyük ,1 tane Küçük,1 tane Numerik");
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

        if(sinif.getSelectedItemPosition()<2)
        {
            Toast.makeText(RegisterActivity.this,"Lütfen Sınıfınızı Seçiniz !!!",Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference userNames =
                FirebaseDatabase.getInstance().getReference("USERNAME").child("students");
        userNames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(userName))
                {
                    username.setError("Bu Kullanıcı Adı Zaten Kullanılıyor ");
                    username.requestFocus();
                    return;
                }
                else
                {
                    auth.createUserWithEmailAndPassword(Email,Password).
                            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        String image = "null";
                                        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                                        String UserID = current_user.getUid();

                                        SetUser( UserID,Name,userName,image,StudentNumber);

                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.putExtra("major",major);
                                        startActivity(intent);
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
    private void SetUser(String userID, String name, String userName, String image, String studentNumber)
    {
        Map UserNAME = new HashMap();
        Map notSetting = new HashMap();
        Map newUser = new HashMap<>();
        newUser.put("major",major);
        newUser.put("name",name);
        newUser.put("image",image);
        newUser.put("number",studentNumber);
        newUser.put("Class",String.valueOf(sinif.getSelectedItemPosition()-1));
        UserNAME.put(userID,userName);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("USERNAME").child("student");
        databaseReference.updateChildren(UserNAME);
        DatabaseReference newUserDB = FirebaseDatabase.getInstance().getReference("Students").child(userID);
        newUserDB.updateChildren(newUser);
        DatabaseReference notficationSetting= FirebaseDatabase.getInstance().getReference().child("NotificationSettings");
        notSetting.put("ask","true");
        notSetting.put("comment","true");
        notSetting.put("like","true");
        notSetting.put("messages","true");
        notSetting.put("messagesRequest","true");
        notSetting.put("notes","true");
        notSetting.put("notices","true");
        notficationSetting.child(userID).setValue(notSetting).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Log.w("notficationSetting", "onComplete: "+" onComplete" );
                }
            }
        });


    }
}
