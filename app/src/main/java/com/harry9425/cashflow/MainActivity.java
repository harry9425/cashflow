package com.harry9425.cashflow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harry9425.cashflow.sort.sort;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    DatabaseReference databaseReference;
    entrymodel entrymodel;
    mainadapter mainadapter;
    ArrayList<entrymodel> list=new ArrayList<>();
    private FirebaseAuth mAuth;
    String curuser;
    Long incash=0l,outcash=0l;
    TextView incashtxt,outcashtxt,tocashtxt;
    Button withdraw,credit;
    EditText searchentry;
    String tosr="";
    public static int sort=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mainrecycler);
        mainadapter = new mainadapter(list, this);
        recyclerView.setAdapter(mainadapter);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser== null){
            Intent i =new Intent(MainActivity.this,loginpage.class);
            startActivity(i);
            finish();
        }
        else {
            curuser = mAuth.getCurrentUser().getUid().toString();
        }
        incashtxt=(TextView) findViewById(R.id.balanceinput);
        outcashtxt=(TextView) findViewById(R.id.balanceout);
        tocashtxt=(TextView) findViewById(R.id.balancetotal);
       // curuser = mAuth.getCurrentUser().getUid().toString();
        searchentry=(EditText) findViewById(R.id.searchentry);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        withdraw = (Button) findViewById(R.id.withdrawamountbtn);
        credit = (Button) findViewById(R.id.creditamountbtn);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdraw.setAlpha(0.5f);
                credit.setAlpha(1f);
                buildbox("out");
            }
        });
        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                credit.setAlpha(0.5f);
                withdraw.setAlpha(1f);
                buildbox("in");
            }
        });
        searchentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                tosr=searchentry.getText().toString().trim();
                filter(tosr);
            }
        });
        allentries();
    }

    private void filter(String tosr) {

        ArrayList<entrymodel> temp=new ArrayList<entrymodel>();
        for(entrymodel entry:list)
        {
            if(entry.getName().contains(tosr))
            {
                temp.add(entry);
            }
        }
        mainadapter.filteredlist(temp);
    }

    public void allentries()
    {
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("entries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                incash=0l;
                outcash=0l;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   entrymodel=snapshot.getValue(entrymodel.class);
                   if(entrymodel.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                       entrymodel.setUser("By you");
                   }
                   else {
                       entrymodel.setUser("By other");
                   }
                   if(entrymodel.getType().equals("in")){
                       incash+=entrymodel.getAmount();
                   }
                   else {
                       outcash+=entrymodel.getAmount();
                   }
                   list.add(entrymodel);
                }
                //Collections.reverse(list);
                incashtxt.setText(incash+"");
                outcashtxt.setText(outcash+"");
                tocashtxt.setText((incash-outcash)+"");
                mainadapter.notifyDataSetChanged();
                if(!list.isEmpty()) {
                    recyclerView.smoothScrollToPosition(list.size() - 1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
              //  curuser=currentUser.getUid().toString();
                if(currentUser== null){
                    Intent i =new Intent(MainActivity.this,loginpage.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser== null){
            Intent i =new Intent(MainActivity.this,loginpage.class);
            startActivity(i);
            finish();
        }
        else {
            curuser = mAuth.getCurrentUser().getUid().toString();
        }
    }

    private void buildbox(String type){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        final View view=getLayoutInflater().inflate(R.layout.addentrydialogbox,null);
        builder.setView(view);
        AlertDialog alertDialog =builder.create();
        EditText amount=view.findViewById(R.id.dialogamount);
        EditText name=view.findViewById(R.id.dialogname);
        Switch typo=view.findViewById(R.id.typeswitch);
        final String[] mode = new String[1];
        mode[0]="null";
        if(type.equals("in")){
            amount.setTextColor(Color.parseColor("#FF8BC34A"));
            amount.setHintTextColor(Color.parseColor("#FF8BC34A"));
        }
        else {
            amount.setTextColor(Color.parseColor("#FFE91E63"));
            amount.setHintTextColor(Color.parseColor("#FFE91E63"));
        }
        typo.setChecked(true);
        typo.setTextOn("CASH");
        typo.setTextOff("ONLINE");
        typo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mode[0]="cash";
                } else {
                    mode[0]="online";
                }
            }
        });
        Button save=view.findViewById(R.id.dialogsave);
        save.setOnClickListener(new View.OnClickListener() {
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
                    String random = databaseReference.push().getKey().toString();
                    entrymodel entrymodel = new entrymodel();
                    entrymodel.setAmount(Long.parseLong(val));
                    entrymodel.setName(des);
                    entrymodel.setId(random);
                    entrymodel.setMode(mode[0]);
                    entrymodel.setTime(System.currentTimeMillis());
                    entrymodel.setUser(curuser);
                    entrymodel.setType(type);
                    databaseReference.child("users").child(curuser).child("entries").child(random).setValue(entrymodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Entry updated", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            alertDialog.cancel();
                        }
                    });
                }
            }
        });
        Button saveandnew=view.findViewById(R.id.dialogsaveandnew);
        saveandnew.setOnClickListener(new View.OnClickListener() {
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
                    String random = databaseReference.push().getKey().toString();
                    entrymodel entrymodel = new entrymodel();
                    entrymodel.setAmount(Long.parseLong(val));
                    entrymodel.setName(des);
                    entrymodel.setId(random);
                    entrymodel.setMode(mode[0]);
                    entrymodel.setTime(System.currentTimeMillis());
                    entrymodel.setUser(curuser);
                    entrymodel.setType(type);
                    databaseReference.child("users").child(curuser).child("entries").child(random).setValue(entrymodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Entry updated", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            alertDialog.cancel();
                            buildbox(type);
                        }
                    });
                }
            }
        });
        alertDialog.show();
        credit.setAlpha(1f);
        withdraw.setAlpha(1f);

    }

    public void  buildsortbox(View view){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        final View view2=getLayoutInflater().inflate(R.layout.sortlistoptions,null);
        builder.setView(view2);
        AlertDialog alertDialog =builder.create();
        TextView sortbynamea=view2.findViewById(R.id.bynamea);
        sortbynamea.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=0;
             list.sort(new sort());
             mainadapter.notifyDataSetChanged();
            }
        });
        TextView sortbynamed=view2.findViewById(R.id.bynamed);
        sortbynamed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=1;
                list.sort(new sort());
                mainadapter.notifyDataSetChanged();
            }
        });
        TextView sortbyamounta=view2.findViewById(R.id.byamounta);
        sortbyamounta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=2;
                list.sort(new sort());
                mainadapter.notifyDataSetChanged();
            }
        });
        TextView sortbyamountd=view2.findViewById(R.id.byamountd);
        sortbyamountd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=3;
                list.sort(new sort());
                mainadapter.notifyDataSetChanged();
            }
        });
        TextView sortbytimea=view2.findViewById(R.id.bydatea);
        sortbytimea.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=4;
                list.sort(new sort());
                mainadapter.notifyDataSetChanged();
            }
        });
        TextView sortbytimed=view2.findViewById(R.id.bydated);
        sortbytimed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                sort=5;
                list.sort(new sort());
                mainadapter.notifyDataSetChanged();
            }
        });
        Button done=view2.findViewById(R.id.sortdonebtn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void signout(View view){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        final View view3=getLayoutInflater().inflate(R.layout.loggingoff,null);
        builder.setView(view3);
        AlertDialog alertDialog =builder.create();
        Button yes=view3.findViewById(R.id.yessignout);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                alertDialog.dismiss();
                alertDialog.cancel();;
            }
        });
        Button no=view3.findViewById(R.id.cancelsignout);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
}