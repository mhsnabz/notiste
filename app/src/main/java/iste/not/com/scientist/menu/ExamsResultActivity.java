package iste.not.com.scientist.menu;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Element;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import iste.not.com.DenemeVisionActivity;
import iste.not.com.Main.MainActivity;
import iste.not.com.R;
import iste.not.com.scientist.Post_Exams_Result.PostExamsResultsActivity;

import static android.content.ContentValues.TAG;

public class ExamsResultActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    private static final int izin = 1;

    private static final int camera_request =200;
    private static final int gallery_request =400;
    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;

    String cameraPermission[];
    String storagePermission[];
    int sayac1=0;
    int sayac2=0;
    Map<Integer,Object> map;
    Map<String,String> not;
    Map<String,String> number;
    Map<String,Object> results;
    String[] NUMBER;
    String [] NOT;
    ArrayMap<String,Object> numbers = new ArrayMap<>();
    ArrayMap<String,Object> nots = new ArrayMap<>();
    ImageView image;
    TextView resultText;
    SurfaceView cameraView;
    Toolbar toolbar;
    Uri image_uri;
    Button showResult,next;
    ImageButton backArrow;
    String major;
    View view;
    Spinner spinner;
    String lessonName;
    String teacherName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_result);

        setToolbar();
        lessonName=getIntent().getStringExtra("lessonName");
        teacherName=getIntent().getStringExtra("name");
        major=getIntent().getStringExtra("major");
        map= new HashMap<>();
        not= new HashMap<>();
        number= new HashMap<>();
        results= new HashMap<>();
        image =(ImageView)findViewById(R.id.resultImage) ;
        showResult=(Button)findViewById(R.id.showResult);
        next=(Button)findViewById(R.id.next);
        spinner=(Spinner)findViewById(R.id.spinner);
        resultText=(TextView)findViewById(R.id.resultText);
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        next.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        showResult.setVisibility(View.VISIBLE);
        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                next.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                showResult.setVisibility(View.INVISIBLE);
                resultText.setVisibility(View.VISIBLE);
                image.setVisibility(View.INVISIBLE);
                findViewById(R.id.back).setVisibility(View.VISIBLE);
            }
        });


        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for (int i =0;i<sayac1;i++)
                {
                    setExamsResult(getIntent().getStringExtra("major"),getIntent().getStringExtra("lessonKey"),getIntent().getStringExtra("currentUser"),
                            NUMBER[i],NOT[i],spinner.getSelectedItem().toString());
                }

                Intent intent = new Intent(ExamsResultActivity.this, PostExamsResultsActivity.class);
                //major,currentUser,lessonKey;
                    intent.putExtra("major",getIntent().getStringExtra("major"));
                    intent.putExtra("currentUser",getIntent().getStringExtra("currentUser"));
                    intent.putExtra("lessonKey",getIntent().getStringExtra("lessonKey"));
                    intent.putExtra("lessonName",getIntent().getStringExtra("lessonName"));
                    intent.putExtra("teacherName",getIntent().getStringExtra("name"));
                    intent.putExtra("type",spinner.getSelectedItem().toString());
                    intent.putExtra("sayac",sayac1);
                startActivity(intent);

            }
        });
        LoadSpinner(ExamsResultActivity.this,spinner);
    }

    private void LoadSpinner(ExamsResultActivity examsResultActivity, Spinner spinner)
    {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(ExamsResultActivity.this,R.array.typeExams,R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setToolbar()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.options_camare, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("Resim Seçiniz");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case camera_request:
                if (grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&writeStorageAccepted)
                        pickCamera();
                    else {
                        Toast.makeText(ExamsResultActivity.this,"İzin Verilmedi",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case gallery_request:
                if (grantResults.length>0)
                {

                    boolean writeStorageAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted)
                      pickGallery();
                    else {
                        Toast.makeText(ExamsResultActivity.this,"İzin Verilmedi",Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case camera_pick_request:
            case image_pick_request:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode==RESULT_OK)
        {
            if (requestCode==image_pick_request)
            {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode==camera_pick_request)
            {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
       if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
       {
           CropImage.ActivityResult result = CropImage.getActivityResult(data);
           if (resultCode ==RESULT_OK)
           {
               Uri resultUri = result.getUri();
               image.setImageURI(resultUri);
               BitmapDrawable bitmapDrawable =(BitmapDrawable)image.getDrawable();
               Bitmap bitmap = bitmapDrawable.getBitmap();
               TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
               if (!textRecognizer.isOperational())
               {
                   Toast.makeText(this,"HATA",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                   SparseArray<TextBlock> items = textRecognizer.detect(frame);
                   NUMBER= new String[50];
                   NOT=new String[50];
                   StringBuilder sb = new StringBuilder();


                   for (int i =0 ;i<items.size();i++)
                   {
                       TextBlock item = items.valueAt(i);
                       List<Line> lines = (List<Line>) item.getComponents();
                       for (Line line : lines) {
                           List<Element> elements = (List<Element>) line.getComponents();
                           for (Element element : elements) {
                               String word = element.getValue();
                               map.put(i,word);
                               if (word.length()==9) NUMBER[sayac1++]=word;// number.put(word,word);
                               if (word.length()==2||word.length()==1||word.length()==3)  NOT[sayac2++]=word;//not.put(word,word);

                           }
                       }
                       TextBlock myitem = items.valueAt(i);

                       sb.append(myitem.getValue());
                       sb.append("\n");
                   }


                   for (int i =0 ;i<NUMBER.length;i++)
                   {

                       Log.d("arrayMap", "post: "+NUMBER[i]);
                       Log.d("arrayMap", "post: "+NOT[i]);


                   }

                   resultText.setText(sb.toString());

                   showResult.setVisibility(View.VISIBLE);

               }
           }
           else if ( resultCode ==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
           {
               Exception exception =result.getError();
               Toast.makeText(this,""+exception,Toast.LENGTH_LONG).show();
           }
       }

    }
    public void clickGallery(View view)
    {
        if (!checkGalleryPermissions())
        {
            requestStoragePermission();
        }
        else
        {
            pickGallery();
        }


    }
    public void clickCamera(View view)
    {
        if (!checkCameraPermissions())
        {
            requestCameraPermission();
        }
        else
        {
            pickCamera();
        }
      //  checkPermisson();
        

    }
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,camera_request);
    }
    private boolean checkCameraPermissions() 
    {
        boolean result =ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 =ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }
    private void pickGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
      //  Intent intent = new Intent(Intent.ACTION_PICK);
       // intent.setType("*/*");
        startActivityForResult(intent,image_pick_request);
    }
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,gallery_request);

    }
    private boolean checkGalleryPermissions()
    {
        boolean result =ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void pickCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Resim Seçiniz ");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Resim->YAZI");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,camera_pick_request);

    }
    private void setStudentsResult(String _major, String _lessonKey, String _currentUser, final String number, final String note, final String type)
    {

    }
    private void setExamsResult(String _major, final String _lessonKey, String _currentUser, final String number, final String note, final String type)
    {
        final DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference().child(_major)
                .child("Teacher")
                .child(_currentUser)
                .child("myLessons")
                .child(_lessonKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("number").child(number).exists())
                {
                    if (type.equals("vize")) {
                        results.put("vize", note);

                        databaseReference.child("ExamsResults").child(dataSnapshot.child("number").child(number).child("studentId").getValue().toString())
                                .updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String studentID =dataSnapshot.child("number").child(number).child("studentId").getValue().toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students")
                                            .child(studentID)
                                            .child("myResults")
                                            .child(_lessonKey);
                                            reference.updateChildren(results);
                                }
                            }
                        });


                    }
                    else if (type.equals("finall"))
                    {

                        results.put("finall", note);

                        databaseReference.child("ExamsResults").child(dataSnapshot.child("number").child(number).child("studentId").getValue().toString())
                                .updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String studentID =dataSnapshot.child("number").child(number).child("studentId").getValue().toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students")
                                            .child(studentID)
                                            .child("myResults")
                                            .child(_lessonKey);
                                    reference.updateChildren(results);
                                }
                            }
                        });
                        ;

                    }
                    else if (type.equals("butunleme"))
                    {

                        results.put("butunleme", note);
                        databaseReference.child("ExamsResults").child(dataSnapshot.child("number").child(number).child("studentId").getValue().toString())
                                .updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String studentID =dataSnapshot.child("number").child(number).child("studentId").getValue().toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students")
                                            .child(studentID)
                                            .child("myResults")
                                            .child(_lessonKey);
                                    reference.updateChildren(results);
                                }
                            }
                        });
                        ;

                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void back(View view)
    {
        showResult.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        resultText.setVisibility(View.INVISIBLE);
        findViewById(R.id.back).setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(spinner.getSelectedItemPosition()<2)
        {
            Toast.makeText(ExamsResultActivity.this,"Lütfen Sınıfınızı Seçiniz !!!",Toast.LENGTH_LONG).show();
            return;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        if (spinner.getSelectedItemPosition()<0)
        {
            spinner.setSelection(1);
        }
    }
}
