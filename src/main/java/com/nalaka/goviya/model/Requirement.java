package com.nalaka.goviya.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "requirements")
public class Requirement {
    @Id
    private String id;
    private String requirementId;
    private String merchantId;
    private String cropType;
    private Double requiredQuantity;
    private String unit;
    private Double expectedPrice;
    private String district;
    private Double latitude;
    private Double longitude;
    
    @GeoSpatialIndexed(name = "location_2dsphere")
    private GeoJsonPoint location;
    
    private LocalDate requiredBefore;
    private String description;
    private String status; // OPEN, IN_NEGOTIATION, FULFILLED, EXPIRED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Requirement() {
    }

    public Requirement(String merchantId, String cropType, Double requiredQuantity, String unit,
                       Double expectedPrice, String district, Double latitude, Double longitude,
                       LocalDate requiredBefore, String description) {
        this.merchantId = merchantId;
        this.cropType = cropType;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
        this.expectedPrice = expectedPrice;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new GeoJsonPoint(longitude, latitude);
        this.requiredBefore = requiredBefore;
        this.description = description;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public Double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(Double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(Double expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    public LocalDate getRequiredBefore() {
        return requiredBefore;
    }

    public void setRequiredBefore(LocalDate requiredBefore) {
        this.requiredBefore = requiredBefore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
