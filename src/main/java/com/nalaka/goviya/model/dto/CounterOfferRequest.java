package com.nalaka.goviya.model.dto;

import java.math.BigDecimal;

public class CounterOfferRequest {
    private BigDecimal counterPrice;
    private String message;

    public CounterOfferRequest() {
    }

    public CounterOfferRequest(BigDecimal counterPrice, String message) {
        this.counterPrice = counterPrice;
        this.message = message;
    }

    public BigDecimal getCounterPrice() {
        return counterPrice;
    }

    public void setCounterPrice(BigDecimal counterPrice) {
        this.counterPrice = counterPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
