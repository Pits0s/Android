package com.example.myapplication;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productDB.db";
    public static final String TABLE_ITEMS = "ITEMS";
    public static final String ITEMS_COLUMN_ID = "item_id";
    public static final String ITEMS_COLUMN_ITEMNAME = "item_name";
    public static final String ITEMS_COLUMN_PRICE = "price";
    public static final String ITEMS_COLUMN_MODULE = "module";

    public static final String TABLE_LISTS = "LISTS";
    public static final String LISTS_COLUMN_ID = "list_id";
    public static final String LISTS_COLUMN_NAME = "list_name";

    public static final String TABLE_LIST_ITEM = "LIST_ITEM";
    public static final String LIST_ITEM_COLUMN_LIST_ID = "list_id";
    public static final String LIST_ITEM_COLUMN_ITEM_ID = "item_id";
    public static final String LIST_ITEM_COLUMN_ITEM_MODULE = "module";
    public static final String LIST_ITEM_COLUMN_ITEM_QUANTITY = "quantity";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_ITEMS +"("+
                ITEMS_COLUMN_ID + " INTEGER PRIMARY KEY,"+
                ITEMS_COLUMN_ITEMNAME + " STRING,"+
                ITEMS_COLUMN_PRICE + " DECIMAL,"+
                ITEMS_COLUMN_MODULE + " INTEGER)";

        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_LISTS_TABLE = "CREATE TABLE LISTS("+
                LISTS_COLUMN_ID + " INTEGER PRIMARY KEY,"+
                LISTS_COLUMN_NAME + " STRING)";
        db.execSQL(CREATE_LISTS_TABLE);

        String CREATE_LIST_ITEMS = "CREATE TABLE " +
                TABLE_LIST_ITEM + " (" +
                LIST_ITEM_COLUMN_LIST_ID + " INTEGER, " +
                LIST_ITEM_COLUMN_ITEM_ID + " INTEGER, " +
                LIST_ITEM_COLUMN_ITEM_QUANTITY + " DECIMAL, " +
                "FOREIGN KEY (" + LIST_ITEM_COLUMN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "("+ITEMS_COLUMN_ID+"))";
        db.execSQL(CREATE_LIST_ITEMS);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }
    public void addItem(Item item){
        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_ITEMNAME,item.getName());
        values.put(ITEMS_COLUMN_MODULE,item.getModule());
        values.put(ITEMS_COLUMN_PRICE,item.getPrice());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ITEMS,null,values);
        db.close();
    }
    public void addList(List list){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LISTS_COLUMN_NAME,list.getName());
        db.insert(TABLE_LISTS,null,values);

        for(Item item:list.getItems().keySet()){
            values = new ContentValues();
            values.put(LIST_ITEM_COLUMN_LIST_ID,list.getID());
            values.put(LIST_ITEM_COLUMN_ITEM_ID,item.getID());
            values.put(LIST_ITEM_COLUMN_ITEM_QUANTITY,Math.floor(list.getItems().get(item)*100)/100);
            db.insert(TABLE_LIST_ITEM,null,values);
        }
        db.close();
    }
    public Item findItem(int id){
        String query = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEMS_COLUMN_ID + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        Item item = new Item();
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            item.setID(cursor.getInt(0));
            item.setName(cursor.getString(1));
            item.setPrice(cursor.getFloat(2));
            item.setModule(cursor.getInt(3));
            cursor.close();
        } else{
            item = null;
        }
        db.close();
        return item;
    }
    public List findList(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List list = new List();
        Cursor cursor;
        String query = "SELECT * FROM " + TABLE_LISTS + " WHERE " + LISTS_COLUMN_ID + " = '" + id + "'";

        cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            list.setID(cursor.getInt(0));
            list.setName(cursor.getString(1));
        } else{
            list = null;
        }
        query = "SELECT * FROM " + TABLE_LIST_ITEM + " WHERE " + LIST_ITEM_COLUMN_LIST_ID + " = '" + id + "'";
        cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                list.addItem(this.findItem(cursor.getInt(1)), cursor.getFloat(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public void removeItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, ITEMS_COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.delete(TABLE_LIST_ITEM,LIST_ITEM_COLUMN_ITEM_ID + " = ?",new String[]{String.valueOf(id)});
        db.close();
    }
    public void removeList(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, LISTS_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void addListItem(int list_id,int item_id,float quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_ITEM_COLUMN_LIST_ID,list_id);
        values.put(LIST_ITEM_COLUMN_ITEM_ID,item_id);
        values.put(LIST_ITEM_COLUMN_ITEM_QUANTITY,quantity);
        db.insert(TABLE_LIST_ITEM,null,values);
        db.close();
    }

    public void removeListItem(int list_id,int item_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIST_ITEM,LIST_ITEM_COLUMN_LIST_ID + " = ? AND " + LIST_ITEM_COLUMN_ITEM_ID + " = ?",new String[]{String.valueOf(list_id),String.valueOf(item_id)});
        db.close();

    }
    public ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setID(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setPrice(cursor.getFloat(2));
                item.setModule(cursor.getInt(3));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    public void editItem(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_ITEMNAME,item.getName());
        values.put(ITEMS_COLUMN_PRICE,item.getPrice());
        values.put(ITEMS_COLUMN_MODULE,item.getModule());
        db.update(TABLE_ITEMS,values,ITEMS_COLUMN_ID + " = ?",new String[]{String.valueOf(item.getID())});
        db.close();
    }
    public ArrayList<List> getLists(){
        ArrayList<List> lists = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_LISTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                List list = new List();
                list.setID(Integer.parseInt(cursor.getString(0)));
                list.setName(String.valueOf(cursor.getString(1)));
                db = this.getReadableDatabase();
                String query1 = "SELECT * FROM " + TABLE_LIST_ITEM + " WHERE " + LIST_ITEM_COLUMN_LIST_ID + " = '" + list.getID() + "'";
                Cursor cursor1 = db.rawQuery(query1,null);
                if(cursor1.moveToFirst()){
                    do{
                        list.addItem(findItem(cursor1.getInt(1)),cursor1.getInt(2));
                    }while(cursor1.moveToNext());
                }
                cursor1.close();
                lists.add(list);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lists;
    }

    public void editListItem(int list_id,int item_id,float quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_ITEM_COLUMN_ITEM_QUANTITY,quantity);
        db.update(TABLE_LIST_ITEM,values,LIST_ITEM_COLUMN_LIST_ID + " = ? AND " + LIST_ITEM_COLUMN_ITEM_ID + " = ?",new String[]{String.valueOf(list_id),String.valueOf(item_id)});
        db.close();
    }
}
