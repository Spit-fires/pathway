# Pathway Gateway API

The android gateway exposes a local HTTP API to send SMS and run USSD codes directly from the device.

## Base URL

`http://<PHONE_IP>:8080` (or configured port)

## Authentication

All requests must include the **API Key** configured in the app.
HEADER: `Authorization: Bearer <YOUR_API_KEY>`

---

## 1. Send SMS

Sends a text message. Supports standard and Unicode (Bangla) characters. Long messages are automatically handled.

**POST** `/sms`

### Request Body

```json
{
	"number": "+8801700000000",
	"message": "Hello World or বাংলা মেসেজ"
}
```

_Note: The `sim` parameter is deprecated. The app uses the globally configured default SIM._

### Response

```json
{
	"status": "sent" // or "failed"
}
```

---

## 2. Run USSD

Executes a USSD code and returns the response.

**POST** `/ussd`

### Request Body

```json
{
	"code": "*123#"
}
```

### Response

```json
{
	"status": "success",
	"result": "Your Balance is 50.00 Tk."
}
```

---

## Error Handling

Failures return HTTP 200 with a "failed" status in the body to prevent client-side exception throwing in some environments (like PowerShell).

```json
{
	"status": "failed",
	"reason": "Timeout waiting for network"
}
```
