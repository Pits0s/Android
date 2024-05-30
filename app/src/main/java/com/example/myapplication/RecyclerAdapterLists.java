package com.example.myapplication;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class RecyclerAdapterLists extends RecyclerView.Adapter<RecyclerAdapterLists.ViewHolder> {
    private DBHandler handler;
    private ArrayList<Integer> lists;
    private ArrayList<Integer> listsToDelete;
    private RecyclerView recyclerView;
    private Button deleteButton;
    public RecyclerAdapterLists(Context context){
        this.listsToDelete = new ArrayList<>();
        this.handler = new DBHandler(context,null,null,1);
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        ArrayList<Integer> db_lists = new ArrayList<>();
        for(List list:handler.getLists()){
            db_lists.add(list.getID());
        }
        this.lists = db_lists;
    }
    public void setAdapter(){
        if(recyclerView!=null) {
            this.recyclerView.setAdapter(this);
        }
    }
    public void bindDeleteButton(Button button, TextView noListsText){
        deleteButton = button;
        refreshDeleteButton();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int list: getListsToDelete()){
                    handler.removeList(list);
                }
                listsToDelete = new ArrayList<>();
                refreshDeleteButton();

                //Checks if there are 0 lists and shows the appropriate msg
                if(handler.getLists().isEmpty())
                {
                    noListsText.setVisibility(View.VISIBLE);
                }

                setAdapter();
            }
        });
    }
    public void refreshDeleteButton(){
        if(this.deleteButton!=null) {
            this.deleteButton.setEnabled(!this.listsToDelete.isEmpty());
            this.deleteButton.setAlpha((this.listsToDelete.isEmpty() ? 0.3f : 1));
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private CheckBox checkBox;
        private int list_id;
        public ViewHolder(View view){
            super(view);
            this.textViewName = view.findViewById(R.id.listViewListName);
            this.checkBox = view.findViewById(R.id.checkBoxList);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),EditList.class);
                    intent.putExtra("list",getListID());
                    v.getContext().startActivity(intent);
                }
            });
        }
        public void setListID(int id){
            this.list_id = id;
        }
        public int getListID(){
            return this.list_id;
        }
    }
    public ArrayList<Integer> getListsToDelete(){
        return this.listsToDelete;
    }

    @NonNull
    @Override
    public RecyclerAdapterLists.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterLists.ViewHolder holder, int position) {
        List list = handler.findList(lists.get(position));
        holder.setListID(list.getID());
        String name = list.getName();
        holder.textViewName.setText(name);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    listsToDelete.add(list.getID());
                }else{
                    listsToDelete.removeIf(n -> n==list.getID());
                }
                refreshDeleteButton();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
