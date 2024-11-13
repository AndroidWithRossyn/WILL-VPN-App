package com.willdev.openvpn.model;

public class PaymentMethod {
    public String name;
    public String status;
    public PaymentMethod(String name, String status) {
        this.name = name;
        this.status = status;
    }
}