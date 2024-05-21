package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
        recyclerAdapterListItems.bindDeleteButton(deleteButton);

        TextView totalPrice = findViewById(R.id.textViewTotalPrice);
        recyclerAdapterListItems.bindTotalPrice(totalPrice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapterListItems.setAdapter();
    }


}
