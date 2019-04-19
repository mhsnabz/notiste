package iste.not.com.POST;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.R;
import uk.co.senab.photoview.PhotoView;



public class AllFilesActivity extends AppCompatActivity
{

    String image_save_path;
    Toolbar toolbar;
    RecyclerView allFiles;
    FirebaseStorage storage;
    String query,key,currentUser,major;

    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files);

        SetToolbar();



        image_save_path =  Environment.getExternalStorageDirectory() + "/" + "Downloads/NotIste"+ Calendar.getInstance().getTimeInMillis() +".jpg";
        allFiles=(RecyclerView)findViewById(R.id.allFiles);
        major=getIntent().getStringExtra("major");
        key=getIntent().getStringExtra("key");
        currentUser=getIntent().getStringExtra("currentUser");
        query=getIntent().getStringExtra("query");
        Log.d("AllFiles", "onCreate: "+query + " " + key + " " + major ) ;
        layoutManager= new GridLayoutManager(AllFilesActivity.this,2,GridLayoutManager.VERTICAL,false);
        allFiles.setLayoutManager(layoutManager);
        allFiles.setHasFixedSize(true);
        setFiles(major,query,currentUser,key);

    }

    private void setFiles(String major, String query, String currentUser, String key)
    {  final Query postReference= FirebaseDatabase.getInstance().getReference().child(major).child(query).child(key).child("data");
        FirebaseRecyclerOptions<Notes> options= new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(postReference,Notes.class)
                .setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<Notes,filesViewHolder> adapter = new FirebaseRecyclerAdapter<Notes, filesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final filesViewHolder holder, int position, @NonNull final Notes model)
            {


              holder.itemView.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Download(model.getImage(),  (ProgressBar)holder.itemView.findViewById(R.id.downloadProgress),(CircleImageView)holder.itemView.findViewById(R.id.done));
                    }
                });
               holder.setImage(model.getImage(),getRef(position).getKey().toString());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            if (getMimeType(model.getImage()).equals("application/pdf"))
                            {
                                Intent intent = new Intent(AllFilesActivity.this,PdfWebViewActivity.class);
                                intent.putExtra("pdf",model.getImage());
                                startActivity(intent);


                            }

                            else if (getMimeType(model.getImage()).equals("image/jpeg"))
                            {AlertDialog.Builder mBuilder = new AlertDialog.Builder(AllFilesActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                                PhotoView photoView = mView.findViewById(R.id.photo_view);
                                Picasso.get().load(model.getImage()).resize(512,512).into(photoView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                                mBuilder.setView(mView);
                                AlertDialog mDialog = mBuilder.create();
                                mDialog.show();

                            }
                            else if (getMimeType(model.getImage()).equals("image/png"))
                            {AlertDialog.Builder mBuilder = new AlertDialog.Builder(AllFilesActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                                PhotoView photoView = mView.findViewById(R.id.photo_view);
                                Picasso.get().load(model.getImage()).resize(512,512).into(photoView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                                mBuilder.setView(mView);
                                AlertDialog mDialog = mBuilder.create();
                                mDialog.show();

                            }
                       Toast.makeText(AllFilesActivity.this,getMimeType(model.getImage()),Toast.LENGTH_LONG).show();




                    }
                });
            }

            @NonNull
            @Override
            public filesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_files_viewholder,viewGroup,false);
                return new filesViewHolder(view);
            }
        };
        allFiles.setAdapter(adapter);
    }

    public class filesViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;

        ProgressBar progressBar;
        public filesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            rootView=itemView;

          //  progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);
        }


        public void setImage(String _image,String _text)
        {
            if (getMimeType(_image).equals("application/pdf"))
            {

                final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressBar) ;
                ImageView view = (ImageView)itemView.findViewById(R.id.files);
                view.setImageResource(R.drawable.pdf);
                progressbar.setVisibility(View.GONE);
            }
            else if (getMimeType(_image).equals("image/jpeg")){
                final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressBar) ;
                ImageView view = (ImageView)itemView.findViewById(R.id.files);
                Picasso.get().load(_image).resize(256,256).centerCrop().placeholder(R.drawable.images).into(view, new Callback() {
                    @Override
                    public void onSuccess()
                    {
                        progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        Log.e("error_bind", "onError: ",e.getCause() );
                    }
                });

            }
            else if (getMimeType(_image).equals("image/png")){
                final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressBar) ;
                ImageView view = (ImageView)itemView.findViewById(R.id.files);
                Picasso.get().load(_image).resize(256,256).centerCrop().placeholder(R.drawable.images).into(view, new Callback() {
                    @Override
                    public void onSuccess()
                    {
                        progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        Log.e("error_bind", "onError: ",e.getCause() );
                    }
                });

            }
            else
            {
                final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressBar) ;
                ImageView view = (ImageView)itemView.findViewById(R.id.files);
                Picasso.get().load(_image).resize(256,256).centerCrop().placeholder(R.drawable.images).into(view, new Callback() {
                    @Override
                    public void onSuccess()
                    {
                        progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        Log.e("error_bind", "onError: ",e.getCause() );
                    }
                });

            }
            }




    }
    public void Download(String url, ProgressBar pb_loading, CircleImageView done) {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                new DownloadFile(pb_loading,done).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            } else {
                new DownloadFile(pb_loading, done).execute(url);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private class DownloadFile extends AsyncTask<String, String, String> {
        ProgressBar pb_loading;
        CircleImageView done;
        public DownloadFile(ProgressBar pb_loading, CircleImageView done) {
            this.pb_loading=pb_loading;
            pb_loading.setVisibility(View.VISIBLE);
            this.done=done;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           pb_loading.setProgress(0);

        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                File rootsd = Environment.getExternalStorageDirectory();
                File mydir = new File(rootsd.toString() + "/NOTISTE");
                mydir.mkdir();
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int file_length = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(new File(mydir
                        + "NotIste"+ Calendar.getInstance().getTimeInMillis() +".jpg"));


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress(""+(int)((total*100)/file_length));
                    output.write(data, 0, count);
                }


                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

            pb_loading.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String file_url) {

           pb_loading.setProgress(100);
            Toast.makeText(getApplicationContext(),"file saved to "+ image_save_path,Toast.LENGTH_SHORT).show();
            pb_loading.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
        //    btn_view.setVisibility(View.VISIBLE);

        }

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
        actionBar.setTitle("Dosyalar");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
