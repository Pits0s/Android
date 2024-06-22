package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterShoppingList extends RecyclerView.Adapter<RecyclerAdapterShoppingList.ViewHolder> {
    private final DBHandler dbHandler;
    private final ArrayList<Item> allProducts;
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
                    float currentQuantity = Float.parseFloat(itemQuantity.getText().toString());

                    //Retrieving the product that will be bound
                    Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                    //Refreshing the price
                    if (product != null) {
                        itemName.setText(product.getName());

                        String module;
                        if (product.getModule() == 0) {
                            module = "$/kg";
                            currentQuantity += 0.5f;
                        } else {
                            module = "$/piece";
                            currentQuantity++;
                        }
                        //Rounding decimal
                        currentQuantity = (float) Math.round(currentQuantity*100) / 100;

                        itemQuantity.setText("" + currentQuantity);

                        //Rounding decimal
                        float overallPrice = product.getPrice() * currentQuantity;
                        overallPrice = (float) Math.round(overallPrice*100) / 100;
                        itemPrice.setText(overallPrice + "(" + product.getPrice() + module + ")");
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float currentQuantity = Float.parseFloat(itemQuantity.getText().toString());

                    if(currentQuantity > 0)
                    {
                        //Retrieving the product that will be bound
                        Item product = getAllProducts().get(getAbsoluteAdapterPosition());
                        //Refreshing the price
                        if (product != null) {
                            itemName.setText(product.getName());

                            String module;
                            if (product.getModule() == 0) {
                                module = "$/kg";
                                currentQuantity -= 0.5f;
                            } else {
                                module = "$/piece";
                                currentQuantity--;
                            }
                            //Rounding decimal
                            currentQuantity = (float) Math.round(currentQuantity*100) / 100;
                            //Making sure the quantity doesn't go below 0
                            if(currentQuantity < 0)
                            {
                                currentQuantity = 0;
                            }

                            itemQuantity.setText("" + currentQuantity);

                            //Rounding decimal
                            float overallPrice = product.getPrice() * currentQuantity;
                            overallPrice = (float) Math.round(overallPrice*100) / 100;
                            itemPrice.setText(overallPrice + "(" + product.getPrice() + module + ")");
                        }
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "The quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
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

                        //Getting the item's overall price
                        float overallItemPrice = product.getPrice() * Float.parseFloat((String) itemQuantity.getText());

                        //Adding to the current amount
                        float currentCost = Float.parseFloat(currentAmountString.substring(0, currentAmountString.length() - 1));
                        currentCost += overallItemPrice;
                        //Rounding it
                        currentCost = (float) Math.round(currentCost*100) / 100;
                        currentAmount.setText(currentCost + "$");

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

                        //Getting the item's overall price
                        float overallItemPrice = product.getPrice() * Float.parseFloat((String) itemQuantity.getText());

                        //Subtracting to the current amount
                        float currentCost = Float.parseFloat(currentAmountString.substring(0, currentAmountString.length() - 1));
                        currentCost -= overallItemPrice;
                        //Rounding it
                        currentCost = (float) Math.round(currentCost*100) / 100;
                        currentAmount.setText(currentCost + "$");

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

    //Binds all the items from the database to the viewHolders
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

            vHolder.itemQuantity.setText(Float.toString(list.getQuantity(product)));
        } else {
            Log.e("ShoppingList", "Sth went wrong with biding the items");
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