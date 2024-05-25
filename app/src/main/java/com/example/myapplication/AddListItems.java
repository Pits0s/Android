package com.example.myapplication;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class AddListItems extends AppCompatActivity {
    private int list_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_items);
        DBHandler handler = new DBHandler(this,null,null,1);
        this.list_id = handler.findList(getIntent().getIntExtra("list_id",0)).getID();
        RecyclerView recyclerView = findViewById(R.id.addListItemsItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerAdapterAddListItems adapter = new RecyclerAdapterAddListItems(getApplicationContext(),list_id);
        recyclerView.setAdapter(adapter);

        //Setting the lists name
        TextView addListItemsItems = findViewById(R.id.addListItemsListName);
        addListItemsItems.setText(handler.findList(list_id).getName());

        Button addNewItem = findViewById(R.id.buttonAddNewItem);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.item_form, null);
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setPositiveButton("Edit Item",null)
                        .setNegativeButton("Close",null)
                        .show();
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
        });

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar3);
        if (appBar != null) {
            setSupportActionBar(appBar);
            //Setting the color of the overflow icon to white
            appBar.getOverflowIcon().setColorFilter(0, PorterDuff.Mode.SRC_ATOP);
            //Adding a back button to the app bar
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null)
            {
                //Customizing the back button
                actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button);
                //Showing the back button in the app bar
                actionBar.setDisplayHomeAsUpEnabled(true);
                //Setting the title of the app bar
                actionBar.setTitle("Items For " + handler.findList(list_id).getName());
            }
        }
    }


    //Binding the menu layout to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }
    //Getting input from menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(!item.isChecked());

        //Checking which menu item was clicked
        //IDs werent working so I used titles instead
        Intent intent;
        //The back button has no title
        if(item.getTitle() == null)
        {
            this.finish();
            return true;
        }
        switch (item.getTitle().toString())
        {
            //Moving to home screen
            case "Home":
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            //Moving to info screen
            case "Info":
                intent = new Intent(this,Info.class);
                startActivity(intent);
                return true;
            //Moving to about screen
            case "About":
                intent = new Intent(this,About.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
