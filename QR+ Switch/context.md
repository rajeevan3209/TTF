# SGQR+ National Switch — Context & Design Document

## 1. Overview

SGQR+ is a **national QR payment switch** that enables interoperability across
multiple QR payment schemes operating in Singapore (and potentially beyond).
Today, QR schemes such as **PayNow QR** and **NETS QR** operate on largely
separate rails. SGQR+ acts as a **common routing and translation layer** that
sits between the merchant/acquirer side and the issuer/scheme side, so that
any participating scheme can accept and settle a QR transaction regardless of
which scheme originated it.

The system is a **national switch**, meaning:
- All inbound QR transactions from any participating acquirer are routed
  through a **single common routing layer** before being delivered to the
  relevant acquirer/issuer switch.
- The switch does **not** hold merchant or consumer funds — it is a routing,
  validation, and translation layer that participants connect to.
- Participants (schemes/banks/PSPs) are onboarded entities with their own
  switches; SGQR+ routes and translates messages between them.

## 2. Goals

1. Enable **interoperability** across QR payment schemes (PayNow QR, NETS QR,
   and future schemes) without merchants/consumers needing scheme-specific
   integrations.
2. Provide a **single national entry point** for inbound QR transaction
   processing, regardless of originating acquirer or scheme.
3. Support **on-us** and **off-us** transaction routing with appropriate fee
   application for off-us (cross-network) transactions.
4. Provide core switch capabilities: **scheme identification**, **participant
   validation**, **transaction routing**, and **message translation**.
5. Be simple to run/demo locally — no external DB dependency (in-memory only).

## 3. Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17+, Spring Boot 3.x |
| API style | REST (JSON), Spring Web |
| Persistence | Spring Data JPA + **H2 in-memory database** |
| Build tool | Maven |
| Frontend | React (Vite), fetch/axios for REST calls |
| Messaging model | Internal DTOs modeled loosely on ISO 20022 / EMVCo QR fields |

The in-memory database (H2) is used to store:
- Participant registry (schemes, acquirers, issuers, status)
- Routing rules / scheme-to-participant mapping
- Transaction log (for audit, fee calculation, and demo dashboards)
- Fee configuration per off-us transaction band

Since H2 is in-memory, data resets on restart — acceptable for a
demo/prototype national switch. A `data.sql` seed script pre-loads sample
participants (PayNow QR, NETS QR, sample banks/PSPs) on startup.

## 4. High-Level Architecture

```
                         ┌───────────────────────────────┐
                         │        Merchant / Acquirer     │
                         │   (presents SGQR+ combo QR)    │
                         └───────────────┬────────────────┘
                                          │ 1. Scan & Pay request
                                          ▼
                         ┌───────────────────────────────┐
                         │        SGQR+ NATIONAL SWITCH    │
                         │  (Spring Boot backend service)  │
                         │                                 │
                         │  ┌───────────────────────────┐  │
                         │  │ A. Scheme Identification   │  │
                         │  └─────────────┬─────────────┘  │
                         │                ▼                │
                         │  ┌───────────────────────────┐  │
                         │  │ B. Participant Validation  │  │
                         │  └─────────────┬─────────────┘  │
                         │                ▼                │
                         │  ┌───────────────────────────┐  │
                         │  │ C. Transaction Routing     │  │
                         │  │   (on-us vs off-us + fee)  │  │
                         │  └─────────────┬─────────────┘  │
                         │                ▼                │
                         │  ┌───────────────────────────┐  │
                         │  │ D. Message Translation     │  │
                         │  └─────────────┬─────────────┘  │
                         └────────────────┼────────────────┘
                                          │ 2. Translated & routed message
                          ┌───────────────┼────────────────┐
                          ▼               ▼                ▼
                 ┌─────────────┐ ┌─────────────┐  ┌─────────────────┐
                 │ PayNow QR   │ │  NETS QR    │  │ Other Acquirer/  │
                 │ Acquirer    │ │  Acquirer   │  │ Issuer Switch    │
                 │ Switch      │ │  Switch     │  │ (extensible)     │
                 └─────────────┘ └─────────────┘  └─────────────────┘
```

All inbound transactions — irrespective of the originating acquirer or the
consumer's issuer — pass through the **same four-stage pipeline** (A→D)
inside the national switch before being dispatched to the destination
acquirer switch. This common routing layer is what gives SGQR+ its
interoperability guarantee: no acquirer talks directly to another acquirer or
issuer switch; everything transits through SGQR+.

## 5. Core Switch Components

### A. Scheme Identification
- Parses the incoming QR payload (EMVCo-style merchant-presented QR fields).
- Identifies which scheme the QR belongs to based on scheme-specific tags
  (e.g., PayNow QR merchant ID prefix, NETS QR merchant ID format, GUID/AID
  fields).
