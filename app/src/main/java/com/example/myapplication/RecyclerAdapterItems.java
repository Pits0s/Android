package com.example.myapplication;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterItems extends RecyclerView.Adapter<RecyclerAdapterItems.ViewHolder> {

    private RecyclerView recyclerView;
    private DBHandler handler;
    private ArrayList<Item> items;
    private Context context;
    private ArrayList<Integer> selected_items;
    private Button deleteButton;
    public RecyclerAdapterItems(Context context){
        this.selected_items = new ArrayList<>();
        this.context = context;
        this.handler = new DBHandler(context,null,null,1);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        this.items = handler.getItems();
    }
    public void setAdapter(){
        if(recyclerView!=null) {
            this.recyclerView.setAdapter(this);
        }
    }
    public void bindDeleteButton(Button button){
        this.deleteButton = button;
        refreshDeleteButton();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int item: getSelected_items()){
                    handler.removeItem(item);
                }
                selected_items = new ArrayList<>();
                refreshDeleteButton();
                setAdapter();
            }
        });

    }
    public void refreshDeleteButton(){
        if(this.deleteButton!=null) {
            this.deleteButton.setEnabled(!this.selected_items.isEmpty());
            this.deleteButton.setAlpha((this.selected_items.isEmpty() ? 0.3f : 1));
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewPrice;
        private CheckBox checkBox;
        private int id;
        public void setID(int id) {
            this.id = id;
        }
        public int getID(){
            return this.id;
        }
        public ViewHolder(View view){
            super(view);
            this.textViewName = view.findViewById(R.id.listItemName);
            this.textViewPrice = view.findViewById(R.id.listItemPrice);
            this.checkBox = view.findViewById(R.id.listItemCheckBox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = handler.findItem(getID());
                    LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
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
                    editTextName.setText(item.getName());
                    editTextPrice.setText(String.valueOf(item.getPrice()));

                    if(item.getModule() == 0){
                        checkBoxKg.setChecked(true);
                    }else{
                        checkBoxPiece.setChecked(true);
                    }

                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setView(layout)
                            .setPositiveButton("Edit Item",null)
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
                            if(close) {
                                Item item1 = new Item();
                                item1.setName(String.valueOf(editTextName.getText()));
                                item1.setPrice(Float.parseFloat(String.valueOf(editTextPrice.getText())));
                                if (checkBoxKg.isChecked()) {
                                    item1.setModule(0);
                                } else {
                                    item1.setModule(1);
                                }
                                item1.setID(item.getID());
                                handler.editItem(item1);
                                setAdapter();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
        }
    }
    public ArrayList<Integer> getSelected_items(){
        return this.selected_items;
    }

    @NonNull
    @Override
    public RecyclerAdapterItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterItems.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.setID(item.getID());
        String name = item.getName();
        String price = String.valueOf(item.getPrice());
        String price_text = price + "$/";
        if(item.getModule() == 0){
            price_text += "kg";
        }
        else{
            price_text += "piece";
        }
        holder.textViewName.setText(name);
        holder.textViewPrice.setText(price_text);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selected_items.add(item.getID());
                }else{
                    selected_items.removeIf(n -> n==item.getID());
                }
                refreshDeleteButton();
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
