package com.nalaka.goviya.repository;

import com.nalaka.goviya.model.Requirement;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementRepository extends MongoRepository<Requirement, String> {
    
    Optional<Requirement> findByRequirementId(String requirementId);
    
    List<Requirement> findByMerchantId(String merchantId);
    
    List<Requirement> findByStatus(String status);
    
    List<Requirement> findByCropType(String cropType);
    
    List<Requirement> findByDistrict(String district);
    
    @Query("{'location': {$near: {$geometry: ?0, $maxDistance: ?1}}}")
    List<Requirement> findNearby(GeoJsonPoint point, Integer maxDistanceMeters);
    
    @Query("{'cropType': ?1, 'location': {$near: {$geometry: ?0, $maxDistance: ?2}}}")
    List<Requirement> findNearbyCropType(GeoJsonPoint point, String cropType, Integer maxDistanceMeters);
}
