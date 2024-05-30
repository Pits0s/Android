package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAdapterListItems extends RecyclerView.Adapter<RecyclerAdapterListItems.ViewHolder> {

    private RecyclerView recyclerView;
    private DBHandler handler;
    private List list;
    private ArrayList<Item> items;
    private ArrayList<Item> selectedItems;
    private Context context;
    private Button deleteButton;
    private TextView totalPrice;
    public RecyclerAdapterListItems(Context context, int list_id){
        this.context = context;
        this.handler = new DBHandler(context,null,null,1);
        this.list = handler.findList(list_id);
        this.selectedItems = new ArrayList<>();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView listItemName;
        private TextView listItemQuantity;
        private TextView listItemPrice;
        private CheckBox listItemCheckBox;
        private int quantity;
        public ViewHolder(View view){
            super(view);
            this.listItemName = view.findViewById(R.id.listItemName);
            this.listItemQuantity = view.findViewById(R.id.listItemQuantity);
            this.listItemPrice = view.findViewById(R.id.listItemPrice);
            this.listItemCheckBox = view.findViewById(R.id.listItemCheckBox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = layoutInflater.inflate(R.layout.item_quantity,null);
                    EditText editText = layout.findViewById(R.id.itemQuantity);
                    editText.setText(listItemQuantity.getText());
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setView(layout)
                            .setPositiveButton("Save",null)
                            .setNegativeButton("Close",null)
                            .show();
                    Button pButton = dialog.getButton(dialog.BUTTON_POSITIVE);
                    pButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!editText.getText().toString().isEmpty()){
                                handler.editListItem(list.getID(),items.get(getAbsoluteAdapterPosition()).getID(),Integer.parseInt(String.valueOf(editText.getText())));
                                setAdapter();
                                dialog.dismiss();
                            }
                        }
                    });
                }


            });
        }
    }
    @NonNull
    @Override
    public RecyclerAdapterListItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new RecyclerAdapterListItems.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterListItems.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.listItemName.setText(item.getName());
        holder.listItemQuantity.setText(String.valueOf(list.getItems().get(item)));
        holder.quantity = list.getItems().get(item);
        String module = "";
        if(item.getModule()==0){
            module="$/kg";
        }else{
            module="$/piece";
        }
        holder.listItemPrice.setText(item.getPrice()*holder.quantity+"("+item.getPrice()+module+")");

        holder.listItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedItems.add(item);
                }else{
                    selectedItems.remove(item);
                }
                refreshDeleteButton();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.list = handler.findList(list.getID());
        this.items = new ArrayList<>(list.getItems().keySet());
        this.recyclerView = recyclerView;

        float totalPrice = 0;
        for(Item item:list.getItems().keySet()){
            totalPrice += item.getPrice() * list.getItems().get(item);
        }
        if(this.totalPrice!=null) {
            this.totalPrice.setText(totalPrice + "$");
        }

        if(this.deleteButton!=null) {
            this.deleteButton.setEnabled(!this.selectedItems.isEmpty());
        }
    }

    public void bindTotalPrice(TextView totalPrice){
        this.totalPrice = totalPrice;
    }
    public void bindDeleteButton(Button button, TextView noItemsText){
        this.deleteButton = button;
        refreshDeleteButton();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Item item: selectedItems){
                    handler.removeListItem(list.getID(),item.getID());
                }
                selectedItems = new ArrayList<>();
                refreshDeleteButton();

                setAdapter();

                //Checks if there are 0 items and shows the appropriate msg
                if(list.getItems().isEmpty())
                {
                    noItemsText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void refreshDeleteButton(){
        if(this.deleteButton!=null) {
            this.deleteButton.setEnabled(!this.selectedItems.isEmpty());
            this.deleteButton.setAlpha((this.selectedItems.isEmpty() ? 0.3f : 1));
        }
    }
    public void setAdapter(){
        if(recyclerView!=null){
            this.recyclerView.setAdapter(this);
        }
    }
}
