# portfolio-app-web

Course: Programming 5

Name: Artem Novorosiuk

Email: artem.novorosiuk@student.kdg.be

Group: ACS202

Domain entities: Investor, Stock, BrokerageAccount

# Stock API — Requests & Responses

> All examples assume the app runs at `http://localhost:8081` and the REST routes are under `/api`. Replace IDs/tokens with real values as needed.

---

## Fetching all stocks — OK (200)

**Request**

```
GET /api/stocks HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "symbol": "AAPL",
    "companyName": "Apple Inc",
    "currentPrice": 199.99,
    "sector": "TECHNOLOGY",
    "listedDate": "2020-01-01",
    "imageURL": null
  },
  {
    "id": 5,
    "symbol": "TSLA",
    "companyName": "Tesla, Inc.",
    "currentPrice": 129.50,
    "sector": "AUTOMOTIVE",
    "listedDate": "2019-07-01",
    "imageURL": null
  }
]
```

---

## Fetching all stocks — No Content (204)

**Request**

```
GET /api/stocks HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 204 No Content
```

---

## Searching for stocks — OK (200)

**Request**

```
GET /api/stocks?symbol=AA&minPrice=10&maxPrice=100 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 12,
    "symbol": "AA",
    "companyName": "Alcoa Corp.",
    "currentPrice": 32.10,
    "sector": "MATERIALS",
    "listedDate": "2016-11-01",
    "imageURL": null
  }
]
```

---

## Searching for stocks — No Content (204)

**Request**

```
GET /api/stocks?symbol=ZZZ&minPrice=1000&maxPrice=2000 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 204 No Content
```

---

## Fetching one stock — OK (200)

**Request**

```
GET /api/stocks/1 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "symbol": "AAPL",
  "companyName": "Apple Inc",
  "currentPrice": 199.99,
  "sector": "TECHNOLOGY",
  "listedDate": "2020-01-01",
  "imageURL": null
}
```

---

## Fetching one stock — Not Found (404)

**Request**

```
GET /api/stocks/999999 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 404 Not Found
```

---

## Fetching stocks for an account — OK (200)

**Request**

```
GET /api/accounts/1/stocks HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "symbol": "AAPL",
    "companyName": "Apple Inc",
    "currentPrice": 199.99,
    "sector": "TECHNOLOGY",
    "listedDate": "2020-01-01",
    "imageURL": null
  },
  {
    "id": 5,
    "symbol": "TSLA",
    "companyName": "Tesla, Inc.",
    "currentPrice": 129.50,
    "sector": "AUTOMOTIVE",
    "listedDate": "2019-07-01",
    "imageURL": null
  }
]
```

---

## Fetching stocks for an account — No Content (204)

**Request**

```
GET /api/accounts/42/stocks HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 204 No Content
```

---

## Fetching available stocks for an account — OK (200)

**Request**

```
GET /api/accounts/1/stocks/available HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 12,
    "symbol": "AA",
    "companyName": "Alcoa Corp.",
    "currentPrice": 32.10,
    "sector": "MATERIALS",
    "listedDate": "2016-11-01",
    "imageURL": null
  }
]
```

---

## Fetching available stocks for an account — No Content (204)

**Request**

```
GET /api/accounts/1/stocks/available HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response**

```
HTTP/1.1 204 No Content
```

---

## Deleting a stock — No Content (204)

**Request**

```
DELETE /api/stocks/5 HTTP/1.1
Host: localhost:8081
Accept: application/json
Cookie: JSESSIONID=YOUR_SESSION_ID
X-CSRF-TOKEN: YOUR_CSRF_TOKEN
```

**Response**

```
HTTP/1.1 204 No Content
```

---

## Deleting a stock — Not Found (404)

**Request**

```
DELETE /api/stocks/999999 HTTP/1.1
Host: localhost:8081
Accept: application/json
Cookie: JSESSIONID=YOUR_SESSION_ID
X-CSRF-TOKEN: YOUR_CSRF_TOKEN
```

**Response**

```
HTTP/1.1 404 Not Found
```

