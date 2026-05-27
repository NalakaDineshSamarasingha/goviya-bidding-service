# Bidding Management Service Documentation

## Overview
The Bidding Management Service handles all bid-related operations including placing bids, accepting/rejecting offers, and managing the bid lifecycle with automatic expiration.

## Base URL
```
/api/v1/bids
```

## API Endpoints

### 1. Place Bid
**Endpoint:** `POST /api/v1/bids`

**Description:** Create a new bid for a harvest or requirement listing.

**Request Body:**
```json
{
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow"
}
```

**Response (201 Created):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow",
  "status": "PENDING",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T10:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

**Business Rules:**
- Cannot bid on own listing
- Cannot exceed available quantity
- Closed harvests cannot accept bids
- Expired listings reject new bids

**Error Responses:**
- `400 Bad Request` - Invalid input or business rule violation
- `404 Not Found` - Target not found
- `500 Internal Server Error` - Server error

---

### 2. Get Bid By ID
**Endpoint:** `GET /api/v1/bids/{bidId}`

**Description:** Retrieve a specific bid by its ID.

**Path Parameters:**
- `bidId` (string, required) - The bid ID (e.g., "BID1001")

**Response (200 OK):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow",
  "status": "PENDING",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T10:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 3. Get Harvest Bids
**Endpoint:** `GET /api/v1/bids/harvest/{harvestId}`

**Description:** Retrieve all bids for a specific harvest.

**Path Parameters:**
- `harvestId` (string, required) - The harvest ID (e.g., "HAR1001")

**Response (200 OK):**
```json
[
  {
    "bidId": "BID1001",
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "bidderId": "USR5001",
    "bidderType": "MERCHANT",
    "pricePerUnit": 145,
    "quantity": 300,
    "message": "Can collect tomorrow",
    "status": "PENDING",
    "createdAt": "2026-05-27T10:30:00Z",
    "updatedAt": "2026-05-27T10:30:00Z",
    "expiresAt": "2026-06-03T10:30:00Z"
  },
  {
    "bidId": "BID1002",
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "bidderId": "USR5002",
    "bidderType": "MERCHANT",
    "pricePerUnit": 150,
    "quantity": 250,
    "message": "Premium quality needed",
    "status": "PENDING",
    "createdAt": "2026-05-27T11:00:00Z",
    "updatedAt": "2026-05-27T11:00:00Z",
    "expiresAt": "2026-06-03T11:00:00Z"
  }
]
```

---

### 4. Get Requirement Bids
**Endpoint:** `GET /api/v1/bids/requirement/{requirementId}`

**Description:** Retrieve all bids for a specific requirement.

**Path Parameters:**
- `requirementId` (string, required) - The requirement ID (e.g., "REQ2001")

**Response (200 OK):**
```json
[
  {
    "bidId": "BID1003",
    "targetType": "REQUIREMENT",
    "targetId": "REQ2001",
    "bidderId": "USR6001",
    "bidderType": "FARMER",
    "pricePerUnit": 120,
    "quantity": 500,
    "message": "Fresh produce available",
    "status": "PENDING",
    "createdAt": "2026-05-27T12:00:00Z",
    "updatedAt": "2026-05-27T12:00:00Z",
    "expiresAt": "2026-06-03T12:00:00Z"
  }
]
```

---

### 5. Accept Bid
**Endpoint:** `POST /api/v1/bids/{bidId}/accept`

**Description:** Accept a pending bid. This will auto-reject all other pending bids for the same target.

**Path Parameters:**
- `bidId` (string, required) - The bid ID to accept

**Response (200 OK):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow",
  "status": "ACCEPTED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T14:00:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

**Business Rules:**
- Can only accept PENDING bids
- Auto-rejects other pending bids for the same target
- Cannot accept bid if harvest is closed
- Cannot exceed available quantity

---

### 6. Reject Bid
**Endpoint:** `POST /api/v1/bids/{bidId}/reject`

**Description:** Reject a pending bid.

**Path Parameters:**
- `bidId` (string, required) - The bid ID to reject

**Response (200 OK):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow",
  "status": "REJECTED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T14:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 7. Counter Offer
**Endpoint:** `POST /api/v1/bids/{bidId}/counter`

**Description:** Send a counter offer for a pending bid with modified price.

**Path Parameters:**
- `bidId` (string, required) - The bid ID to counter

**Request Body:**
```json
{
  "counterPrice": 150,
  "message": "Can you increase the price?"
}
```

