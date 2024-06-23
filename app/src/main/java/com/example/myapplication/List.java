package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class List {
    private String name;
    private final HashMap<Item,Float> items;
    private int id;
    public List(){
        this.items = new HashMap<Item,Float>();
        this.name = name;
    }
    public List(String name){
        this.items = new HashMap<Item,Float>();
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setID(int id){
        this.id = id;
    }
    public int getID(){
        return this.id;
    }
    public void addItem(Item item,float quantity){
        this.items.put(item,quantity);
    }

    public void removeItem(Item item){
        this.items.remove(item);
    }

    public HashMap<Item,Float> getItems(){
        return this.items;
    }

    //Returns the quantity of an item in the list
    public float getQuantity(Item item){
        for(Item it : this.items.keySet())
        {
            if(it.getName().equals(item.getName()))
            {
                try
                {
                    return this.items.get(it);
                }
                catch (NullPointerException e)
                {
                    return -1;
                }
            }
        }
        return -1;
    }


}
