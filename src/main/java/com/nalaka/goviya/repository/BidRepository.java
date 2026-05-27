package com.nalaka.goviya.repository;

import com.nalaka.goviya.model.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends MongoRepository<Bid, String> {
    
    /**
     * Find bid by bidId
     */
    Optional<Bid> findByBidId(String bidId);
    
    /**
     * Find all bids for a specific harvest
     */
    List<Bid> findByTargetIdAndTargetType(String targetId, String targetType);
    
    /**
     * Find all bids for a specific requirement
     */
    List<Bid> findByTargetIdAndTargetTypeAndStatus(String targetId, String targetType, String status);
    
    /**
     * Find bids by bidder
     */
    List<Bid> findByBidderId(String bidderId);
    
    /**
     * Find accepted bid for a target
     */
    List<Bid> findByTargetIdAndTargetTypeAndStatusAndBidderId(String targetId, String targetType, String status, String bidderId);
    
    /**
     * Count active bids for a bidder on a specific target
     */
    long countByBidderIdAndTargetIdAndTargetTypeAndStatus(String bidderId, String targetId, String targetType, String status);
}
