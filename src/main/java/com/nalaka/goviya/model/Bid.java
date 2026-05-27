package com.nalaka.goviya.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Document(collection = "bids")
public class Bid {
    @Id
    private String id;
    private String bidId;
    private String targetType; // HARVEST, REQUIREMENT
    private String targetId;
    private String bidderId;
    private String bidderType; // MERCHANT, FARMER
    private BigDecimal pricePerUnit;
    private Double quantity;
    private String message;
    private String status; // PENDING, ACCEPTED, REJECTED, COUNTERED, WITHDRAWN, EXPIRED
    private String createdBy; // User who created the bid
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;

    public Bid() {
    }

    public Bid(String bidId, String targetType, String targetId, String bidderId, String bidderType,
               BigDecimal pricePerUnit, Double quantity, String message) {
        this.bidId = bidId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.bidderId = bidderId;
        this.bidderType = bidderType;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.message = message;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Set expiration to 7 days from now
        this.expiresAt = LocalDateTime.now().plusDays(7);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
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

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public String getBidderType() {
        return bidderType;
    }

    public void setBidderType(String bidderType) {
        this.bidderType = bidderType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
