package com.nalaka.goviya.controller;

import com.nalaka.goviya.model.dto.BidResponse;
import com.nalaka.goviya.model.dto.CounterOfferRequest;
import com.nalaka.goviya.model.dto.PlaceBidRequest;
import com.nalaka.goviya.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bids")
@Tag(name = "Bid Management", description = "APIs for managing bids")
public class BidController {

    @Autowired
    private BidService bidService;

    /**
     * Place a new bid
     */
    @PostMapping
    @Operation(summary = "Place a new bid", description = "Create a new bid for a harvest or requirement")
    public ResponseEntity<?> placeBid(@RequestBody PlaceBidRequest request,
                                     @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            // In a real application, get userId from JWT token or security context
            if (userId == null || userId.isEmpty()) {
                userId = "USR" + System.currentTimeMillis(); // Placeholder
            }
            
            BidResponse response = bidService.placeBid(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error placing bid: " + e.getMessage()));
        }
    }

    /**
     * Get bid by ID
     */
    @GetMapping("/{bidId}")
    @Operation(summary = "Get bid by ID", description = "Retrieve a specific bid by its ID")
    public ResponseEntity<?> getBidById(@PathVariable String bidId) {
        try {
            BidResponse response = bidService.getBidById(bidId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error retrieving bid: " + e.getMessage()));
        }
    }

    /**
     * Get all bids for a harvest
     */
    @GetMapping("/harvest/{harvestId}")
    @Operation(summary = "Get harvest bids", description = "Retrieve all bids for a specific harvest")
    public ResponseEntity<?> getHarvestBids(@PathVariable String harvestId) {
        try {
            List<BidResponse> responses = bidService.getHarvestBids(harvestId);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error retrieving harvest bids: " + e.getMessage()));
        }
    }

    /**
     * Get all bids for a requirement
     */
    @GetMapping("/requirement/{requirementId}")
    @Operation(summary = "Get requirement bids", description = "Retrieve all bids for a specific requirement")
    public ResponseEntity<?> getRequirementBids(@PathVariable String requirementId) {
        try {
            List<BidResponse> responses = bidService.getRequirementBids(requirementId);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error retrieving requirement bids: " + e.getMessage()));
        }
    }

    /**
     * Accept a bid
     */
    @PostMapping("/{bidId}/accept")
    @Operation(summary = "Accept a bid", description = "Accept a pending bid")
    public ResponseEntity<?> acceptBid(@PathVariable String bidId,
                                       @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                userId = "USR" + System.currentTimeMillis();
            }
            
            BidResponse response = bidService.acceptBid(bidId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error accepting bid: " + e.getMessage()));
        }
    }

    /**
     * Reject a bid
     */
    @PostMapping("/{bidId}/reject")
    @Operation(summary = "Reject a bid", description = "Reject a pending bid")
    public ResponseEntity<?> rejectBid(@PathVariable String bidId) {
        try {
            BidResponse response = bidService.rejectBid(bidId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error rejecting bid: " + e.getMessage()));
        }
    }

    /**
     * Send counter offer for a bid
     */
    @PostMapping("/{bidId}/counter")
    @Operation(summary = "Send counter offer", description = "Send a counter offer for a pending bid")
    public ResponseEntity<?> counterOffer(@PathVariable String bidId,
                                         @RequestBody CounterOfferRequest request) {
        try {
            BidResponse response = bidService.counterOffer(bidId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error sending counter offer: " + e.getMessage()));
        }
    }

    /**
     * Withdraw a bid
     */
    @PostMapping("/{bidId}/withdraw")
    @Operation(summary = "Withdraw a bid", description = "Withdraw a pending or countered bid")
    public ResponseEntity<?> withdrawBid(@PathVariable String bidId) {
        try {
            BidResponse response = bidService.withdrawBid(bidId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error withdrawing bid: " + e.getMessage()));
        }
    }

    /**
     * Get bids by bidder
     */
    @GetMapping("/bidder/{bidderId}")
    @Operation(summary = "Get bids by bidder", description = "Retrieve all bids placed by a specific bidder")
    public ResponseEntity<?> getBidsByBidderId(@PathVariable String bidderId) {
        try {
            List<BidResponse> responses = bidService.getBidsByBidderId(bidderId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error retrieving bids: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
