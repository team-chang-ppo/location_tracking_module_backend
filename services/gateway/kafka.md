# 카프카 이벤트 스펙 예시
## api-usage-trace
gateway에서 발생하는 api 사용 이벤트
```json
{
  "eventId": "01814a38-50ed-4623-be8e-f995f82c0404",
  "requestTime": "2024-05-17T12:44:27.473Z",
  "responseTime": "2024-05-17T12:44:27.485Z",
  "requestProtocol": "http",
  "requestMethod": "GET",
  "requestUri": "/api/aggregation/v1/ping",
  "responseStatus": 200,
  "clientIp": "111.111.111.111",
  "clientAgent": "PostmanRuntime/7.39.0",
  "apiKey": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNRU1CRVJfSUQiOjEsIkdSQURFX1RZUEUiOiJHUkFERV9DTEFTU0lDIiwiSUQiOjF9.kJ5120x4Rd3FWeI5T3p8wacjvyX2sQDouSNrmZy-FSQ",
  "apiKeyId": 1,
  "memberId": 1,
  "memberGrade": "GRADE_CLASSIC",
  "traceId": "c9bd5893-8791-424c-990d-8c20b62a0318"
}
```

## hourly-api-usage
집계된 카운트 결과
```json
{
  "requestUri": "/api/aggregation/v1/ping",
  "requestMethod": "GET",
  "memberId": 1,
  "apiKeyId": 1,
  "date": "2024-05-18",
  "window": {
    "start": "2024-05-18T04:08:00.000+09:00",
    "end": "2024-05-18T04:10:00.000+09:00"
  },
  "count": 14
}
```