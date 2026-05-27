package com.nalaka.goviya.service;

import com.nalaka.goviya.model.Bid;
import com.nalaka.goviya.model.Harvest;
import com.nalaka.goviya.model.Requirement;
import com.nalaka.goviya.model.User;
import com.nalaka.goviya.model.dto.BidResponse;
import com.nalaka.goviya.model.dto.CounterOfferRequest;
import com.nalaka.goviya.model.dto.PlaceBidRequest;
import com.nalaka.goviya.repository.BidRepository;
import com.nalaka.goviya.repository.HarvestRepository;
import com.nalaka.goviya.repository.RequirementRepository;
import com.nalaka.goviya.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private HarvestRepository harvestRepository;

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Place a new bid
     */
    public BidResponse placeBid(PlaceBidRequest request, String bidderId) {
        // Validation: Check if bidder exists
        Optional<User> bidder = userRepository.findById(bidderId);
        if (bidder.isEmpty()) {
            throw new IllegalArgumentException("Bidder not found");
        }

        // Validation: Check target exists and get its details
        if ("HARVEST".equalsIgnoreCase(request.getTargetType())) {
            validateHarvestBid(request, bidderId);
        } else if ("REQUIREMENT".equalsIgnoreCase(request.getTargetType())) {
            validateRequirementBid(request, bidderId);
        } else {
            throw new IllegalArgumentException("Invalid target type");
        }

        // Create new bid
        String bidId = "BID" + System.currentTimeMillis();
        Bid bid = new Bid(
            bidId,
            request.getTargetType(),
            request.getTargetId(),
            bidderId,
            bidder.get().getRole(),
            request.getPricePerUnit(),
            request.getQuantity(),
            request.getMessage()
        );
        bid.setCreatedBy(bidderId);

        Bid savedBid = bidRepository.save(bid);
        return convertToResponse(savedBid);
    }

    /**
     * Get bid by ID
     */
    public BidResponse getBidById(String bidId) {
        Optional<Bid> bid = bidRepository.findByBidId(bidId);
        if (bid.isEmpty()) {
            throw new IllegalArgumentException("Bid not found");
        }
        
        // Check if bid is expired
        Bid bidObj = bid.get();
        if (bidObj.getExpiresAt().isBefore(LocalDateTime.now()) && 
            "PENDING".equalsIgnoreCase(bidObj.getStatus())) {
            bidObj.setStatus("EXPIRED");
            bidRepository.save(bidObj);
        }
        
        return convertToResponse(bidObj);
    }

    /**
     * Get all bids for a specific harvest
     */
    public List<BidResponse> getHarvestBids(String harvestId) {
        // Validate harvest exists
        Optional<Harvest> harvest = harvestRepository.findByHarvestId(harvestId);
        if (harvest.isEmpty()) {
            throw new IllegalArgumentException("Harvest not found");
        }

        List<Bid> bids = bidRepository.findByTargetIdAndTargetType(harvestId, "HARVEST");
        
        // Check for expired bids and update status
        bids.forEach(bid -> {
            if (bid.getExpiresAt().isBefore(LocalDateTime.now()) && 
                "PENDING".equalsIgnoreCase(bid.getStatus())) {
                bid.setStatus("EXPIRED");
                bidRepository.save(bid);
            }
        });
        
        return bids.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all bids for a specific requirement
     */
    public List<BidResponse> getRequirementBids(String requirementId) {
        // Validate requirement exists
        Optional<Requirement> requirement = requirementRepository.findByRequirementId(requirementId);
        if (requirement.isEmpty()) {
            throw new IllegalArgumentException("Requirement not found");
        }

        List<Bid> bids = bidRepository.findByTargetIdAndTargetType(requirementId, "REQUIREMENT");
        
        // Check for expired bids and update status
        bids.forEach(bid -> {
            if (bid.getExpiresAt().isBefore(LocalDateTime.now()) && 
                "PENDING".equalsIgnoreCase(bid.getStatus())) {
                bid.setStatus("EXPIRED");
                bidRepository.save(bid);
            }
        });
        
        return bids.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Accept a bid
     */
    public BidResponse acceptBid(String bidId, String acceptedBy) {
        Optional<Bid> bidOptional = bidRepository.findByBidId(bidId);
        if (bidOptional.isEmpty()) {
            throw new IllegalArgumentException("Bid not found");
        }

        Bid bid = bidOptional.get();

        // Validation: Cannot accept non-pending bid
        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bids can be accepted");
        }

        // Validation: Cannot exceed quantity
        if ("HARVEST".equalsIgnoreCase(bid.getTargetType())) {
            Optional<Harvest> harvest = harvestRepository.findByHarvestId(bid.getTargetId());
            if (harvest.isPresent()) {
                Harvest h = harvest.get();
                // Validation: Cannot accept bid for closed harvest
                if ("SOLD".equalsIgnoreCase(h.getStatus()) || "CANCELLED".equalsIgnoreCase(h.getStatus())) {
                    throw new IllegalArgumentException("Cannot accept bid for closed harvest");
                }
                if (bid.getQuantity() > h.getQuantity()) {
                    throw new IllegalArgumentException("Bid quantity exceeds available harvest quantity");
                }
            }
        }

        // Accept the bid
        bid.setStatus("ACCEPTED");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Business rule: Auto-reject other pending bids for the same target
        rejectOtherBidsForTarget(bid.getTargetId(), bid.getTargetType(), bidId);

        return convertToResponse(savedBid);
    }

    /**
     * Reject a bid
     */
    public BidResponse rejectBid(String bidId) {
        Optional<Bid> bidOptional = bidRepository.findByBidId(bidId);
        if (bidOptional.isEmpty()) {
            throw new IllegalArgumentException("Bid not found");
        }

        Bid bid = bidOptional.get();

        // Validation: Cannot reject non-pending bid
        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bids can be rejected");
        }

        bid.setStatus("REJECTED");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        return convertToResponse(savedBid);
    }

    /**
     * Send counter offer
     */
    public BidResponse counterOffer(String bidId, CounterOfferRequest request) {
        Optional<Bid> bidOptional = bidRepository.findByBidId(bidId);
        if (bidOptional.isEmpty()) {
            throw new IllegalArgumentException("Bid not found");
        }

        Bid bid = bidOptional.get();

        // Validation: Cannot counter non-pending bid
        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bids can be countered");
        }

        // Update bid with counter offer
        bid.setStatus("COUNTERED");
        bid.setPricePerUnit(request.getCounterPrice());
        bid.setMessage(request.getMessage());
        bid.setUpdatedAt(LocalDateTime.now());
        // Reset expiration for counter offer
        bid.setExpiresAt(LocalDateTime.now().plusDays(3));

        Bid savedBid = bidRepository.save(bid);
        return convertToResponse(savedBid);
    }

    /**
     * Withdraw a bid
     */
    public BidResponse withdrawBid(String bidId) {
        Optional<Bid> bidOptional = bidRepository.findByBidId(bidId);
        if (bidOptional.isEmpty()) {
            throw new IllegalArgumentException("Bid not found");
        }

        Bid bid = bidOptional.get();

        // Can only withdraw pending or countered bids
        if (!"PENDING".equalsIgnoreCase(bid.getStatus()) && 
            !"COUNTERED".equalsIgnoreCase(bid.getStatus())) {
            throw new IllegalArgumentException("Can only withdraw PENDING or COUNTERED bids");
        }

        bid.setStatus("WITHDRAWN");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        return convertToResponse(savedBid);
    }

    /**
     * Get bids by bidder
     */
    public List<BidResponse> getBidsByBidderId(String bidderId) {
        List<Bid> bids = bidRepository.findByBidderId(bidderId);
        
        // Check for expired bids and update status
        bids.forEach(bid -> {
            if (bid.getExpiresAt().isBefore(LocalDateTime.now()) && 
                "PENDING".equalsIgnoreCase(bid.getStatus())) {
                bid.setStatus("EXPIRED");
                bidRepository.save(bid);
            }
        });
        
        return bids.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Validate harvest bid
     */
    private void validateHarvestBid(PlaceBidRequest request, String bidderId) {
        Optional<Harvest> harvest = harvestRepository.findByHarvestId(request.getTargetId());
        
        if (harvest.isEmpty()) {
            throw new IllegalArgumentException("Harvest not found");
        }

        Harvest h = harvest.get();

        // Validation: Cannot bid on own listing
        if (h.getFarmerId().equals(bidderId)) {
            throw new IllegalArgumentException("Cannot bid on own harvest listing");
        }

        // Validation: Cannot exceed quantity
        if (request.getQuantity() > h.getQuantity()) {
            throw new IllegalArgumentException("Bid quantity exceeds available harvest quantity");
        }

        // Validation: Closed harvest cannot accept bids
        if ("SOLD".equalsIgnoreCase(h.getStatus()) || "CANCELLED".equalsIgnoreCase(h.getStatus())) {
            throw new IllegalArgumentException("Closed harvest cannot accept bids");
        }

        // Validation: Expired listing rejects new bids
        if ("EXPIRED".equalsIgnoreCase(h.getStatus())) {
            throw new IllegalArgumentException("Expired harvest listing cannot accept bids");
        }
    }

    /**
     * Validate requirement bid
     */
    private void validateRequirementBid(PlaceBidRequest request, String bidderId) {
        Optional<Requirement> requirement = requirementRepository.findByRequirementId(request.getTargetId());
        
        if (requirement.isEmpty()) {
            throw new IllegalArgumentException("Requirement not found");
        }

        Requirement r = requirement.get();

        // Validation: Cannot bid on own requirement
        if (r.getMerchantId().equals(bidderId)) {
            throw new IllegalArgumentException("Cannot bid on own requirement");
        }

        // Validation: Cannot exceed quantity
        if (request.getQuantity() > r.getRequiredQuantity()) {
            throw new IllegalArgumentException("Bid quantity exceeds required quantity");
        }

        // Validation: Expired requirement rejects new bids
        if ("EXPIRED".equalsIgnoreCase(r.getStatus()) || "FULFILLED".equalsIgnoreCase(r.getStatus())) {
            throw new IllegalArgumentException("Expired or fulfilled requirement cannot accept bids");
        }
    }

    /**
     * Auto-reject other pending bids for the same target
     */
    private void rejectOtherBidsForTarget(String targetId, String targetType, String acceptedBidId) {
        List<Bid> bids = bidRepository.findByTargetIdAndTargetType(targetId, targetType);
        
        bids.forEach(bid -> {
            if (!bid.getBidId().equals(acceptedBidId) && "PENDING".equalsIgnoreCase(bid.getStatus())) {
                bid.setStatus("REJECTED");
                bid.setUpdatedAt(LocalDateTime.now());
                bidRepository.save(bid);
            }
        });
    }

    /**
     * Convert Bid entity to BidResponse DTO
     */
    private BidResponse convertToResponse(Bid bid) {
        return new BidResponse(
            bid.getBidId(),
            bid.getTargetType(),
            bid.getTargetId(),
            bid.getBidderId(),
            bid.getBidderType(),
            bid.getPricePerUnit(),
            bid.getQuantity(),
            bid.getMessage(),
            bid.getStatus(),
            bid.getCreatedAt(),
            bid.getUpdatedAt(),
            bid.getExpiresAt()
        );
    }
}
