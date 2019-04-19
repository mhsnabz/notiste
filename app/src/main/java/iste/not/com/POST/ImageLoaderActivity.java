package iste.not.com.POST;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import iste.not.com.Main.MainActivity;
import iste.not.com.R;


public class ImageLoaderActivity extends AppCompatActivity
{
    private static final String ALLOWED_CHARACTERS ="0123456789";
    private static final int izin = 1;
    ViewPagerAdapter adapter;
    ViewPager viewPager;
   ArrayList<String> images;
   String Images[];
    String key;
    ImageButton download;
    String  dirPath;
    String filename;

    File file,fileName;
    ProgressBar loading;
    String imageName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);
        checkPermisson();

        download=(ImageButton) findViewById(R.id.download);
        AndroidNetworking.initialize(getApplicationContext());
         dirPath = Environment.getExternalStorageDirectory().getPath() ;
       //   dirPath = android.os.Environment.getExternalStorageDirectory().toString() ;
          loading=(ProgressBar)findViewById(R.id.loading);
      filename = "notiste"+getRandomString(15)+".jpg";
        file= new File(dirPath,filename);

        images=getIntent().getStringArrayListExtra("list");
        int a = images.size();
        Images= new String[a];
        for (int i =0; i<images.size();i++)
        {
            Images[i]=images.get(i);
        }

        imageName="notiste"+getRandomString(15)+".jpg";
        key =getIntent().getStringExtra("key");

        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(ImageLoaderActivity.this,Images,ImageLoader.getInstance());
        viewPager.setAdapter(adapter);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    AndroidNetworking.download(Images[viewPager.getCurrentItem()],dirPath,"notiste"+getRandomString(15)+".jpg")
                            .build()

                            .startDownload(new DownloadListener() {@Override
                                public void onDownloadComplete()
                            {
                               // Bitmap bmImg = imageCacher.getBitmap(Images[viewPager.getCurrentItem()]);
                            /*
                                    if (!file.exists())
                                        file.mkdirs();
                                fileName = new File(file.getAbsolutePath() + "/" + imageName);
                                try {
                                    FileOutputStream out = new FileOutputStream(filename);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }*/
                                Toast.makeText(ImageLoaderActivity.this, "DownLoad Complete", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });
                }catch (Exception e)
                {
                    Log.d("download error", "download error: " + e.getLocalizedMessage());
                }


            }
        });
    }
    private void checkPermisson()
    {
        Log.d("permission", "checkPermisson: "+"asking users");
        String[] permissisons = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };
        if (ContextCompat.checkSelfPermission(ImageLoaderActivity.this,permissisons[0])==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(ImageLoaderActivity.this,permissisons[1])==PackageManager.PERMISSION_GRANTED
                        &&
                ContextCompat.checkSelfPermission(ImageLoaderActivity.this,permissisons[2])==PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(ImageLoaderActivity.this,permissisons,izin);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermisson();
    }

    private  String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d("randomStrign", "getRandomString: "+sb.toString());

        return sb.toString();
    }
}
