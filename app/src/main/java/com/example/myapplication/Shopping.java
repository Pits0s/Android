package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    }
}