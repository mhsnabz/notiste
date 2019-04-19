package iste.not.com.Profile.ExamsResults;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import iste.not.com.R;
import iste.not.com.scientist.VizeFinalBut;

public class ProfileExamsResultsActivity extends AppCompatActivity
{
    Toolbar toolbar;
    RecyclerView examsResults;
    FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_exams_results);
        SetToolbar();
        examsResults=(RecyclerView)findViewById(R.id.examsResults);
        examsResults.setLayoutManager(new GridLayoutManager(ProfileExamsResultsActivity.this,1));
        setExamsResultsRec(firebaseUser.getUid(),getIntent().getStringExtra("major"));
    }
    private void setExamsResultsRec(String _currentUser, final String _major)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students")
                .child(_currentUser)
                .child("myResults");
        FirebaseRecyclerOptions<VizeFinalBut> options = new FirebaseRecyclerOptions.Builder<VizeFinalBut>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference,VizeFinalBut.class).build();
        FirebaseRecyclerAdapter<VizeFinalBut,ExamsResultsViewHolder> adapter = new FirebaseRecyclerAdapter<VizeFinalBut, ExamsResultsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExamsResultsViewHolder holder, int position, @NonNull VizeFinalBut model)
            {
                holder.setLessonName(getRef(position).getKey(),_major);
                holder.vizeNot(model.getVize());
                holder.butunleme(model.getButunleme());
                holder.finalNot(model.getFinall());
            }

            @NonNull
            @Override
            public ExamsResultsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exams_result_viewholder,viewGroup,false);
                return new ExamsResultsViewHolder(view);
            }
        };
        examsResults.setAdapter(adapter);
    }
    public class ExamsResultsViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        public ExamsResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView=itemView;
        }
        public void vizeNot(String _vize)
        {
            TextView vize = (TextView)itemView.findViewById(R.id.vize);
            if (_vize!=null)
            {
                vize.setText("Vize : "+_vize);
            }
            else vize.setText("Vize : "+"--");
        }
        public void  finalNot(String _final)
        {
            TextView finall = (TextView)itemView.findViewById(R.id.finall);
            if (_final!=null)
            {
                finall.setText("Final : "+_final);
            }
            else finall.setText("Final : "+"--");
        }
        public void butunleme(String _butunleme)
        {
            TextView butunleme =(TextView)itemView.findViewById(R.id.butunleme);
            if (_butunleme!=null)
            {
              butunleme.setText("Bütünleme : "+_butunleme);
            }
            else
                butunleme.setText("Bütünleme : "+"--");
        }
        public void setLessonName(String _key,String _major)
        {
            final TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Lessons")
                    .child(_key);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    lessonName.setText(dataSnapshot.child("lessonName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        actionBar.setTitle("Sınav Sonuçları");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
