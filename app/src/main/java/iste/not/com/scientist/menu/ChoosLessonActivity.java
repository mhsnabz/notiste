package iste.not.com.scientist.menu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import iste.not.com.FragmentDialogs.LessonsName;
import iste.not.com.R;

public class ChoosLessonActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView lessons;
    String major,current_user,name,key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choos_lesson);

        SetToolbar();
        major=getIntent().getStringExtra("major");
        current_user=getIntent().getStringExtra("currentUser");
        name=getIntent().getStringExtra("name");
        lessons=(RecyclerView)findViewById(R.id.lessons);
        lessons.setHasFixedSize(true);
        lessons.setLayoutManager(new GridLayoutManager(ChoosLessonActivity.this,1));
        setLessons(major,current_user);

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
        actionBar.setTitle("Dersi Seçiniz");
        // actionBar;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
 String lessonName ;
    private void setLessons(final String _major, final String _currentUser)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(major).child("Teacher").child(current_user)
                .child("myLessons");
        FirebaseRecyclerOptions<LessonsName> options = new FirebaseRecyclerOptions.Builder<LessonsName>()
                .setLifecycleOwner(this)
                .setQuery(reference,LessonsName.class).build();
        FirebaseRecyclerAdapter<LessonsName,ChoosLessonViewHolderLessons> adapter = new FirebaseRecyclerAdapter<LessonsName, ChoosLessonViewHolderLessons>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChoosLessonViewHolderLessons holder, final int position, @NonNull final LessonsName model)
            {
                Log.d("setLessons", "onBindViewHolder: "+getRef(position).getKey());
                holder.setLessonName(getRef(position).getKey(),_major);
                key=getRef(position).getKey();



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {


                      final Intent intent = new Intent(ChoosLessonActivity.this, ExamsResultActivity.class);
                        intent.putExtra("lessonName",lessonName);
                        //  intent.putExtra("lessonName",model.getLessonName());
                        intent.putExtra("image",getIntent().getStringExtra("image"));
                        intent.putExtra("currentUser",getIntent().getStringExtra("currentUser"));
                        intent.putExtra("major",_major);
                        intent.putExtra("lessonKey",getRef(position).getKey());
                        intent.putExtra("query","Public_POST");
                        intent.putExtra("name",name);



                        startActivity(intent);


                    }
                });
            }

            @NonNull
            @Override
            public ChoosLessonViewHolderLessons onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_lessons_layout,viewGroup,false);


                return new ChoosLessonViewHolderLessons(view);
            }
        };

        lessons.setAdapter(adapter);
    }

    private void setLessonName(String _key)
    {

    }
    class ChoosLessonViewHolderLessons extends  RecyclerView.ViewHolder
    {
        View rootView;

        public ChoosLessonViewHolderLessons(@NonNull View itemView) {
            super(itemView);
            rootView=itemView;
        }

        public void setLessonName(String _key,String _major)
        {
            final TextView name = (TextView)itemView.findViewById(R.id.TV_LessonName);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(major)
                    .child("Lessons")
                    .child(_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    name.setText(dataSnapshot.child("lessonName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
