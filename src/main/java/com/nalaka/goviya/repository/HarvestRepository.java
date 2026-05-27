package com.nalaka.goviya.repository;

import com.nalaka.goviya.model.Harvest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestRepository extends MongoRepository<Harvest, String> {
    
    Optional<Harvest> findByHarvestId(String harvestId);
    
    List<Harvest> findByFarmerId(String farmerId);
    
    List<Harvest> findByStatus(String status);
    
    List<Harvest> findByCropType(String cropType);
    
    List<Harvest> findByDistrict(String district);
    
    @Query("{'cropType': ?0, 'pricePerUnit': {$gte: ?1, $lte: ?2}}")
    List<Harvest> findByCropTypeAndPriceRange(String cropType, Double minPrice, Double maxPrice);
    
    List<Harvest> findByOrganic(Boolean organic);
    
    @Query("{'location': {$near: {$geometry: ?0, $maxDistance: ?1}}}")
    List<Harvest> findNearby(GeoJsonPoint point, Integer maxDistanceMeters);
    
    @Query("{'cropType': ?1, 'location': {$near: {$geometry: ?0, $maxDistance: ?2}}}")
    List<Harvest> findNearbyCropType(GeoJsonPoint point, String cropType, Integer maxDistanceMeters);
}
