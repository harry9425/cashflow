package com.harry9425.cashflow.sort;

import com.harry9425.cashflow.MainActivity;
import com.harry9425.cashflow.entrymodel;

import java.util.Comparator;

public class sort implements Comparator<com.harry9425.cashflow.entrymodel> {

    @Override
    public int compare(entrymodel entrymodel, entrymodel t1) {
        if(MainActivity.sort==0) {
            return entrymodel.getName().compareTo(t1.getName());
        }
        else if(MainActivity.sort==1) {
            return t1.getName().compareTo(entrymodel.getName());
        }
        else if(MainActivity.sort==2){
            return entrymodel.getAmount().compareTo(t1.getAmount());
        }
        else if(MainActivity.sort==3){
            return t1.getAmount().compareTo(entrymodel.getAmount());
        }
        else if(MainActivity.sort==4){
            return entrymodel.getTime().compareTo(t1.getTime());
        }
        else {
            return t1.getTime().compareTo(entrymodel.getTime());
        }
    }
}
