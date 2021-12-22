package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button btnAdd;
    EditText eItem;
    RecyclerView rvItem;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        eItem = findViewById(R.id.eItem);
        rvItem = findViewById(R.id.rvItem);


        loadItems();

       ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemAdapter.notifyItemRemoved(position);
                saveItems();
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            }
        };
        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemAdapter = new ItemAdapter(items, onLongClickListener, onClickListener);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String todoItem = eItem.getText().toString();
              items.add(todoItem);

              itemAdapter.notifyItemInserted(items.size() - 1);
              eItem.setText("");
              saveItems();
              Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
            }
        });
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
                String itemText = data.getStringExtra(KEY_ITEM_TEXT);
                int position = data.getExtras().getInt(KEY_ITEM_POSITION);
                items.set(position, itemText);
                itemAdapter.notifyItemChanged(position);
                saveItems();
                Toast.makeText(getApplicationContext(), "Item updated", Toast.LENGTH_SHORT);
            } else {
                Log.w("MainActivity", "Unknown call to onActivityResult");
            }
        }

        private File getDataFile(){
            return new File(getFilesDir(), "data.txt");
        }

        private void loadItems(){
            try {
                items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading items", e);
                items = new ArrayList<>();
            }
        }

        private void saveItems(){
            try {
                FileUtils.writeLines(getDataFile(), items);
            } catch (IOException e) {
                Log.e("MainActivity", "Error writing items", e);
            }
        }

    }