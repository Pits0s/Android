package com.example.myapplication;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditList extends AppCompatActivity {
    private List list;

    private RecyclerAdapterListItems recyclerAdapterListItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_list);

        DBHandler handler = new DBHandler(getApplicationContext(),null,null,1);
        this.list = handler.findList(getIntent().getIntExtra("list",0));

        TextView listName = this.findViewById(R.id.editListName);
        listName.setText(list.getName());

        RecyclerView recyclerView = this.findViewById(R.id.listItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapterListItems = new RecyclerAdapterListItems(getApplicationContext(),list.getID());
        recyclerView.setAdapter(recyclerAdapterListItems);

        //Checks if there are 0 items and shows the appropriate msg
        if(recyclerAdapterListItems.getItemCount() == 0)
        {
            TextView noItemsText = findViewById(R.id.noItemsText);
            noItemsText.setVisibility(View.VISIBLE);
        }

        Button button = findViewById(R.id.listAddItem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddListItems.class);
                intent.putExtra("list_id",list.getID());
                startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.listDeleteItems);
        recyclerAdapterListItems.bindDeleteButton(deleteButton, findViewById(R.id.noItemsText));

        TextView totalPrice = findViewById(R.id.textViewTotalPrice);
        recyclerAdapterListItems.bindTotalPrice(totalPrice);

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar4);
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
                actionBar.setTitle(listName.getText().toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapterListItems.setAdapter();

        //Hides the no items message
        TextView noItemsText = findViewById(R.id.noItemsText);
        if(recyclerAdapterListItems.getItemCount() != 0)
        {
            noItemsText.setVisibility(View.INVISIBLE);
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
