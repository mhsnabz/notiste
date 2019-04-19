package iste.not.com.POST;

import android.net.Uri;
import android.sax.TextElementListener;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import iste.not.com.POST.PostNoteActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.List;

import iste.not.com.R;

public class UploadListAdapter extends  RecyclerView.Adapter<UploadListAdapter.ViewHolder>
{      FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String UserID = firebaseUser.getUid();
     StorageReference mStorage;
    DatabaseReference UserReference,UserPost;

    public List<String> fileNameList;
    public List<String> fileDoneList;
    public List<Double> fileProgres;
    public List<String> sizeLabel;
    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList){

        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
//        holder.fileNameView.setText(fileName);

      //  holder.labelData.setText(sizeLabel.get(position));
        String fileDone = fileDoneList.get(position);
        UserReference = FirebaseDatabase.getInstance().getReference();
        UserPost =FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        //  double progressPercent = fileProgres.get(position);
       // holder.labelPercent.setText((int)progressPercent+"%");
       // holder.progressBar.setProgress((int)progressPercent);
        if(fileDone.equals("uploading"))
        {
            holder.fileDoneView.setImageResource(R.mipmap.progress);


        }
        else {


            holder.fileDoneView.setImageResource(R.mipmap.checked);

        }

    }

    @Override
    public int getItemCount()
    {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView fileNameView,labelData,labelPercent;
        public ImageView fileDoneView;
        public ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            fileNameView = (TextView) mView.findViewById(R.id.upload_filename);
            fileDoneView = (ImageView) mView.findViewById(R.id.upload_loading);
            progressBar=(ProgressBar)mView.findViewById(R.id.progressBar);
            labelData=(TextView)mView.findViewById(R.id.labelData);
            labelPercent=(TextView)mView.findViewById(R.id.labelPercent);

        }

    }
}
