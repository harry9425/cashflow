package com.harry9425.cashflow;

public class entrymodel {
    String name,type,user,id,mode;
    Long amount,time,balance;

    public entrymodel(String name, Long amount, String type, String user, Long time, String mode,Long balance) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.user = user;
        this.time = time;
        this.mode = mode;
        this.balance=balance;
    }

    public entrymodel(String id) {
        this.id = id;
    }
    public entrymodel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
