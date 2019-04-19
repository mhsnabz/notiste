package iste.not.com.scientist.Post_Exams_Result;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;
import iste.not.com.scientist.VizeFinalBut;

import static java.security.AccessController.getContext;

public class PostExamsResultsActivity extends AppCompatActivity {
 Toolbar toolbar;
 String major,currentUser,lessonKey,lessonName,senderName,type;
 RecyclerView list;
 Map<String,Object> map;
 Map<String,String> studentResults;
 int sayac;
 Button sendResultButton;

 String userId[],not[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_exams_results);
        SetToolbar();
        sendResultButton=(Button)findViewById(R.id.post_click);
        major=getIntent().getStringExtra("major");
        currentUser=getIntent().getStringExtra("currentUser");
        lessonKey=getIntent().getStringExtra("lessonKey");
        type=getIntent().getStringExtra("type");
        sayac=getIntent().getIntExtra("sayac",sayac);
        not= new String[getIntent().getIntExtra("sayac",sayac)];
        userId= new String[getIntent().getIntExtra("sayac",sayac)];

        list=(RecyclerView)findViewById(R.id.list);
        map= new HashMap<>();
        studentResults= new HashMap<>();
        list.setHasFixedSize(true);
        list.setLayoutManager(new GridLayoutManager(PostExamsResultsActivity.this,1));
        list.addItemDecoration(new DividerItemDecoration(PostExamsResultsActivity.this, DividerItemDecoration.VERTICAL));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(major)
                .child("Lessons")
                .child(lessonKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonName=dataSnapshot.child("lessonName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        setStudentResults(major,currentUser,lessonKey,getIntent().getStringExtra("teacherName"),getIntent().getStringExtra("type"));
            sendResultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendNotification(userId,not,
                            lessonName,
                            getIntent().getStringExtra("type"),
                            currentUser,
                            getIntent().getStringExtra("teacherName"));
                }
            });

    }

    private void setStudentResults(String _major, final String _currentUser, final String _lessonKey, final String _teacherName, final String _type )
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(_major)
                .child("Teacher")
                .child(_currentUser)
                .child("myLessons")
                .child(_lessonKey)
                .child("ExamsResults");
        FirebaseRecyclerOptions<VizeFinalBut> options = new FirebaseRecyclerOptions.Builder<VizeFinalBut>()
                .setQuery(databaseReference,VizeFinalBut.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<VizeFinalBut,ViewHolder> adapter = new FirebaseRecyclerAdapter<VizeFinalBut, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull VizeFinalBut model)
            {
                holder.setResult(model.getVize(),model.getFinall(),model.getButunleme());
                holder.setProfile(getRef(position).getKey());
                if (type.equals("vize")){
                    userId[position]=getRef(position).getKey();
                    not[position]=model.getVize();
                }
                if (type.equals("finall"))
                {
                    userId[position]=getRef(position).getKey();
                    not[position]=model.getFinall();
                }
                if (type.equals("butunleme"))
                {
                    userId[position]=getRef(position).getKey();
                    not[position]=model.getButunleme();
                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_exams_result_viewholder,viewGroup,false);

                return new ViewHolder(view);
            }
        };

        sayac=adapter.getItemCount();
        list.setAdapter(adapter);
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
        actionBar.setTitle("Sınav Sonuçlarını Paylaş");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void sendNotification(String[] _userId,String[] _not,String _lessonName, String _type,String _teacherId,String _teacherName)
    {
        Map<String,Object> map ;
        for (int i=0;i<_userId.length;i++)
        {
            if (_userId[i]!=null)
            {
                map = new HashMap<>();
                map.put("send", _teacherId);
                map.put("title",_teacherName );
                map.put("name",_lessonName + " Sınav Sonuçları ->");
                if (_type.equals("vize"))
                    map.put("type", "Vize = " + _not[i]);
                if (_type.equals("finall"))
                    map.put("type", "Final = " + _not[i]);
                if (_type.equals("butunleme"))
                    map.put("type", "Bütünleme = " + _not[i]);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification")
                        .child(_userId[i]);
                databaseReference.push().setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("result Notification", "onComplete: " + "task isSuccessful");
                                } else
                                    Log.w("result Notification", "onComplete: ", task.getException());
                            }
                        });
            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            rootView=itemView;
        }

        public void setProfile(String _key)
        {
            final CircleImageView profile = (CircleImageView)itemView.findViewById(R.id.profilePic);
            final ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progressBar);
            final TextView name = (TextView)itemView.findViewById(R.id.name);
            final TextView number =(TextView)itemView.findViewById(R.id.number);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).resize(512,512)
                    .centerCrop().placeholder(R.drawable.placeholder_image)
                    .into(profile, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    name.setText(dataSnapshot.child("name").getValue().toString());
                    number.setText(dataSnapshot.child("number").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setResult(String _vize,String _final,String _butunleme)
        {
            TextView vize = (TextView)itemView.findViewById(R.id.vize);
            TextView fınal = (TextView)itemView.findViewById(R.id.finall);
            TextView butunleme = (TextView)itemView.findViewById(R.id.butunleme);
            if (_vize==null)vize.setText("Vize:"+"--");
          else  vize.setText("Vize:"+_vize);
          if (_final==null) fınal.setText("Final:"+"--");
          else fınal.setText("Final"+_final);
           if (_butunleme==null)butunleme.setText("Bütünleme:"+"--");
           else butunleme.setText("Bütünleme:"+_butunleme);

        }
    }
}
