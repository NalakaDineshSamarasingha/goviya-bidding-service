# Bidding Service - Developer Quick Reference

## Quick Start

### Prerequisites
- Java 17+
- Spring Boot 4.0.0
- MongoDB
- Maven

### Key Classes

| Class | Purpose | Location |
|-------|---------|----------|
| `Bid` | Entity model for bids | `model/Bid.java` |
| `BidRepository` | Data access layer | `repository/BidRepository.java` |
| `BidService` | Business logic | `service/BidService.java` |
| `BidController` | REST endpoints | `controller/BidController.java` |
| `PlaceBidRequest` | Place bid DTO | `model/dto/PlaceBidRequest.java` |
| `CounterOfferRequest` | Counter offer DTO | `model/dto/CounterOfferRequest.java` |
| `BidResponse` | Bid response DTO | `model/dto/BidResponse.java` |

---

## Common Operations

### Place a Bid
```java
PlaceBidRequest request = new PlaceBidRequest(
    "HARVEST", "HAR1001", new BigDecimal("145"), 300.0, "Can collect tomorrow"
);
BidResponse response = bidService.placeBid(request, "USR5001");
```

### Accept a Bid
```java
BidResponse response = bidService.acceptBid("BID1001", "USR4001");
// Other pending bids for the same harvest are auto-rejected
```

### Send Counter Offer
```java
CounterOfferRequest request = new CounterOfferRequest(
    new BigDecimal("150"), "Can you increase the price?"
);
BidResponse response = bidService.counterOffer("BID1001", request);
```

### Get All Harvest Bids
```java
List<BidResponse> bids = bidService.getHarvestBids("HAR1001");
// Expired bids are automatically marked as EXPIRED
```

---

## Status Flow Diagram

```
PENDING
  ├─→ ACCEPTED (auto-rejects other PENDING bids)
  ├─→ REJECTED
  ├─→ COUNTERED (extends expiration to 3 days)
  ├─→ WITHDRAWN
  └─→ EXPIRED (auto after 7 days, or 3 days for COUNTERED)
```

---

## Validation Rules

### Bid Placement
- [ ] Target must exist (harvest or requirement)
- [ ] Bidder must exist
- [ ] Bidder cannot be listing owner
- [ ] Quantity must not exceed available quantity
- [ ] Listing status must allow bids (not SOLD, EXPIRED, CANCELLED)

### Bid Acceptance
- [ ] Bid must be in PENDING status
- [ ] Listing must not be closed
- [ ] Quantity must be valid

### Counter Offer
- [ ] Bid must be in PENDING status
- [ ] Counter price must be valid
- [ ] Resets expiration to 3 days

---

## API Endpoints Summary

```
POST   /api/v1/bids                          - Place bid
GET    /api/v1/bids/{bidId}                  - Get bid
GET    /api/v1/bids/harvest/{harvestId}      - Get harvest bids
GET    /api/v1/bids/requirement/{requirementId} - Get requirement bids
POST   /api/v1/bids/{bidId}/accept           - Accept bid
POST   /api/v1/bids/{bidId}/reject           - Reject bid
POST   /api/v1/bids/{bidId}/counter          - Counter offer
POST   /api/v1/bids/{bidId}/withdraw         - Withdraw bid
GET    /api/v1/bids/bidder/{bidderId}        - Get bidder's bids
```

---

## Error Handling

### Common Errors

**400 Bad Request**
- Invalid input
- Business rule violation
- Invalid status transition

**404 Not Found**
- Bid not found
- Target (harvest/requirement) not found
- Bidder not found

**500 Internal Server Error**
- Database error
- Unexpected exception

### Example Error Response
```json
{
  "error": "Cannot bid on own harvest listing",
  "timestamp": 1685162400000
}
```

---

## Database Queries

### Find All Pending Bids for a Harvest
```javascript
db.bids.find({ 
  targetType: "HARVEST", 
  targetId: "HAR1001", 
  status: "PENDING" 
})
```

