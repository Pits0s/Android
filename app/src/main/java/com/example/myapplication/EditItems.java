package com.example.myapplication;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar2);
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
                actionBar.setTitle("Items");
            }
        }

        //Setting the app's theme
        //Fetching the stored data from the SharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("BrightnessPref", MODE_PRIVATE);
        int storedBrightness = sharedPreferences.getInt("brightness", MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(storedBrightness);
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
            //Changing the app's theme
            case "Brightness":
                //Fetching the stored data from the SharedPreference
                SharedPreferences sharedPreferences = getSharedPreferences("BrightnessPref", MODE_PRIVATE);
                int storedBrightness = sharedPreferences.getInt("brightness", MODE_NIGHT_NO);

                //Creating a SharedPreference Editor to edit the value
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                //Setting and storing the new theme value
                if(getDefaultNightMode() == MODE_NIGHT_YES)
                {
                    myEdit.putInt("brightness", MODE_NIGHT_NO);
                    myEdit.apply();
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    return true;
                }
                else
                {
                    myEdit.putInt("brightness", MODE_NIGHT_YES);
                    myEdit.apply();
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    return true;
                }
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