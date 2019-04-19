package iste.not.com.DersEkleme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.BatchUpdateException;
import java.util.HashMap;

import iste.not.com.R;

public class DersEkleActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ders_ekle);

        button=(Button)findViewById(R.id.dersEkle);
        editText=(EditText)findViewById(R.id.dersAdi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ComputerEngineering")
                        .child("Lessons").push();
                String name = editText.getText().toString();
                HashMap<String,String> map = new HashMap<>();
                map.put("name",name);
                databaseReference.setValue(map);
            }
        });

    }
}
