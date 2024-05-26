package com.example.myapplication;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar7);
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
                actionBar.setTitle("Info");
            }
        }

        //Setting the app's theme
        //Fetching the stored data from the SharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("BrightnessPref", MODE_PRIVATE);
        int storedBrightness = sharedPreferences.getInt("brightness", MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(storedBrightness);
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
            //Moving to info screen
            case "About":
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
            //Moving to home screen
            case "Home":
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}