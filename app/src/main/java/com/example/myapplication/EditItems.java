package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditItems extends AppCompatActivity {

    private RecyclerAdapterItems adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_items);
        RecyclerView recyclerView = findViewById(R.id.items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapterItems(getApplicationContext());
        adapter.bindDeleteButton(findViewById(R.id.buttonDeleteItems));
        recyclerView.setAdapter(adapter);
    }
    public void clickAddItem(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.item_form, null);

        EditText editTextName = layout.findViewById(R.id.editTextName);
        EditText editTextPrice = layout.findViewById(R.id.editTextPrice);
        CheckBox checkBoxKg = layout.findViewById(R.id.checkBoxKg);
        CheckBox checkBoxPiece = layout.findViewById(R.id.checkBoxPiece);


        checkBoxKg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    checkBoxPiece.setChecked(false);
                }
            }
        });

        checkBoxPiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    checkBoxKg.setChecked(false);
                }
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
        .setView(layout)
        .setPositiveButton("Add Item",null)
        .setNegativeButton("Close",null)
        .show();


        Button pButton =dialog.getButton(dialog.BUTTON_POSITIVE);

        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean close = true;
                if (editTextName.getText().toString().isEmpty()) {
                    close = false;
                }
                if (editTextPrice.getText().toString().isEmpty()) {
                    close = false;
                }
                if (!checkBoxPiece.isChecked() && !checkBoxKg.isChecked()) {
                    close = false;
                }
                int module = 0;
                if(close){
                    if (checkBoxKg.isChecked()){
                        module = 0;
                    }
                    if (checkBoxPiece.isChecked()){
                        module = 1;
                    }
                    Item item = new Item(editTextName.getText().toString(),module,Float.parseFloat(editTextPrice.getText().toString()));
                    DBHandler handler = new DBHandler(getApplicationContext(),null,null,1);
                    handler.addItem(item);
                    dialog.dismiss();
                    adapter.setAdapter();
                }
            }
        });

    }
}
