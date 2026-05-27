# Bid Service Test Data & cURL Examples

## Sample Test Data

### Prerequisites
Ensure you have test data for:
1. Users (farmers and merchants)
2. Harvests
3. Requirements

### Sample Users
```javascript
db.users.insertMany([
  {
    "firstName": "Farmer",
    "lastName": "John",
    "fullName": "Farmer John",
    "email": "farmer@example.com",
    "phone": "1234567890",
    "role": "FARMER",
    "district": "Colombo"
  },
  {
    "firstName": "Merchant",
    "lastName": "Smith",
    "fullName": "Merchant Smith",
    "email": "merchant@example.com",
    "phone": "0987654321",
    "role": "MERCHANT",
    "district": "Colombo"
  }
]);
```

### Sample Harvests
```javascript
db.harvests.insertMany([
  {
    "harvestId": "HAR1001",
    "farmerId": "FARMER_USER_ID",
    "title": "Fresh Tomatoes",
    "cropType": "TOMATO",
    "quantity": 500,
    "unit": "KG",
    "pricePerUnit": 140,
    "district": "Colombo",
    "latitude": 6.9271,
    "longitude": 80.7789,
    "location": { type: "Point", coordinates: [80.7789, 6.9271] },
    "harvestDate": new Date("2026-05-28"),
    "availableUntil": new Date("2026-06-28"),
    "organic": true,
    "description": "Fresh organic tomatoes from local farm",
    "status": "ACTIVE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);
```

### Sample Requirements
```javascript
db.requirements.insertMany([
  {
    "requirementId": "REQ2001",
    "merchantId": "MERCHANT_USER_ID",
    "cropType": "TOMATO",
    "requiredQuantity": 300,
    "unit": "KG",
    "expectedPrice": 150,
    "district": "Colombo",
    "latitude": 6.9271,
    "longitude": 80.7789,
    "location": { type: "Point", coordinates: [80.7789, 6.9271] },
    "requiredBefore": new Date("2026-06-10"),
    "description": "Need fresh tomatoes for restaurant",
    "status": "OPEN",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);
```

---

## cURL Examples

### 1. Place Bid on Harvest
```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: MERCHANT_USER_ID" \
  -d '{
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "pricePerUnit": 145,
    "quantity": 250,
    "message": "Can collect tomorrow morning"
  }'
```

**Expected Response (201 Created):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "MERCHANT_USER_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 250,
  "message": "Can collect tomorrow morning",
  "status": "PENDING",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T10:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 2. Place Bid on Requirement
```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: FARMER_USER_ID" \
  -d '{
    "targetType": "REQUIREMENT",
    "targetId": "REQ2001",
    "pricePerUnit": 140,
    "quantity": 300,
    "message": "Fresh tomatoes available from my farm"
  }'
```

---

### 3. Get Bid by ID
```bash
curl -X GET http://localhost:8080/api/v1/bids/BID1001 \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "MERCHANT_USER_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 250,
  "message": "Can collect tomorrow morning",
  "status": "PENDING",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T10:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 4. Get All Bids for a Harvest
```bash
curl -X GET http://localhost:8080/api/v1/bids/harvest/HAR1001 \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
[
  {
    "bidId": "BID1001",
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "bidderId": "MERCHANT_USER_ID",
    "bidderType": "MERCHANT",
    "pricePerUnit": 145,
    "quantity": 250,
    "message": "Can collect tomorrow morning",
    "status": "PENDING",
    "createdAt": "2026-05-27T10:30:00Z",
    "updatedAt": "2026-05-27T10:30:00Z",
    "expiresAt": "2026-06-03T10:30:00Z"
  }
]
```

---

### 5. Accept a Bid
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/accept \
  -H "X-User-Id: FARMER_USER_ID"
```

**Expected Response (Bid status changes to ACCEPTED):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "MERCHANT_USER_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 250,
  "message": "Can collect tomorrow morning",
  "status": "ACCEPTED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T14:00:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 6. Reject a Bid
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1002/reject \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "bidId": "BID1002",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "ANOTHER_MERCHANT_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 140,
  "quantity": 200,
  "message": "Quick delivery available",
  "status": "REJECTED",
  "createdAt": "2026-05-27T11:00:00Z",
  "updatedAt": "2026-05-27T14:30:00Z",
  "expiresAt": "2026-06-03T11:00:00Z"
}
```

---

### 7. Send Counter Offer
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/counter \
  -H "Content-Type: application/json" \
  -d '{
    "counterPrice": 150,
    "message": "Can you accept 150 per unit instead?"
  }'
```