- Determines the **acquirer scheme** (from QR) and the **issuer scheme**
  (from the paying instrument/app) needed later for on-us/off-us
  determination.

### B. Participant Validation
- Confirms both the acquirer (merchant-side) participant and the issuer
  (consumer-side) participant are **active, onboarded members** of SGQR+.
- Validates participant status (ACTIVE / SUSPENDED / TERMINATED), supported
  transaction types, and daily/velocity limits (basic checks for the demo).
- Rejects transactions from unregistered or suspended participants with a
  clear error/response code.

### C. Transaction Routing
- Determines whether the transaction is **on-us** or **off-us**:
  - **On-us**: issuer participant and acquirer participant belong to the
    **same network/scheme** → routed directly, **no interchange fee**.
  - **Off-us**: issuer participant and acquirer participant belong to
    **different networks/schemes** → routed cross-network, and a
    **transaction fee of 0.3%–0.7%** (configurable per participant
    agreement/tier) is applied and recorded against the transaction.
- Looks up the correct destination acquirer switch endpoint via the
  participant/routing registry.
- Computes and logs the applicable fee (if off-us) before dispatch.

### D. Message Translation
- Translates the common internal SGQR+ transaction message into the
  **destination scheme's native message format** (e.g., PayNow QR message
  schema vs NETS QR message schema), since each scheme may have different
  field naming, structure, or protocol expectations.
- Ensures the response from the destination switch is translated back into
  the common SGQR+ response format before being returned to the originating
  acquirer.

## 6. Participant Schemes (initial set)

| Scheme Code | Name | Type |
|---|---|---|
| `PAYNOW_QR` | PayNow QR | Bank-linked, real-time transfer scheme |
| `NETS_QR` | NETS QR | Card/debit-linked acquiring scheme |
| `OTHER_QR` | Extensible placeholder | For future schemes (e.g., GrabPay, regional QR schemes) |

The participant model is designed to be **extensible** — new schemes can be
registered without code changes to the routing/translation pipeline (driven
by configuration/registry data).

## 7. On-us vs Off-us & Fee Model

- **On-us transaction**: issuer and acquirer are part of the *same* network
  (i.e., same scheme/participant network handles both sides). No SGQR+
  transaction fee applies (or a minimal/zero routing fee).
- **Off-us transaction**: issuer and acquirer belong to *different* networks.
  SGQR+ applies a **transaction fee of 0.3%–0.7%** of transaction value,
  charged to the relevant participant (configurable — typically the
  acquiring participant), to compensate for cross-network routing/settlement.
- Fee percentage is **configurable per participant pair or tier**, defaulting
  to a flat rate within the 0.3%–0.7% band for the prototype.
- The fee is **capped at SGD 100 per transaction**, regardless of the
  percentage calculation — large-value off-us transactions never pay more
  than the cap. Both the percentage and the cap are configurable via
  `FeeConfig`.
- All computed fees are persisted against the transaction record for
  reporting/settlement reconciliation.

## 8. Core Domain Entities (in-memory DB)

- **Participant**: id, name, schemeCode, participantType (ACQUIRER/ISSUER/BOTH), status, feeTier
- **Scheme**: code, name, qrIdentifierPattern, messageFormat
- **RoutingRule**: sourceScheme, destinationScheme, isOnUs, feePercentage
- **Transaction**: id, qrPayload, acquirerParticipantId, issuerParticipantId, amount, onUsFlag, feeApplied, feeAmount, status, timestamps
- **FeeConfig**: participantId/tier, feePercentage (within 0.3%–0.7% band), feeCap (SGD, default 100)

## 9. Indicative REST API (backend)

| Endpoint | Purpose |
|---|---|
| `POST /api/transactions/inbound` | Submit an inbound QR transaction for processing (runs full A→D pipeline) |
| `GET /api/participants` | List registered participants |
| `POST /api/participants` | Onboard a new participant |
| `GET /api/schemes` | List registered schemes |
| `GET /api/transactions` | List processed transactions (for dashboard) |
| `GET /api/transactions/{id}` | Get transaction detail incl. routing decision & fee |

## 10. Frontend (React) — Scope

- **Dashboard**: view recent transactions, on-us vs off-us split, fees collected.
- **Participant management**: view/add participants and their scheme.
- **Transaction simulator**: form to simulate an inbound QR scan (choose
  acquirer, issuer, amount) and view the routing/translation result live.

## 11. Non-Functional Notes (prototype scope)

- No real settlement/clearing — this is a routing/orchestration prototype.
- No persistent storage across restarts (by design — in-memory H2).
- Security (mTLS, signing, auth between participants) is out of scope for
  the prototype but noted as a future requirement for a production national
  switch.