**Response (200 OK):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 150,
  "quantity": 300,
  "message": "Can you increase the price?",
  "status": "COUNTERED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T15:00:00Z",
  "expiresAt": "2026-05-30T15:00:00Z"
}
```

**Note:** Counter offer resets the expiration time to 3 days from the counter offer date.

---

### 8. Withdraw Bid
**Endpoint:** `POST /api/v1/bids/{bidId}/withdraw`

**Description:** Withdraw a pending or countered bid.

**Path Parameters:**
- `bidId` (string, required) - The bid ID to withdraw

**Response (200 OK):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 300,
  "message": "Can collect tomorrow",
  "status": "WITHDRAWN",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T15:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 9. Get Bids by Bidder
**Endpoint:** `GET /api/v1/bids/bidder/{bidderId}`

**Description:** Retrieve all bids placed by a specific bidder.

**Path Parameters:**
- `bidderId` (string, required) - The bidder's user ID

**Response (200 OK):**
```json
[
  {
    "bidId": "BID1001",
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "bidderId": "USR5001",
    "bidderType": "MERCHANT",
    "pricePerUnit": 145,
    "quantity": 300,
    "message": "Can collect tomorrow",
    "status": "ACCEPTED",
    "createdAt": "2026-05-27T10:30:00Z",
    "updatedAt": "2026-05-27T14:00:00Z",
    "expiresAt": "2026-06-03T10:30:00Z"
  },
  {
    "bidId": "BID1004",
    "targetType": "REQUIREMENT",
    "targetId": "REQ2002",
    "bidderId": "USR5001",
    "bidderType": "MERCHANT",
    "pricePerUnit": 130,
    "quantity": 200,
    "message": "Quick delivery available",
    "status": "PENDING",
    "createdAt": "2026-05-27T16:00:00Z",
    "updatedAt": "2026-05-27T16:00:00Z",
    "expiresAt": "2026-06-03T16:00:00Z"
  }
]
```

---

## Bid Status Types

| Status | Description |
|--------|-------------|
| PENDING | Waiting for response from listing owner |
| ACCEPTED | Bid has been accepted |
| REJECTED | Bid has been rejected |
| COUNTERED | Counter offer has been sent |
| WITHDRAWN | Bid has been withdrawn by bidder |
| EXPIRED | Bid has expired (7 days default) |

---

## Bid Entity Structure

```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST|REQUIREMENT",
  "targetId": "HAR1001|REQ2001",
  "bidderId": "USR5001",
  "bidderType": "MERCHANT|FARMER",
  "pricePerUnit": 140.00,
  "quantity": 300,
  "message": "Can collect immediately",
  "status": "PENDING|ACCEPTED|REJECTED|COUNTERED|WITHDRAWN|EXPIRED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T10:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

## Business Rules

### Bid Validation Rules
1. **Cannot bid own listing**: A user cannot place a bid on their own harvest or requirement
2. **Quantity validation**: Bid quantity cannot exceed available listing quantity
3. **Closed harvest validation**: Closed/Sold/Cancelled harvests cannot accept new bids
4. **Expired listing validation**: Expired listings reject new bids

### Bid Lifecycle Rules
1. **Auto-rejection**: When a bid is accepted, all other PENDING bids for the same target are automatically rejected
2. **Bid expiration**: Bids expire after 7 days by default
3. **Counter offer expiration**: Counter offers are valid for 3 days
4. **Status transitions**: Only certain status transitions are allowed (PENDING → ACCEPTED/REJECTED/COUNTERED/WITHDRAWN)

---

## Error Responses

All error responses follow this format:

```json
{
  "error": "Error description",
  "timestamp": 1685162400000
}
```

### Common Error Codes

| HTTP Code | Scenario |
|-----------|----------|
| 400 Bad Request | Invalid input, business rule violation, invalid status transition |
| 404 Not Found | Bid, Harvest, Requirement, or User not found |
| 500 Internal Server Error | Server-side error |

---

## Usage Examples

### Example 1: Place a Bid on a Harvest
```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: USR5001" \
  -d '{
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "pricePerUnit": 145,
    "quantity": 300,
    "message": "Can collect tomorrow"
  }'
```

### Example 2: Accept a Bid
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/accept \
  -H "X-User-Id: USR4001"
```

### Example 3: Send Counter Offer
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/counter \
  -H "Content-Type: application/json" \
  -d '{
    "counterPrice": 150,
    "message": "Can you increase the price?"
  }'
```

### Example 4: Get All Bids for a Harvest
```bash
curl http://localhost:8080/api/v1/bids/harvest/HAR1001
```

### Example 5: Reject a Bid
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/reject
```

---

## Authentication & Authorization

Currently, the bidder ID is extracted from the `X-User-Id` header. In production:
- Use JWT tokens from security context instead
- Implement proper authorization checks to ensure users can only accept/reject/counter their own bids
- Add role-based access control (FARMER vs MERCHANT permissions)

---

## Performance Considerations

The following indexes have been created on the `bids` collection:
- `bidId` (unique)
- `targetType` + `targetId`
- `status`
- `bidderId`
- `createdAt`
- `expiresAt`
- `targetType` + `targetId` + `status`

These indexes optimize:
- Looking up bids by target (harvest/requirement)
- Filtering by status
- Finding bids by bidder
- Expiration checks

---

## Future Enhancements

1. **Notification System**: Send notifications when bids are placed, accepted, rejected, or countered
2. **Bid History**: Track all changes to a bid with audit trail
3. **Automatic Cleanup**: Scheduled job to mark expired bids
4. **Analytics**: Bid acceptance rate, average bid amounts by crop type, etc.
5. **Advanced Filtering**: Filter bids by date range, price range, location radius
6. **Bulk Operations**: Accept/reject multiple bids at once
