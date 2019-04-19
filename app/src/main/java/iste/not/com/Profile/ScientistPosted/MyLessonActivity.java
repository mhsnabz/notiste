package iste.not.com.Profile.ScientistPosted;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import iste.not.com.R;

public class MyLessonActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView lessonsList;
    String currentUser,major;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lesson);
        SetToolbar();
        currentUser=getIntent().getStringExtra("current_user");
        lessonsList=(RecyclerView)findViewById(R.id.lessons_list);
        lessonsList.setHasFixedSize(true);
        lessonsList.setLayoutManager(new GridLayoutManager(MyLessonActivity.this,1));

        major=getIntent().getStringExtra("major");
        lisMyLesson(currentUser,major);

    }



    private  void lisMyLesson(String _currentUser,final String _major)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_currentUser)
                .child("myLesson");
        FirebaseRecyclerOptions<helper> options= new FirebaseRecyclerOptions.Builder<helper>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference,helper.class)
                .build();

        FirebaseRecyclerAdapter<helper,myLessonViewHolder> adapter= new FirebaseRecyclerAdapter<helper, myLessonViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final myLessonViewHolder holder, int position, @NonNull final helper model)
            {
                 holder.setTeacherName(model.getTeacherId(),_major);
                 holder.setLessonName(_major,getRef(position).getKey());
                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v)
                     {
                        Intent intent = new Intent(MyLessonActivity.this,PostActivity.class);
                        intent.putExtra("lessonKey",model.getKey());
                        intent.putExtra("major",_major);
                        intent.putExtra("teacherId",model.getTeacherId());

                        startActivity(intent);
                     }
                 });

            }

            @NonNull
            @Override
            public myLessonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view;
              view=  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_lessons,viewGroup,false);
                return new myLessonViewHolder(view);
            }
        };
        lessonsList.setAdapter(adapter);
    }
    public class myLessonViewHolder extends  RecyclerView.ViewHolder
    {   View rootView;
        public myLessonViewHolder(@NonNull View itemView)
        {
            super(itemView);
            rootView=itemView;
        }
        public void setLessonName(String _major,String _key)
        {
            final TextView lessonName=(TextView)itemView.findViewById(R.id.lessonName);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Lessons")
                    .child(_key);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lessonName.setText(dataSnapshot.child("lessonName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void setTeacherName(String _userID,String _major)
        {
            final TextView teacherName = (TextView)itemView.findViewById(R.id.teacherName);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                    .child("Teacher").child(_userID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    teacherName.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
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
        actionBar.setTitle("Ders Seç");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
