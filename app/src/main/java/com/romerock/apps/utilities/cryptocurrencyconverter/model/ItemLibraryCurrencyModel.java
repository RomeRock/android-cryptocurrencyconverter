package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ebricko on 15/03/2018.
 */

public class ItemLibraryCurrencyModel  implements Serializable {

    @SerializedName("name")
    private String name;
    @SerializedName("country_iso2")
    private String country_iso2;
    @SerializedName("country_name")
    private String country_name;
    @SerializedName("currency_name")
    private String currency_name;
    @SerializedName("currency_symbol")
    private String currency_symbol;
    @SerializedName("currency")
    private Double currency;

    public ItemLibraryCurrencyModel() {
    }

    public ItemLibraryCurrencyModel(String name, String country_iso2, String country_name, String currency_name, String currency_symbol, Double currency) {
        this.name = name;
        this.country_iso2 = country_iso2;
        this.country_name = country_name;
        this.currency_name = currency_name;
        this.currency_symbol = currency_symbol;
        this.currency=currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_iso2() {
        return country_iso2;
    }

    public void setCountry_iso2(String country_iso2) {
        this.country_iso2 = country_iso2;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public Double getCurrency() {
        return currency;
    }

    public void setCurrency(Double currency) {
        this.currency = currency;
    }
}
