package iste.not.com.scientist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Profile.ProfileActivity;
import iste.not.com.R;
import iste.not.com.scientist.menu.ScientistEditActivity;

public class ScientistProfileActivity extends AppCompatActivity
{  private static final int GALLERY=1;
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    String currentUser;
    CircleImageView profilePic,add_image;
    TextView name,contack,webSite;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    private StorageReference imageStorage;
    DatabaseReference imageDatabaseReference,imageDatabaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_profile);
        currentUser= firebaseUser.getUid();
        profilePic=(CircleImageView)findViewById(R.id.profilePic);
        add_image=(CircleImageView)findViewById(R.id.add_image);
        name=(TextView)findViewById(R.id.name);
        contack=(TextView)findViewById(R.id.contack);
        webSite=(TextView)findViewById(R.id.webSite);
        setProfile(currentUser);
        imageStorage= FirebaseStorage.getInstance().getReference();
        imageDatabaseReference=FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses").child(currentUser);
        imageDatabaseReference2=FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("major")).child("Teacher").child(currentUser);
    }

    private void setProfile(String _currentUser)
    {
        final ProgressBar progressBar =(ProgressBar)findViewById(R.id.progressBar);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(_currentUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("image").getValue().toString().equals("null"))
                    progressBar.setVisibility(View.GONE);

                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.scientist_black)
                        .resize(512,512)
                        .centerCrop()
                        .into(profilePic, new Callback() {
                            @Override
                            public void onSuccess()
                            {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e)
                            {
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString())

                                        .placeholder(R.drawable.scientist_black)
                                        .resize(512,512)
                                        .centerCrop()
                                        .into(profilePic, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.w("image_scientist", "onError: ",e.getCause() );
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        });

                name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
                contack.setText(firebaseUser.getEmail());
                if (dataSnapshot.child("webSite").getValue()==null)
                {
                    webSite.setVisibility(View.INVISIBLE);
                }
                else
                {
                    webSite.setText(dataSnapshot.child("webSite").getValue().toString());
                    Linkify.addLinks(webSite,Linkify.ALL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void editProfile(View view)
    {

    }

    public void settings(View view)
    {
    }

    public void addImage(View view)
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"RESİM SEÇ"),GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY&& resultCode==RESULT_OK )
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                progressDialog= new ProgressDialog(ScientistProfileActivity.this);
                progressDialog.setTitle("RESİM YÜKLENİYOR");

                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = imageStorage.child(currentUser).child("images").child(currentUser+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful()) {
                            imageStorage.child(currentUser).child("images").child(currentUser+".jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {
                                            final String dwonloadUri=uri.toString();
                                            imageDatabaseReference.child("image").setValue(dwonloadUri).addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        progressDialog.dismiss();
                                                        imageDatabaseReference2.child("image").setValue(dwonloadUri);
                                                    }
                                                }
                                            });
                                        }
                                    });
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progresSize = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();


                        String progressText =taskSnapshot.getBytesTransferred()/1024+"KB/"+taskSnapshot.getTotalByteCount()/1024+"KB";
                        progressDialog.setMessage("Lütfen Bekleyiniz " + (int)progresSize+"%");

                    }
                });
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error =result.getError();
            }
        }
    }

    public void back(View view)
    {
        finish();
    }

    public void goSetting(View view) {
    }

    public void goEdit(View view)
    {
        Intent intent = new Intent(ScientistProfileActivity.this, ScientistEditActivity.class);
        intent.putExtra("currentUser",currentUser);
        startActivity(intent);
    }
}
