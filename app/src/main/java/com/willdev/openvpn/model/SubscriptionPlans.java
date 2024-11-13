package com.willdev.openvpn.model;

public class SubscriptionPlans {
    private String name;
    private String product_id;

    private String stripe_product_id;
    private String price;
    private String currency;

    private String status;
    public SubscriptionPlans() {
    }

    public SubscriptionPlans(String name, String product_id, String price, String currency, String status) {
        this.name = name;
        this.product_id = product_id;
        this.price = price;
        this.currency = currency;
        this.status = status;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getStripeProductId() {
        return stripe_product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
