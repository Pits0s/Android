package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterChoiceList extends RecyclerView.Adapter<RecyclerAdapterChoiceList.ViewHolder>{
    private DBHandler dbHandler;

    public RecyclerAdapterChoiceList(Context context) {
        //Initialising database api
        this.dbHandler = new DBHandler(context,null,null,1);
    }

    //Holds the lists to be displayed
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemList;
        TextView productCount;
        public ViewHolder(View view) {
            super(view);

            itemList = view.findViewById(R.id.listName);
            productCount = view.findViewById(R.id.productCount);
            //Setting clickListener to the list
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Gets the list's ID from its position
                    int listID = getAbsoluteAdapterPosition();
                    //Creating intent to start the Shopping activity
                    Intent intent = new Intent(v.getContext(), Shopping.class);
                    //Passing the ID to the new activity
                    intent.putExtra("listID", listID + 1);
                    //Ask Android to start the new Activity
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    //Creates a viewHolder class and binds the list card layout to it
    @NonNull
    @Override
    public RecyclerAdapterChoiceList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_for_choosing, parent, false);
        return new ViewHolder(view);
    }

    //Binds all the lists from the database to the viewHolders
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterChoiceList.ViewHolder vHolder, int position) {

        List list = dbHandler.findList(position + 1);
        if(list != null)
        {
            vHolder.itemList.setText(list.getName());
            vHolder.productCount.setText(list.getItems().size() + " items");
        }
        else
        {
            //TODO : Display msg that no lists have been added
        }

    }

    //Returns the number of lists
    @Override
    public int getItemCount() {
        return dbHandler.getLists().size();
    }
}
