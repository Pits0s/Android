package com.example.myapplication;



import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


public class Item {
    private String name;
    private int module;
    private float price;
    private int id;

    public Item(){}
    public Item(String name,int module,float price){
        this.name = name;
        this.price = price;
        this.module = module;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getID(){
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
