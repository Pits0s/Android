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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Shopping extends AppCompatActivity {
    TextView listName;
    TextView currentAmount;
    private DBHandler dbHandler;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //RecyclerView.Adapter<RecyclerAdapterShoppingList.ViewHolder> adapter;
    private RecyclerAdapterShoppingList adapter;
    private int listID;

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialising database api
        dbHandler = new DBHandler(this, null, null, 1);

        //Getting the current amount to pass it to the adapter
        currentAmount = findViewById(R.id.currentAmount);

        //Get Bundle from the Intent
        Bundle extras = getIntent().getExtras();
        //Checking if there are data passed in the intent
        if (extras != null) {
            //Retrieve data passed in the Intent
            listID = extras.getInt("listID");
        }
        //Naming the list
        listName = findViewById(R.id.listName);
        listName.setText(dbHandler.findList(listID).getName());

        recyclerView = findViewById(R.id.productList);
        //Setting a layout manager to the recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Setting an adapter to the recycleView
        adapter = new RecyclerAdapterShoppingList(this, getListID());
        adapter.bindCurrentAmount(currentAmount);
        recyclerView.setAdapter(adapter);

        //Checks if there are 0 items in the list and shows the appropriate msg
        if(adapter.getItemCount() == 0)
        {
            TextView noItemsText = findViewById(R.id.noItemsText);
            noItemsText.setVisibility(View.VISIBLE);
        }

        //Setting the toolbar
        Toolbar appBar = findViewById(R.id.toolbar1);
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
    public void transaction(View view) {
        String amountText = currentAmount.getText().toString();
        // Remove any currency symbols and parse the number
        try {
            double amount = Double.parseDouble(amountText.replace("$", ""));
            if (amount > 0) {
                Toast.makeText(getBaseContext(), "Your transaction has been made\nYou have been" +
                        " charged "+amount+ " $", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "No quantity confirmed!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "Invalid amount!", Toast.LENGTH_SHORT).show();
        }
    }
}

