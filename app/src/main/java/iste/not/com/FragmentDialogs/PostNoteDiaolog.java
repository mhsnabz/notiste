package iste.not.com.FragmentDialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import iste.not.com.R;

import static android.content.ContentValues.TAG;

public class PostNoteDiaolog extends DialogFragment
{
    private MaterialEditText lessonSearch;
    private  View RootView;
    FirebaseUser uid;
    String Major;
    private String name;
    private RecyclerView LessonsName;
    Query reference;
    RecyclerView.LayoutManager layoutManager;
    FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    String UserID = firebaseUser.getUid();
    RelativeLayout relativeLayout_edittext,relativeLayout_post;
    private TextView lessonNAME_textview,tv_name;
    private CircleImageView profilePicture;
    public PostNoteDiaolog()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        RootView = inflater.inflate(R.layout.post_note_dialog,container,false);
        //getMajor(UserID);
        lessonNAME_textview=(TextView)RootView.findViewById(R.id._tv_lessonName);
        relativeLayout_edittext=(RelativeLayout)RootView.findViewById(R.id.realLay_editText);
        relativeLayout_post=(RelativeLayout)RootView.findViewById(R.id.relLayout_post);
        lessonSearch=(MaterialEditText)RootView.findViewById(R.id.searchLesson_note);
        LessonsName =(RecyclerView)RootView.findViewById(R.id.recyclerView_LessonsName);
        LessonsName.setHasFixedSize(true);
        LessonsName.setLayoutManager(new LinearLayoutManager(getContext()));





        lessonSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                    name = lessonSearch.getText().toString();
                    Log.d(TAG, "onTextChanged: " + name);
                 //  GetLessons(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return RootView;
    }

  /*  private void GetLessons( String Name)
    {
        reference =  FirebaseDatabase.getInstance().getReference().child(Major).child("Lessons").orderByChild("name").startAt(Name).endAt(Name+"\uf8ff");
        FirebaseRecyclerOptions<LessonsName> options
                = new FirebaseRecyclerOptions.Builder<LessonsName>().setQuery(reference, iste.not.com.FragmentDialogs.LessonsName.class)
                .setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<iste.not.com.FragmentDialogs.LessonsName,LessonsViewHolder> adapter
        = new FirebaseRecyclerAdapter<iste.not.com.FragmentDialogs.LessonsName, LessonsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LessonsViewHolder holder, final int position, @NonNull final iste.not.com.FragmentDialogs.LessonsName model)
            {
                 holder.GetName(model.getName());

                 holder.view.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view)
                     {
                         String name  = getRef(position).getKey().toString();
                         Log.d(TAG, "onClick: "+name);
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Major).child("Lessons").child(name);
                         databaseReference.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                             {
                                String name = dataSnapshot.getValue().toString();
                                 lessonNAME_textview.setText(name);
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });
                         relativeLayout_edittext.setVisibility(View.INVISIBLE);
                         relativeLayout_post.setVisibility(View.VISIBLE);


                     }
                 });
            }

            @NonNull
            @Override
            public LessonsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new LessonsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_lessons_layout,viewGroup,false));
            }
        } ;

        LessonsName.setAdapter(adapter);
    }
    private void getMajor(final String keyX)
    {

        DatabaseReference MajorDb = FirebaseDatabase.getInstance().getReference().child("Students");
        MajorDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Major= dataSnapshot.child(keyX).child("major").getValue().toString();
                Log.d(TAG, "onDataChange: + Major");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static class LessonsViewHolder extends RecyclerView.ViewHolder
    {   View view;

            public LessonsViewHolder (View itemView)
            {
                super(itemView);
                view=itemView;
            }
        public void GetName (String _name)
        {
            TextView  Tv_LessonName =(TextView)view.findViewById(R.id.TV_LessonName);
            Tv_LessonName.setText(_name);
        }

    }*/
}
