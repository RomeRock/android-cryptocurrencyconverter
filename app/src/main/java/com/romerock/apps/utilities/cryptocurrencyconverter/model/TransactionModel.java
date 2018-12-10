package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebricko on 12/04/2018.
 */
public class TransactionModel {
    private String itemid;
    private String orderid;
    private String paid;
    private String transtoken;

    public TransactionModel(String itemid, String orderid, String paid, String transtoken) {
        this.itemid = itemid;
        this.orderid = orderid;
        this.paid = paid;
        this.transtoken = transtoken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemid", itemid);
        result.put("orderid", orderid);
        result.put("paid", paid);
        result.put("transtoken", transtoken);
        return result;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getTranstoken() {
        return transtoken;
    }

    public void setTranstoken(String transtoken) {
        this.transtoken = transtoken;
    }
}
