package iste.not.com.Profile.ScientistPosted;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.Main.askNoteHelper.Notes;
import iste.not.com.POST.Datas;
import iste.not.com.R;

public class PostActivity extends AppCompatActivity {
    String teacherId;
    CircleImageView profilePic;
    TextView teacherName,priotiry,lessonName;
    RecyclerView postList;
    String major,lessonKey;

    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        lessonName=(TextView)findViewById(R.id.lessonName);
        profilePic=(CircleImageView)findViewById(R.id.profileimage) ;
        teacherName=(TextView)findViewById(R.id.name);
        priotiry=(TextView)findViewById(R.id.priotiry) ;
        teacherId=getIntent().getStringExtra("teacherId");
        setToolbar(profilePic,teacherName,priotiry);
        major=getIntent().getStringExtra("major");
        postList=(RecyclerView)findViewById(R.id.postList);
        postList.setHasFixedSize(true);
        postList.setLayoutManager(new GridLayoutManager(PostActivity.this,1));
        setPostList(getIntent().getStringExtra("major"),getIntent().getStringExtra("lessonKey"),teacherId);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("major")).child("Lessons")
                .child(getIntent().getStringExtra("lessonKey"));
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

    private void setPostList(final String _major, final String _lessonKey, final String _teacherId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("Teacher")
                .child(_teacherId)
                .child("myLessons")
                .child(_lessonKey)
                .child("Post");
        FirebaseRecyclerOptions<Notes> options = new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(databaseReference,Notes.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Notes,postListViewHolder> adapter= new FirebaseRecyclerAdapter<Notes, postListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull postListViewHolder holder, final int position, @NonNull final Notes model)
            {
                holder.setComment(model.getComment());
                holder.setTime( getTimeAgo(model.getTime()));
                holder.setCount(model.getCount());
                holder.itemView.findViewById(R.id.images).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(PostActivity.this,ScientistLessonsPostActivity.class);
                        intent.putExtra("teacherId",teacherId);
                        intent.putExtra("major",major);
                        intent.putExtra("lessonKey",getIntent().getStringExtra("lessonKey"));
                        intent.putExtra("count",model.getCount());
                        intent.putExtra("key",getRef(position).getKey());
                        intent.putExtra("user_id",model.getUserId().toString());
                        intent.putExtra("comment",model.getComment());


                        startActivity(intent);
                    }
                });
               // holder.setDatas((RecyclerView) holder.rootView.findViewById(R.id.datas),_major,_teacherId,_lessonKey,getRef(position).getKey());
            }

            @NonNull
            @Override
            public postListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View  view;
                view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_single_post,viewGroup,false);
                return new postListViewHolder(view);
            }
        };
        postList.setAdapter(adapter);
    }
    public class postListViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        public postListViewHolder(@NonNull View itemView)
        {
            super(itemView);
            rootView=itemView;
        }
        public void setCount(long count)
        {
            TextView _count = (TextView)itemView.findViewById(R.id.datasCount);
            _count.setText(String.valueOf(count)+" Tane Dosya");
        }
        public void setComment(String _comment)
        {
            TextView commnent =(TextView)itemView.findViewById(R.id.dersHakinda);
            commnent.setText(_comment);
        }
        public void setTime(String time)
        {
            TextView _time = (TextView)itemView.findViewById(R.id.time);
            _time.setText(time);
        }
        public void setDatas(RecyclerView datas, String _major, String _teacherId, String _lessonKey, String _key)
        {
            RecyclerView _datas = (RecyclerView)itemView.findViewById(R.id.datas);
            _datas.setHasFixedSize(true);
            _datas.setLayoutManager(new GridLayoutManager(getApplicationContext(),1,GridLayoutManager.HORIZONTAL,false));
           setData(_datas,_major,_teacherId,_lessonKey,_key);

        }
    }

    private  void setData(RecyclerView _datas, String _major, String _teacherId, String _lessonKey, String _key)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(_major)
                .child("Teacher")
                .child(_teacherId)
                .child("myLessons")
                .child(_lessonKey)
                .child("Post")
                .child(_key)
                .child("data");
        FirebaseRecyclerOptions<Datas> options = new FirebaseRecyclerOptions.Builder<Datas>()
                .setQuery(databaseReference,Datas.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<Datas,datasViewHolder> adapter = new FirebaseRecyclerAdapter<Datas, datasViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull datasViewHolder holder, int position, @NonNull Datas model) {
                holder.setDatas(model.getImage());
            }

            @NonNull
            @Override
            public datasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View view;
                view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_lesson_post_view_holder,viewGroup,false);

                return new datasViewHolder(view);
            }
        };
        _datas.setAdapter(adapter);
    }
    public class datasViewHolder extends RecyclerView.ViewHolder
    {   View datasView;
        public datasViewHolder(@NonNull View itemView) {
            super(itemView);
            datasView=itemView;
        }
        public void setDatas(String url)
        {
            final ProgressBar progressbar = (ProgressBar)itemView.findViewById(R.id.progressbar) ;
            ImageView view = (ImageView)itemView.findViewById(R.id.datas);
            Picasso.get().load(url).resize(150,150).centerCrop().placeholder(R.drawable.images).into(view, new Callback() {
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
    private void setToolbar(final CircleImageView pro_pic, final TextView _name, final TextView _priotiry)
    {
        final ProgressBar progressBar;
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ScientistEmailAdreses")
                .child(teacherId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                        .resize(512,512)
                        .centerCrop()
                        .placeholder(R.drawable.scientist)
                        .into(pro_pic, new Callback() {
                            @Override
                            public void onSuccess()
                            {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                _name.setText(dataSnapshot.child("unvan").getValue().toString()+dataSnapshot.child("name").getValue().toString());

                if(dataSnapshot.child("priority").getValue()!=null)
                { _priotiry.setText("#"+dataSnapshot.child("priority").getValue().toString());
                }
                else
                    _priotiry.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Şimdi";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "bir kaç dk önce";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " dk önce";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 saat önce";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " saat önce";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Dün";
        } else {
            return diff / DAY_MILLIS + " gün önce";
        }
    }
}