### Find All Bids by a Bidder
```javascript
db.bids.find({ bidderId: "USR5001" })
```

### Find Expired Bids
```javascript
db.bids.find({ 
  status: "PENDING", 
  expiresAt: { $lt: new Date() } 
})
```

### Find Accepted Bids
```javascript
db.bids.find({ 
  targetType: "HARVEST", 
  targetId: "HAR1001", 
  status: "ACCEPTED" 
})
```

---

## Testing Checklist

### Unit Tests
- [ ] Test bid validation rules
- [ ] Test bid status transitions
- [ ] Test auto-rejection mechanism
- [ ] Test counter offer logic
- [ ] Test expiration logic

### Integration Tests
- [ ] Test place bid endpoint
- [ ] Test accept bid endpoint
- [ ] Test reject bid endpoint
- [ ] Test counter offer endpoint
- [ ] Test get bids endpoints
- [ ] Test error responses

### Manual Testing
- [ ] Place bid on harvest
- [ ] Accept bid (verify other bids rejected)
- [ ] Send counter offer
- [ ] Withdraw bid
- [ ] Verify auto-expiration

---

## Performance Tips

1. **Index Usage**: All common queries use indexes defined in schema
2. **Batch Queries**: Use `findByTargetIdAndTargetType()` with composite index
3. **Lazy Loading**: Status checks are done on retrieval only
4. **Connection Pooling**: Configure MongoDB connection pool appropriately

---

## Security Considerations

1. **User ID Extraction**: Currently uses X-User-Id header (update to JWT in production)
2. **Authorization**: Verify user can only accept bids on their listings
3. **Input Validation**: All inputs validated before processing
4. **Data Sanitization**: No SQL injection risk (using MongoDB)
5. **Rate Limiting**: Consider adding rate limiting for bid placement

---

## Configuration

### application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/goviya
```

### MongoDB Connection
- Uses Spring Data MongoDB
- Auto-wired repositories
- Automatic index creation on application startup (if enabled)

---

## Logging

Add logging for production monitoring:

```java
@Slf4j
public class BidService {
    public BidResponse placeBid(PlaceBidRequest request, String bidderId) {
        log.info("Placing bid for bidder: {} on target: {}", bidderId, request.getTargetId());
        // ... rest of code
        log.info("Bid placed successfully: {}", bidId);
    }
}
```

---

## Future Enhancements Checklist

- [ ] Add notification service integration
- [ ] Implement bid history/audit trail
- [ ] Add scheduled expiration task
- [ ] Create bid analytics endpoint
- [ ] Implement advanced filtering
- [ ] Add batch operations support
- [ ] Create rating system for bidders
- [ ] Integrate payment processing

---

## Troubleshooting

### Issue: "Harvest not found"
- Verify harvest exists in database
- Check harvestId format
- Ensure harvest hasn't been deleted

### Issue: "Cannot bid on own listing"
- Verify bidderId is not the same as listing owner
- Check bidderId from JWT token

### Issue: "Bid quantity exceeds available"
- Check bid quantity vs. available quantity in listing
- Verify other accepted bids haven't reduced quantity

### Issue: "Only PENDING bids can be accepted"
- Check bid status is PENDING
- Cannot accept already accepted/rejected bids

---

## Resources

- [MongoDB Documentation](https://docs.mongodb.com/)
- [Spring Data MongoDB Guide](https://spring.io/projects/spring-data-mongodb)
- [Spring Boot REST API Guide](https://spring.io/guides/gs/rest-service/)
- [OpenAPI Documentation](https://swagger.io/specification/)

---

## Contact & Support

For questions or issues with the bidding service:
1. Check API_DOCUMENTATION.md for endpoint details
2. Review IMPLEMENTATION_SUMMARY.md for architecture overview
3. Check logs for error details
4. Review business rules in service layer comments
