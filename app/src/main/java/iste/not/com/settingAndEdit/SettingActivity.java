package iste.not.com.settingAndEdit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;


import iste.not.com.Profile.ProfileActivity;
import iste.not.com.Profile.SendErrorActivity;
import iste.not.com.R;
import iste.not.com.SplashScreen;
import iste.not.com.settingAndEdit.settings.NotificaitonSettingsActivity;
import iste.not.com.settingAndEdit.settings.setting_about;
import iste.not.com.settingAndEdit.settings.setting_password;

public class SettingActivity extends AppCompatActivity
{

    ImageButton veriftyImage;
    Toolbar toolbar;
    Button logOut;
    TextView verfity,emailAdres,password,about;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SetToolbar();
        emailAdres=(TextView)findViewById(R.id.emailAdres);
        emailAdres.setText(firebaseUser.getEmail());
        veriftyImage=(ImageButton)findViewById(R.id.verfityImafge);
        verfity=(TextView)findViewById(R.id.verfity);
        about=(TextView)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, setting_about.class);
                startActivity(intent);
            }
        });
        verfity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SettingActivity.this,"E Posta Adresinize Doğrulama Linki Gönderildi",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        password=(TextView)findViewById(R.id.password);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SettingActivity.this, setting_password.class);
                startActivity(intent);
            }
        });
        if (firebaseUser.isEmailVerified())
        {
            verfity.setVisibility(View.INVISIBLE);
            veriftyImage.setVisibility(View.VISIBLE);
        }
        logOut=(Button)findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingActivity.this,SplashScreen.class);
                startActivity(intent);
                finish();

            }
        });
    }
    private  void SetToolbar()
    {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inLayoutInflater.inflate(R.layout.toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("Ayarlar");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



   /* public void onCustomThemeClick(View view)
    {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .setThemeResourceId(R.style.custom_theme)
                .setDividerColorId(R.color.colorAccent)
                .build()
                .show();
    }*/


    public void showLicensesDialog(View view) {
        new EasyLicensesDialogCompat(this)
                .setTitle("Licenses")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    public void showAboutDialog() {
        String msg = getResources().getString(R.string.about_text);
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogStyle).create();
        alertDialog.setTitle("About " + getResources().getString(R.string.library_name));
        alertDialog.setMessage(Html.fromHtml(msg));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"View on GitHub",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/marcoscgdev/EasyLicensesDialog")));
                    }
                });
        alertDialog.show();
        TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        messageView.setTextColor(Color.parseColor("#757575"));
        ((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setNotification(View view)
    {
        Intent intent = new Intent(SettingActivity.this, NotificaitonSettingsActivity.class);
        intent.putExtra("currentUser",firebaseUser.getUid());
        startActivity(intent);
    }

    public void sendError(View view)
    {
        Intent intent = new Intent(SettingActivity.this, SendErrorActivity.class);
        startActivity(intent);
    }
}
