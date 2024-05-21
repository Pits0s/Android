package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditLists extends AppCompatActivity {

    private RecyclerAdapterLists adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_lists);
        recyclerView = findViewById(R.id.lists);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DBHandler handler = new DBHandler(this.getApplicationContext(),null,null,1);
        adapter = new RecyclerAdapterLists(getApplicationContext());
        adapter.bindDeleteButton(findViewById(R.id.buttonDeleteLists));
        recyclerView.setAdapter(adapter);
    }

    public void buttonAddList(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.list_form, null);
        EditText editTextListName = layout.findViewById(R.id.editTextListName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("Add List",null)
                .setNegativeButton("Close",null)
                .show();

        Button pButton = dialog.getButton(dialog.BUTTON_POSITIVE);
        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!String.valueOf(editTextListName.getText()).isEmpty()){
                    DBHandler handler = new DBHandler(getApplicationContext(),null,null,1);
                    handler.addList(new List(String.valueOf(editTextListName.getText())));
                }
                adapter.setAdapter();
                dialog.dismiss();
            }
        });
    }


}
