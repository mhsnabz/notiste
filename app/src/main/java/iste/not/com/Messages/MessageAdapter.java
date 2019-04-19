package iste.not.com.Messages;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECIEVED = 2;
    private List<Messages> mMessageList;
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    Context context;

    public MessageAdapter(List<Messages> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        this.context = context;
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(i==VIEW_TYPE_MESSAGE_RECIEVED)
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.received_message,viewGroup,false);
            return new MessageViewHolder(view);
        }
        else if(i==VIEW_TYPE_MESSAGE_SENT)
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.send_message,viewGroup,false);
            return new MessageViewHolder(view);

        }

        return null;
    }

    @Override
    public int getItemViewType(int position)
    {
        Messages messages = (Messages) mMessageList.get(position);
        String current_user_id = firebaseAuth.getCurrentUser().getUid();

        String from_user = messages.getFrom();
        if (from_user.equals(current_user_id)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            return VIEW_TYPE_MESSAGE_RECIEVED;
        }

    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
        String current_user_id =firebaseAuth.getCurrentUser().getUid();
        final Messages messages = mMessageList.get(i);
        String from_user =messages.getFrom();

        switch (messageViewHolder.getItemViewType())
        {
            case VIEW_TYPE_MESSAGE_RECIEVED:
                Query reference = FirebaseDatabase.getInstance().getReference().child("Students")
                        .child(from_user).orderByChild("time");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String image =dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image)
                                .resize(256,256)
                                .placeholder(R.drawable.placeholder_image)
                        .into(messageViewHolder.image);

                        String name =dataSnapshot.child("name").getValue().toString();
                        messageViewHolder.userName.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
                messageViewHolder._time.setText(convertime(messages.time));
                messageViewHolder.messageTex.setText(messages.getMessage());
                Linkify.addLinks(messageViewHolder.messageTex, Linkify.ALL);

                break;
            case VIEW_TYPE_MESSAGE_SENT:
                Query reference1 = FirebaseDatabase.getInstance().getReference().child("Students")
                        .child(current_user_id).orderByChild("time");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                         String image =dataSnapshot.child("image").getValue().toString();
                         String name =dataSnapshot.child("name").getValue().toString();
                        messageViewHolder.userName.setText(name);
                       Picasso.get().load(image)
                                .resize(256,256)
                               .placeholder(R.drawable.placeholder_image)
                                .into(messageViewHolder.image);
                       messageViewHolder.userName.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                messageViewHolder._time.setText(convertime(messages.time));
                messageViewHolder.messageTex.setText(messages.getMessage());
                Linkify.addLinks(messageViewHolder.messageTex, Linkify.ALL);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageTex,userName;
        TextView _time;
        CircleImageView image;
        ImageView seen,not_seen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTex=(TextView)itemView.findViewById(R.id.comment);
            image=(CircleImageView)itemView.findViewById(R.id.profilePic);
            userName=(TextView)itemView.findViewById(R.id.name);
            _time = (TextView)itemView.findViewById(R.id.time);

        }
    }
    public String convertime(Long milliseconds)
    {
        String dakika;
        int seconds = (int) ((milliseconds+10800000) / 1000) % 60 ;
        int minutes = (int) (((milliseconds+10800000)  / (1000*60)) % 60);
        int hours   = (int) (((milliseconds+10800000)  / (1000*60*60)) % 24);
        if (minutes<=9)
        {
            dakika = "0"+String.valueOf(minutes);
        }
        else
        {
            dakika = String.valueOf(minutes);
        }

        String saat = String.valueOf(hours);
        String saniye = String.valueOf(seconds);
        String time= saat+":"+dakika;
        return time;

    }
}
