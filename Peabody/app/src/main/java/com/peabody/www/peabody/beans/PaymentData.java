package com.peabody.www.peabody.beans;

public class PaymentData {
    String amount;
    String provider;
    String reference;
    String method;

    public PaymentData(){}
    public PaymentData(String amount, String provider, String reference, String method) {
        this.amount = amount;
        this.provider = provider;
        this.reference = reference;
        this.method = method;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
