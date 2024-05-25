package com.example.myapplication;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChoiceList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter<RecyclerAdapterChoiceList.ViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.choice_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.listsForChoosing);
        //Setting a layout manager to the recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Setting an adapter to the recycleView
        adapter = new RecyclerAdapterChoiceList(this);
        recyclerView.setAdapter(adapter);

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar);
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
                actionBar.setTitle("Shopping");
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