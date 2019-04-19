package iste.not.com.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Profile.helper.lessonsHelper;
import iste.not.com.R;

import static android.content.ContentValues.TAG;

public class scientistActivity extends AppCompatActivity {
    Toolbar toolbar;
    MaterialEditText search;
    RecyclerView lessons,teachers;
    String currentUser,major;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist);
        toolbar = (Toolbar) findViewById(R.id.MianToolBar);
        currentUser=getIntent().getStringExtra("currentUser");
        major=getIntent().getStringExtra("major");



        teachers=(RecyclerView)findViewById(R.id.teacher_list);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inLayoutInflater.inflate(R.layout.toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        actionBar.setTitle("Öğretim Görevlileri");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        teachers.setHasFixedSize(true);
        teachers.setLayoutManager(new GridLayoutManager(scientistActivity.this,1));


        setTeacher();



    }

    private void setTeacher()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUser).child("myLesson");
        FirebaseRecyclerOptions<lessonsHelper> options = new FirebaseRecyclerOptions.Builder<lessonsHelper>()
                .setQuery(databaseReference,lessonsHelper.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<lessonsHelper,ViewHolderTeacher> adapter = new FirebaseRecyclerAdapter<lessonsHelper, ViewHolderTeacher>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderTeacher holder, int position, @NonNull lessonsHelper model) {

                holder.loadProfile(model.getTeacherId(),model.getKey());

            }

            @NonNull
            @Override
            public ViewHolderTeacher onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_teacher_viewholder,viewGroup,false);
                return new ViewHolderTeacher(view);
            }
        };

        teachers.setAdapter(adapter);
    }
    public class ViewHolderTeacher extends RecyclerView.ViewHolder
    {
        View rootView;
        public ViewHolderTeacher(@NonNull View itemView) {
            super(itemView);
            rootView=itemView;
        }
        public void setFalow()
        {
            ImageButton fallowButton =(ImageButton)itemView.findViewById(R.id.fallowButton);
            ImageButton fallowedButton =(ImageButton)itemView.findViewById(R.id.fallowedButton);

            TextView fallow = (TextView)itemView.findViewById(R.id.fallow);
            fallowButton.setVisibility(View.GONE);
            fallowedButton.setVisibility(View.VISIBLE);
            fallow.setText("Takip Ediliyor");

        }
        public void setUnfallow()
        {
            ImageButton fallowButton =(ImageButton)itemView.findViewById(R.id.fallowButton);
            ImageButton fallowedButton =(ImageButton)itemView.findViewById(R.id.fallowedButton);

            TextView fallow = (TextView)itemView.findViewById(R.id.fallow);
            fallowButton.setVisibility(View.VISIBLE);
            fallowedButton.setVisibility(View.GONE);
            fallow.setText("Takip Et");

        }
        public void setFallowButton(String key, final String lessonKey, String major, final String UserID, final String studentNumber, final String current_user)
        {
            Log.d("key", "onClick: "+key+"lessonkey");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(major).child("Teacher").child(UserID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!dataSnapshot.child("myLessons").child(lessonKey).child("number").child(studentNumber).exists()
                            &&!dataSnapshot.child("myLessons").child(lessonKey).child("Uid").child(current_user).exists()
                    )
                    {
                        setUnfallow();

                    }
                    else
                    {
                        setFalow();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void loadProfile(String _teaherID,String _key)
        {
            final TextView lessonName =(TextView)itemView.findViewById(R.id.lessonName);
            final TextView name = (TextView)itemView.findViewById(R.id.name);
            final ProgressBar bar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            final CircleImageView profilePic = (CircleImageView)itemView.findViewById(R.id.profileImage);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                    .child(_teaherID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                            .resize(512,512)
                            .centerCrop()
                            .placeholder(R.drawable.scientist_black)
                            .into(profilePic, new Callback() {
                                @Override
                                public void onSuccess() {
                                    bar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                    name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(major)
                    .child("Lessons")
                    .child(_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        actionBar.setTitle("Öğretim Görevlileri");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
