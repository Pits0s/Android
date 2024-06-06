package com.example.myapplication;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterAddListItems extends RecyclerView.Adapter<RecyclerAdapterAddListItems.ViewHolder> {
    private DBHandler handler;
    private List list;
    private ArrayList<Item> items;
    private RecyclerView recyclerView;
    private ArrayList<Integer> selectedItems;
    public RecyclerAdapterAddListItems(Context context,int list_id){
        this.selectedItems = new ArrayList<>();
        this.handler = new DBHandler(context,null,null,1);
        this.list = handler.findList(list_id);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.list = handler.findList(this.list.getID());
        this.items = handler.getItems();
        ArrayList<Item> removeItems = new ArrayList<>();
        for(Item list_item:this.list.getItems().keySet()) {
            for(Item item:items){
                if(list_item.getID()==item.getID()){
                    removeItems.add(item);
                }
            }
        }
        this.items.removeAll(removeItems);
        this.recyclerView = recyclerView;
    }

    public void setAdapter() {
        if (this.recyclerView != null) {
            this.recyclerView.setAdapter(this);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private int item_id;
        public ViewHolder(View view){
            super(view);
            this.textViewName = view.findViewById(R.id.addListItemName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.item_quantity, null);
                    EditText itemQuantity = layout.findViewById(R.id.itemQuantity);

                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setView(layout)
                            .setPositiveButton("Add Item",null)
                            .setNegativeButton("Close",null)
                            .show();

                    Button pButton = dialog.getButton(dialog.BUTTON_POSITIVE);
                    pButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean close = true;
                            if(String.valueOf(itemQuantity.getText()).isEmpty()){
                                close = false;
                            }
                            if(close){
                                handler.addListItem(list.getID(), item_id,Float.parseFloat(String.valueOf(itemQuantity.getText())));
                                dialog.dismiss();
                                setAdapter();
                            }
                        }
                    });
                }
            });
        }
    }
    @NonNull
    @Override
    public RecyclerAdapterAddListItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_list_item_view,parent,false);
        return new RecyclerAdapterAddListItems.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterAddListItems.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.item_id = item.getID();
        holder.textViewName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
