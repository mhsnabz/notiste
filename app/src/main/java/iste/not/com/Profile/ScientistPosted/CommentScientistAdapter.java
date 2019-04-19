package iste.not.com.Profile.ScientistPosted;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.POST.CommentHelper.CommentHelper;
import iste.not.com.R;

public class CommentScientistAdapter extends RecyclerView.Adapter<CommentScientistAdapter.ViewHolder>
{

    public static final int VIEW_TYPE_COMMENT_SENT = 1;
    public static final int VIEW_TYPE_COMMENT_RECIEVED = 2;
    public List<CommentHelper> commentHelpers;
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    public String major;
    public String teacherId;
    public String key;
    public String lessonKey;

    public CommentScientistAdapter(List<CommentHelper> commentHelpers, String major, String teacherId, String key, String lessonKey) {
        this.commentHelpers = commentHelpers;
        this.major = major;
        this.teacherId = teacherId;
        this.key = key;
        this.lessonKey = lessonKey;
    }


    private String convertime(String Miliseconds)
    {
        long milliseconds= Long.parseLong(Miliseconds);
        int seconds = (int) (-milliseconds / 1000) % 60 ;
        int minutes = (int) ((-milliseconds / (1000*60)) % 60);
        int hours   = (int) ((-milliseconds / (1000*60*60)) % 24);
        String saat = String.valueOf(hours);
        String dakika = String.valueOf(minutes);
        String saniye = String.valueOf(seconds);
        String time= saat+":"+dakika;
        return  time;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(i==VIEW_TYPE_COMMENT_RECIEVED)
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.received_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==VIEW_TYPE_COMMENT_SENT)
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.send_comment,viewGroup,false);
            return new ViewHolder(view);

        }

        return null;
    }
    @Override
    public int getItemViewType(int position)
    {
        CommentHelper helper = (CommentHelper) commentHelpers.get(position) ;
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        String from_user = helper.getFrom();
        if (from_user.equals(currentUserID))
        {
            return VIEW_TYPE_COMMENT_SENT;
        }
        else
            return VIEW_TYPE_COMMENT_RECIEVED;

    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        String current_user_id =firebaseAuth.getCurrentUser().getUid();
        CommentHelper helper = commentHelpers.get(i);
        String from_user = helper.getFrom();
        switch (viewHolder.getItemViewType())
        {
            case VIEW_TYPE_COMMENT_RECIEVED:
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(major)
                        .child("Teacher")
                        .child(teacherId)
                        .child("myLessons")
                        .child(lessonKey)
                        .child("Uid")
                        .child(from_user);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String image= dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).centerCrop().resize(512,512)
                                .placeholder(R.drawable.placeholder_image)
                                .into(viewHolder.pic);
                        String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.username.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.time.setText(convertime(helper.getSendTime()));
                viewHolder.comment.setText(helper.getComment());
                break;

            case VIEW_TYPE_COMMENT_SENT:
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                        .child(major)
                        .child("Teacher")
                        .child(teacherId)
                        .child("myLessons")
                        .child(lessonKey)
                        .child("Uid")
                        .child(current_user_id);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String image= dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).centerCrop().resize(512,512)
                                .placeholder(R.drawable.placeholder_image)
                                .into(viewHolder.pic);
                        String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.username.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.time.setText(convertime(helper.getSendTime()));
                viewHolder.comment.setText(helper.getComment());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return commentHelpers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {TextView username,comment,time;
        CircleImageView pic;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            pic=(CircleImageView)itemView.findViewById(R.id.pic);
            username=(TextView)itemView.findViewById(R.id.name);
            comment=(TextView)itemView.findViewById(R.id.comment);
            time=(TextView)itemView.findViewById(R.id.time);
        }
    }
}

