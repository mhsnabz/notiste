package iste.not.com.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import iste.not.com.Messages.MessageAdapter;
import iste.not.com.Messages.Messages;
import iste.not.com.R;

public class SendErrorActivity extends AppCompatActivity
{
    RecyclerView messagesRec;
    private final List<Messages> messages = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    ImageButton sendButton;
    MaterialEditText new_message_edittex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_error);
        sendButton=(ImageButton)findViewById(R.id.sendButton);
        new_message_edittex=(MaterialEditText)findViewById(R.id.new_message_edittex);
        messageAdapter = new MessageAdapter(messages,SendErrorActivity.this);
    }
}
