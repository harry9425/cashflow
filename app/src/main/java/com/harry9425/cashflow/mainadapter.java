package com.harry9425.cashflow;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class mainadapter extends RecyclerView.Adapter<mainadapter.viewholder>{

    ArrayList<entrymodel> list;
    Context context;
    int pos=-1;
    DatabaseReference databaseReference;

    public mainadapter(ArrayList<entrymodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.sampleentryshow,parent,false);
        return new viewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        final entrymodel detailsm=list.get(position);
        try {
            long seconds = detailsm.getTime();
            if (seconds != 0) {
                Date myDate = new Date(seconds);
                SimpleDateFormat formatter = new SimpleDateFormat("K:mm a");
                String myTime = formatter.format(myDate);
                holder.time.setText(myTime);
            }
        } catch (NumberFormatException e) {
            holder.time.setText("Error");
        }
        if(position==0){
            detailsm.setBalance(detailsm.getAmount());
        }
        else {
            final entrymodel prev=list.get(position-1);
            if(detailsm.getType().equals("in")){
                detailsm.setBalance(detailsm.getAmount()+prev.getBalance());
                holder.amount.setTextColor(Color.parseColor("#FF8BC34A"));
            }
            else{
                detailsm.setBalance(prev.getBalance()-detailsm.getAmount());
                holder.amount.setTextColor(Color.parseColor("#FFE91E63"));
            }
        }
        holder.name.setText(detailsm.getName());
        holder.name.setSelected(true);
        holder.amount.setText(detailsm.getAmount()+"");
        holder.mode.setText(detailsm.getMode());
        if(detailsm.getMode().equals("null")){
            holder.mode.setVisibility(View.GONE);
        }
        holder.user.setText(detailsm.getUser());
        holder.balance.setText("Balance:"+detailsm.getBalance());
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(holder.cardView.getAlpha()==1f) {
                    if(pos==-1){
                        holder.cardView.setAlpha(0.99f);
                        pos = position;
                        holder.selected.setVisibility(View.VISIBLE);
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#4A4A4A"));
                    }
                }
                else {
                    holder.cardView.setAlpha(1f);
                    pos=-1;
                    holder.selected.setVisibility(View.GONE);
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#191919"));
                }
                return false;
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showbox(detailsm);
            }
        });
        if(pos==-1){
            holder.cardView.setAlpha(1f);
            holder.selected.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#191919"));
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.keepSynced(true);
                databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("entries").child(detailsm.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                        pos=-1;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void filteredlist(ArrayList<entrymodel> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView name,amount,mode,balance,time,user;
        CardView cardView;
        ImageButton edit,delete;
        LinearLayout selected;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.entryname);
            amount=itemView.findViewById(R.id.entryamount);
            mode=itemView.findViewById(R.id.entrymode);
            balance=itemView.findViewById(R.id.entrybalance);
            time=itemView.findViewById(R.id.entrytime);
            user=itemView.findViewById(R.id.entryuser);
            cardView=itemView.findViewById(R.id.entrycardview);
            selected=itemView.findViewById(R.id.selectedblocksample);
            edit=itemView.findViewById(R.id.editentrysample);
            delete=itemView.findViewById(R.id.deleteentrysample);
        }
    }

    private void showbox(entrymodel entrymodel){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.addentrydialogbox, null);
        builder.setView(view);
        AlertDialog alertDialog =builder.create();
        EditText amount=view.findViewById(R.id.dialogamount);
        EditText name=view.findViewById(R.id.dialogname);
        LinearLayout savelinear=view.findViewById(R.id.newentrylinear);
        savelinear.setVisibility(View.GONE);
        LinearLayout updatelinear=view.findViewById(R.id.updateentrylinear);
        updatelinear.setVisibility(View.VISIBLE);
        Switch typo=view.findViewById(R.id.typeswitch);
        if(entrymodel.getMode().equals("cash")){
            typo.setChecked(false);
        }
        else {
            typo.setChecked(true);
        }
        typo.setTextOn("CASH");
        typo.setTextOff("ONLINE");
        typo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    entrymodel.setMode("cash");
                } else {
                    entrymodel.setMode("online");
                }
            }
        });
        if(entrymodel.getType().equals("in")){
            amount.setTextColor(Color.parseColor("#FF8BC34A"));
            amount.setHintTextColor(Color.parseColor("#FF8BC34A"));
        }
        else {
            amount.setTextColor(Color.parseColor("#FFE91E63"));
            amount.setHintTextColor(Color.parseColor("#FFE91E63"));
        }
        name.setText(entrymodel.getName());
        amount.setText(entrymodel.getAmount()+"");
        Switch cashinout=view.findViewById(R.id.cashinout);
        Button update=view.findViewById(R.id.dialogupdate);
        if(entrymodel.getType().equals("in")){
            cashinout.setChecked(false);
        }
        else {
            cashinout.setChecked(true);
        }
        cashinout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    entrymodel.setType("out");
                }
                else {
                    entrymodel.setType("in");
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = amount.getText().toString().trim();
                String des = name.getText().toString().trim();
                if (des.isEmpty()) {
                    des = "-";
                }
                if (val.isEmpty()) {
                    amount.setError("Empty");
                    alertDialog.dismiss();
                    alertDialog.cancel();
                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.keepSynced(true);
                    entrymodel.setAmount(Long.parseLong(val));
                    entrymodel.setName(des);
                    entrymodel.setTime(System.currentTimeMillis());
                    databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("entries").child(entrymodel.getId()).setValue(entrymodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Entry updated", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            alertDialog.cancel();
                            pos = -1;
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        alertDialog.show();
    }


}
