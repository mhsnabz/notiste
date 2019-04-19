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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Messages.OneToOneChatActivity;
import iste.not.com.R;
import iste.not.com.scientist.GetMyStudent.StudentSetGet;

public class MyStudentsActivity extends AppCompatActivity
{
   Toolbar toolbar;
   RecyclerView studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_students);
        SetToolbar();
        studentList=(RecyclerView)findViewById(R.id.studentList);
        studentList.setHasFixedSize(true);
        studentList.setLayoutManager(new GridLayoutManager(MyStudentsActivity.this,1));
        getMyAllStudents(getIntent().getStringExtra("key"),getIntent().getStringExtra("major"),getIntent().getStringExtra("currentUser"));
    }

    private void getMyAllStudents(String _key,String _major,String _currentUser )
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("Teacher")
                .child(_currentUser)
                .child("myLessons").child(_key).child("number");
        FirebaseRecyclerOptions<StudentSetGet> options = new FirebaseRecyclerOptions.Builder<StudentSetGet>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference,StudentSetGet.class)
                .build();
        FirebaseRecyclerAdapter<StudentSetGet,StudentListViewHolder > adapter = new FirebaseRecyclerAdapter<StudentSetGet, StudentListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StudentListViewHolder holder, int position, @NonNull StudentSetGet model)
            {
                    holder.getStudentClass(model.getStudentId());
                    holder.loadStudentInfo(model.getName(),getRef(position).getKey(),model.getStudentId(),model.getImage());
                    holder.itemView.findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(MyStudentsActivity.this, OneToOneChatActivity.class);
                            startActivity(intent);
                        }
                    });
            }

            @NonNull
            @Override
            public StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view;
                view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_students_single_viewholder,viewGroup,false);

                return new StudentListViewHolder(view);
            }
        };
        studentList.setAdapter(adapter);

    }
    class StudentListViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        public StudentListViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView=itemView;
        }

        public void loadStudentInfo(String _name,String _number ,String  _userId ,String _image)
        {
            TextView  name =(TextView)itemView.findViewById(R.id.student_name);
            name.setText(_name);
            final TextView  email =(TextView)itemView.findViewById(R.id.student_email);
            TextView  number =(TextView)itemView.findViewById(R.id.student_number);
            number.setText(_number);
            final ProgressBar bar =(ProgressBar)itemView.findViewById(R.id.progressBar) ;
            CircleImageView image =(CircleImageView)itemView.findViewById(R.id.profileImage);
            Picasso.get().load(_image).centerCrop().resize(256,256)
                    .placeholder(R.drawable.placeholder_image)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            bar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e)
                        {
                            bar.setVisibility(View.GONE);

                        }
                    });
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(_userId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("email").exists())
                    {
                        email.setText(dataSnapshot.child("email").getValue().toString());
                    }
                    else email.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void getStudentClass(String _studentId)
        {
            final TextView sinif = (TextView)itemView.findViewById(R.id.sinif);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students")
                    .child(_studentId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    sinif.setText(dataSnapshot.child("Class").getValue().toString()+". Sınıf");
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
        actionBar.setTitle("Öğrencileriniz");
        // actionBar;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
