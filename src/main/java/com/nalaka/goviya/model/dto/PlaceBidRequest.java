package com.nalaka.goviya.model.dto;

import java.math.BigDecimal;

public class PlaceBidRequest {
    private String targetType; // HARVEST, REQUIREMENT
    private String targetId;
    private BigDecimal pricePerUnit;
    private Double quantity;
    private String message;

    public PlaceBidRequest() {
    }

    public PlaceBidRequest(String targetType, String targetId, BigDecimal pricePerUnit, Double quantity, String message) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.message = message;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
