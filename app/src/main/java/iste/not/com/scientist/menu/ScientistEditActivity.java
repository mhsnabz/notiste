package iste.not.com.scientist.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;
import iste.not.com.scientist.ScientistProfileActivity;

public class ScientistEditActivity extends AppCompatActivity
{
        Toolbar toolbar;
        ProgressBar progressBar;
        CircleImageView image;
        ImageButton delete,add;
        MaterialEditText name , unvan , website;
        Button save;
        ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_edit);
        SetToolbar();
        image=(CircleImageView)findViewById(R.id.image);
        delete=(ImageButton)findViewById(R.id.delete);
        add=(ImageButton)findViewById(R.id.add);
        name=(MaterialEditText)findViewById(R.id.name);
        unvan=(MaterialEditText)findViewById(R.id.unvan);
        website=(MaterialEditText)findViewById(R.id.webSite);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        save=(Button)findViewById(R.id.save);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                        .child(getIntent().getStringExtra("currentUser"));
                databaseReference.child("image").setValue("null").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            loadProfile(image,delete,add,name,unvan,website,getIntent().getStringExtra("currentUser"),progressBar);
                        add.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.GONE);
                    }
                });
            }
        });
        loadProfile(image,delete,add,name,unvan,website,getIntent().getStringExtra("currentUser"),progressBar);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveProfile(unvan,website,getIntent().getStringExtra("currentUser"));
            }
        });
    }

    private void saveProfile( final MaterialEditText unvan, final MaterialEditText website, final String currentUser)
    {
        progressDialog= new ProgressDialog(ScientistEditActivity.this);
        progressDialog.setTitle("Profinin Güncelleniyor");

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
       Map map = new HashMap();
       String _unvan = unvan.getText().toString();
       String _name =name.getText().toString();
       if (_unvan.isEmpty())
       {
           unvan.setError("Lütfen Ünvanınızı Giriniz");
           unvan.requestFocus();
           progressDialog.cancel();
           return;
       }
       if (_name.isEmpty())
       {
           name.setError("Lütfen Adınızı Giriniz");
           name.requestFocus();
           progressDialog.cancel();

           return;
       }
       map.put("name",_name);
       map.put("unvan",_unvan);
       if (website.getText().toString().isEmpty())
       {
           map.put("webSite","null");
       }
       else map.put("webSite",website.getText().toString());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(currentUser);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())

                {
                    progressDialog.cancel();
                    Toast.makeText(ScientistEditActivity.this,"Profiliniz Güncellendi",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void loadProfile(final CircleImageView image, final ImageButton delete, final ImageButton add, final MaterialEditText name, final MaterialEditText unvan, final MaterialEditText website, String currentUser, final ProgressBar progressBar)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(currentUser);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {

                    if (dataSnapshot.child("image").getValue().toString().equals("null"))
                    {
                        progressBar.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        add.setVisibility(View.VISIBLE);
                    }

                    Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                    .centerCrop().resize(512,512).placeholder(R.drawable.scientist_black)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e)
                        {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    unvan.setText(dataSnapshot.child("unvan").getValue().toString());
                    if (dataSnapshot.child("webSite").exists())
                    {
                        website.setText(dataSnapshot.child("webSite").getValue().toString());
                    }
                    else website.setText("Kayıtlı Web Site Yok");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
           DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                   .child(currentUser);
           databaseReference1.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot)
               {
                if (!dataSnapshot.child("image").getValue().toString().equals("null")||!dataSnapshot.child("image").exists()||dataSnapshot.child("image").getValue().toString()!=null)
                {
                    delete.setVisibility(View.VISIBLE);
                    add.setVisibility(View.GONE);
                }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

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
        actionBar.setTitle("Profilinizi Düzenleyin");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void save(View view)
    {

    }
}
