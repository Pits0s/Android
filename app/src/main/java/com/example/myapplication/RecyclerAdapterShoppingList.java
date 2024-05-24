package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerAdapterShoppingList extends RecyclerView.Adapter<RecyclerAdapterShoppingList.ViewHolder> {
    private DBHandler dbHandler;
    private ArrayList<Item> allProducts;
    private int listID;
    TextView currentAmount;

    public RecyclerAdapterShoppingList(Context context, int listID) {
        //Initialising database api
        this.dbHandler = new DBHandler(context, null, null, 1);
        //Retrieving all the products and putting them in an ArrayList
        allProducts = new ArrayList<>(dbHandler.findList(listID).getItems().keySet());
        this.listID = listID;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public ArrayList<Item> getAllProducts() {
        return allProducts;
    }

    //Holds the lists to be displayed
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemQuantity;
        ImageButton addButton;
        ImageButton removeButton;
        CheckBox tickButton;

        public ViewHolder(View view) {
            super(view);

            itemName = view.findViewById(R.id.itemName);
            itemPrice = view.findViewById(R.id.itemPrice);
            itemQuantity = view.findViewById(R.id.itemQuantity);

            addButton = view.findViewById(R.id.addButton);
            removeButton = view.findViewById(R.id.removeButton);
            tickButton = view.findViewById(R.id.tickButton);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Refreshing the quantity
                    int currentQuantity = Integer.parseInt(itemQuantity.getText().toString());
                    currentQuantity++;
                    itemQuantity.setText("" + currentQuantity);

                    //Retrieving the product that will be bound
                    Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                    //Refreshing the price
                    if (product != null) {
                        itemName.setText(product.getName());

                        String module;
                        if (product.getModule() == 0) {
                            module = "$/kg";
                        } else {
                            module = "$/piece";
                        }

                        itemPrice.setText(product.getPrice() * currentQuantity + "(" + product.getPrice() + module + ")");
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = Integer.parseInt(itemQuantity.getText().toString());
                    currentQuantity--;

                    if(currentQuantity >= 0)
                    {
                        itemQuantity.setText("" + currentQuantity);

                        //Retrieving the product that will be bound
                        Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                        //Refreshing the price
                        if (product != null) {
                            itemName.setText(product.getName());

                            String module;
                            if (product.getModule() == 0) {
                                module = "$/kg";
                            } else {
                                module = "$/piece";
                            }

                            itemPrice.setText(product.getPrice() * currentQuantity + "(" + product.getPrice() + module + ")");
                        }
                    }
                    else
                    {
                        //TODO : Throw a toast msg maybe
                    }
                }
            });

            tickButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //Making the list item transparent
                        view.setAlpha(0.5f);

                        //Retrieving the product that will be bound
                        Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                        //Retrieving the amassed amount
                        String currentAmountString = (String) currentAmount.getText();
                        //Adding to the current amount
                        float currentCost = Float.parseFloat(currentAmountString.substring(0, currentAmountString.length() - 1));
                        currentCost += product.getPrice() * Integer.parseInt((String) itemQuantity.getText());
                        currentAmount.setText(currentCost + "");

                        //Deactivating the add and remove buttons
                        addButton.setEnabled(false);
                        removeButton.setEnabled(false);
                    } else {
                        //Making the list item transparent
                        view.setAlpha(1);

                        //Retrieving the product that will be bound
                        Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                        //Retrieving the amassed amount
                        String currentAmountString = (String) currentAmount.getText();
                        //Adding to the current amount
                        float currentCost = Float.parseFloat(currentAmountString.substring(0, currentAmountString.length() - 1));
                        currentCost -= product.getPrice() * Integer.parseInt((String) itemQuantity.getText());
                        currentAmount.setText(currentCost + "");

                        //Activating the add and remove buttons
                        addButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                }
            });
        }
    }

    //Creates a viewHolder class and binds the list card layout to it
    @NonNull
    @Override
    public RecyclerAdapterShoppingList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_shopping, parent, false);
        return new RecyclerAdapterShoppingList.ViewHolder(view);
    }

    //Binds all the lists from the database to the viewHolders
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterShoppingList.ViewHolder vHolder, int position) {
        //Retrieving the list
        List list = dbHandler.findList(getListID());
        //Retrieving the product that will be bound
        Item product = getAllProducts().get(position);
        if (product != null && list != null) {
            vHolder.itemName.setText(product.getName());

            String module;
            if (product.getModule() == 0) {
                module = "$/kg";
            } else {
                module = "$/piece";
            }

            vHolder.itemPrice.setText(product.getPrice() * list.getQuantity(product) + "(" + product.getPrice() + module + ")");

            vHolder.itemQuantity.setText(Integer.toString(list.getQuantity(product)));
        } else {
            //TODO : Display msg that no items have been added
        }

    }

    //Returns the number of lists
    @Override
    public int getItemCount() {
        return dbHandler.findList(getListID()).getItems().size();
    }

    //Retrieving the currentAmount from the Shopping activity so that it can be refreshed
    public void bindCurrentAmount(TextView currentAmount) {
        this.currentAmount = currentAmount;
    }
}