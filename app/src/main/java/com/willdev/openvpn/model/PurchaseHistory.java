
package com.willdev.openvpn.model;

import com.google.gson.annotations.SerializedName;

public class PurchaseHistory {

    @SerializedName("payment_method")
    public String payment_method;
    @SerializedName("amount")
    public String amount;
    @SerializedName("date_created")
    public String date_created;

    @SerializedName("type")
    public String type;

    public PurchaseHistory(String payment_method, String amount, String date_created, String type) {
        this.payment_method = payment_method;
        this.amount = amount;
        this.date_created = date_created;
        this.type = type;
    }
}