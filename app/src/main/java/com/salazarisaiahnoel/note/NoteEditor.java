package com.salazarisaiahnoel.note;

import static com.salazarisaiahnoel.note.MainActivity.db;
import static com.salazarisaiahnoel.note.MainActivity.table;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.salazarisaiahnoel.customs.EasySQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        EasySQL es = new EasySQL(this);
        List<Map<String, String>> values = es.getTableValues(db, table);
        List<String> titles = new ArrayList<>();

        for (Map<String, String> a : values){
            for (Map.Entry<String, String> aa : a.entrySet()){
                if (aa.getKey().equals("title")){
                    titles.add(aa.getValue());
                }
            }
        }

        EditText editText = findViewById(R.id.note_editor_edit_text);

        try {
            editText.setText(getIntent().getStringExtra("content"));
        } catch (Exception ignored){
        }

        ImageView imageView = toolbar.findViewById(R.id.save_note);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titles.contains(getIntent().getStringExtra("title"))){
                    String where = es.whereClauseCreator("title", getIntent().getStringExtra("title"));
                    es.deleteFromTable(db, table, where);
                }
                Map<String, String> values = new HashMap<>();
                values.put("title", getIntent().getStringExtra("title"));
                values.put("content", editText.getText().toString());
                es.insertToTable(db, table, values);
                Toast.makeText(NoteEditor.this, "Note saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}