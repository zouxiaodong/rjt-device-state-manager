curl -v -X POST "http://localhost:20000/api/device-event" \
-H "Content-Type: application/json" \
-d '{
"deviceCode": "0869446076986939",
"evtType": 1,
"time": "2025-03-25 21:30:05"
}'