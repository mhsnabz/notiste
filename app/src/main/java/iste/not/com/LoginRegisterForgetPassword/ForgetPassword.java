package iste.not.com.LoginRegisterForgetPassword;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Pattern;

import iste.not.com.R;

public class ForgetPassword extends AppCompatActivity {
    ImageButton backArrow;
    Button sendEmailClick;
    MaterialEditText emailEditText;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        backArrow=(ImageButton)findViewById(R.id.back);
        emailEditText=(MaterialEditText)findViewById(R.id.sendEmail) ;
        auth=FirebaseAuth.getInstance();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPassword.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        sendEmailClick=(Button)findViewById(R.id.sendClick);
        sendEmailClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSendEmailClick();
            }
        });


    }
    private void setSendEmailClick()
    {
        String email = emailEditText.getText().toString();
        if (email.isEmpty())
        {
            emailEditText.setError("Lütfen Bir E-Posta Adresi Giriniz ");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Lütfen Geçerli Bir E-Posta Adresi Giriniz ");
            emailEditText.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(ForgetPassword.this,"Şifre Yenileme Linki E-Posta Adresinize Gönderildi",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgetPassword.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