**Expected Response (Status becomes COUNTERED, price updated):**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "MERCHANT_USER_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 150,
  "quantity": 250,
  "message": "Can you accept 150 per unit instead?",
  "status": "COUNTERED",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T15:00:00Z",
  "expiresAt": "2026-05-30T15:00:00Z"
}
```

---

### 8. Withdraw a Bid
```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/withdraw \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "bidId": "BID1001",
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "bidderId": "MERCHANT_USER_ID",
  "bidderType": "MERCHANT",
  "pricePerUnit": 145,
  "quantity": 250,
  "message": "Can collect tomorrow morning",
  "status": "WITHDRAWN",
  "createdAt": "2026-05-27T10:30:00Z",
  "updatedAt": "2026-05-27T15:30:00Z",
  "expiresAt": "2026-06-03T10:30:00Z"
}
```

---

### 9. Get Bids by Bidder
```bash
curl -X GET http://localhost:8080/api/v1/bids/bidder/MERCHANT_USER_ID \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
[
  {
    "bidId": "BID1001",
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "bidderId": "MERCHANT_USER_ID",
    "bidderType": "MERCHANT",
    "pricePerUnit": 145,
    "quantity": 250,
    "message": "Can collect tomorrow morning",
    "status": "ACCEPTED",
    "createdAt": "2026-05-27T10:30:00Z",
    "updatedAt": "2026-05-27T14:00:00Z",
    "expiresAt": "2026-06-03T10:30:00Z"
  }
]
```

---

### 10. Get Requirement Bids
```bash
curl -X GET http://localhost:8080/api/v1/bids/requirement/REQ2001 \
  -H "Content-Type: application/json"
```

---

## Test Scenarios

### Scenario 1: Complete Bid Lifecycle
1. Merchant places bid on farmer's harvest
2. Farmer accepts the bid
3. Other merchant's bids are auto-rejected
4. Verify bid status is ACCEPTED

```bash
# Step 1: Place bid
BID_ID=$(curl -s -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: MERCHANT_USER_ID" \
  -d '{"targetType":"HARVEST","targetId":"HAR1001","pricePerUnit":145,"quantity":250,"message":"test"}' | jq -r '.bidId')

# Step 2: Accept bid
curl -X POST http://localhost:8080/api/v1/bids/$BID_ID/accept \
  -H "X-User-Id: FARMER_USER_ID"

# Step 3: Verify status
curl -X GET http://localhost:8080/api/v1/bids/$BID_ID
```

### Scenario 2: Counter Offer Negotiation
1. Farmer sends counter offer to merchant bid
2. Verify price is updated
3. Verify expiration is reset to 3 days

```bash
curl -X POST http://localhost:8080/api/v1/bids/BID1001/counter \
  -H "Content-Type: application/json" \
  -d '{"counterPrice":150,"message":"Counter offer from farmer"}'
```

### Scenario 3: Validation - Cannot Bid on Own Listing
1. Farmer tries to bid on their own harvest
2. Should receive 400 Bad Request error

```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: FARMER_USER_ID" \
  -d '{"targetType":"HARVEST","targetId":"HAR1001","pricePerUnit":145,"quantity":250,"message":"test"}'
```

Expected Error:
```json
{
  "error": "Cannot bid on own harvest listing",
  "timestamp": 1685162400000
}
```

---

## Database Verification Queries

### Check All Bids
```javascript
db.bids.find({}).pretty()
```

### Check Pending Bids for a Harvest
```javascript
db.bids.find({
  targetType: "HARVEST",
  targetId: "HAR1001",
  status: "PENDING"
}).pretty()
```

### Check Accepted Bids
```javascript
db.bids.find({ status: "ACCEPTED" }).pretty()
```

### Check Bids by Bidder
```javascript
db.bids.find({ bidderId: "MERCHANT_USER_ID" }).pretty()
```

### Check Bid Counts by Status
```javascript
db.bids.aggregate([
  { $group: { _id: "$status", count: { $sum: 1 } } }
]).pretty()
```

---

## Performance Testing

### Load Test - Place 100 Bids
```bash
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/v1/bids \
    -H "Content-Type: application/json" \
    -H "X-User-Id: MERCHANT_USER_ID_$i" \
    -d '{
      "targetType": "HARVEST",
      "targetId": "HAR1001",
      "pricePerUnit": 145,
      "quantity": 10,
      "message": "Bid '$i'"
    }' &
done
wait
```

### Bulk Query Test
```bash
# Get all bids for a harvest
time curl -X GET http://localhost:8080/api/v1/bids/harvest/HAR1001
```

---

## Troubleshooting Tests

### Test: Bid on Non-Existent Harvest
```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: MERCHANT_USER_ID" \
  -d '{
    "targetType": "HARVEST",
    "targetId": "NONEXISTENT",
    "pricePerUnit": 145,
    "quantity": 250,
    "message": "test"
  }'
```

Expected: `400 Bad Request` with message "Harvest not found"

### Test: Exceed Quantity
```bash
curl -X POST http://localhost:8080/api/v1/bids \
  -H "Content-Type: application/json" \
  -H "X-User-Id: MERCHANT_USER_ID" \
  -d '{
    "targetType": "HARVEST",
    "targetId": "HAR1001",
    "pricePerUnit": 145,
    "quantity": 1000,
    "message": "test"
  }'
```

Expected: `400 Bad Request` with message "Bid quantity exceeds available harvest quantity"

---

## Integration with Postman

Import these endpoints into Postman for testing:

1. Create a new Postman Collection
2. Add a new Environment with variables:
   - `base_url`: http://localhost:8080
   - `farmer_user_id`: FARMER_USER_ID
   - `merchant_user_id`: MERCHANT_USER_ID
3. Import the provided cURL examples as requests

Example Postman request:
```
Method: POST
URL: {{base_url}}/api/v1/bids
Headers: 
  - Content-Type: application/json
  - X-User-Id: {{merchant_user_id}}
Body:
{
  "targetType": "HARVEST",
  "targetId": "HAR1001",
  "pricePerUnit": 145,
  "quantity": 250,
  "message": "Can collect tomorrow"
}
```
