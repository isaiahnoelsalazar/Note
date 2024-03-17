package com.salazarisaiahnoel.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.salazarisaiahnoel.customs.EasySQL;
import com.salazarisaiahnoel.customs.RoundedAlertDialog;
import com.salazarisaiahnoel.customs.SimpleList;
import com.salazarisaiahnoel.customs.interfaces.SimpleListOnItemClick;
import com.salazarisaiahnoel.customs.interfaces.SimpleListOnItemLongClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SimpleListOnItemClick, SimpleListOnItemLongClick {

    EasySQL es;
    RecyclerView rv;
    SimpleList simpleList;
    List<String> content;

    public static final String db = "notesdb";
    public static final String table = "notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Note");

        es = new EasySQL(this);
        rv = findViewById(R.id.rv);

        content = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh(){
        simpleList = new SimpleList(this, rv, new ArrayList<>(), this, this);
        simpleList.setItemPadding(24);

        if (!es.doesTableExist(db, table)){
            Map<String, String> columns = new HashMap<>();
            columns.put("title", "text");
            columns.put("content", "text");
            es.createTable(db, table, columns);
        }

        List<Map<String, String>> values = es.getTableValues(db, table);

        TextView hint = findViewById(R.id.hint);

        if (!values.isEmpty()){
            hint.setVisibility(View.GONE);
        } else {
            hint.setVisibility(View.VISIBLE);
        }

        for (Map<String, String> a : values){
            for (Map.Entry<String, String> aa : a.entrySet()){
                if (aa.getKey().equals("title")){
                    simpleList.addItem(aa.getValue());
                }
                if (aa.getKey().equals("content")){
                    content.add(aa.getValue());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note){
            View vv = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note, null);
            final EditText e = vv.findViewById(R.id.edit_text_add_note);
            RoundedAlertDialog rad = new RoundedAlertDialog(MainActivity.this);
            rad.create("Add note");

            rad.setupRightButton("Done");
            rad.setupLeftButton("Cancel");
            rad.setupRightButtonOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (simpleList.getData().contains(e.getText().toString())){
                        e.setError("Note already exists.");
                    } else {
                        rad.hide();
                        Intent i = new Intent(MainActivity.this, NoteEditor.class);
                        i.putExtra("title", e.getText().toString());
                        startActivity(i);
                    }
                }
            });
            rad.setupLeftButtonOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rad.hide();
                }
            });

            rad.addView(vv);

            rad.show();
        }
        if (item.getItemId() == R.id.clear_all_notes){
            RoundedAlertDialog rad = new RoundedAlertDialog(this);
            rad.create("Clear all notes?");

            rad.setupRightButton("Yes");
            rad.setupLeftButton("No");
            rad.setupRightButtonOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    es.deleteTable(db, table);
                    refresh();
                    rad.hide();
                }
            });
            rad.setupLeftButtonOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rad.hide();
                }
            });

            rad.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(int i) {
        Intent intent = new Intent(MainActivity.this, NoteEditor.class);
        intent.putExtra("title", simpleList.getData().get(i));
        intent.putExtra("content", content.get(i));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int i) {
        RoundedAlertDialog rad = new RoundedAlertDialog(this);
        rad.create("Delete note?");

        rad.setupRightButton("Yes");
        rad.setupLeftButton("No");
        rad.setupRightButtonOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String where = es.whereClauseCreator("title", simpleList.getData().get(i));
                es.deleteFromTable(db, table, where);
                refresh();
                rad.hide();
            }
        });
        rad.setupLeftButtonOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rad.hide();
            }
        });

        rad.show();
    }
}