# rfq-to-quote-automation-poc

Problem
Target Customer
Current Pain
Proposed Solution
Architecture
Business Workflow
Business Rules
API
Roadmap



Yes. For tonight, **don't write a huge README**. Your README should explain the business problem and what the POC is proving.

Use this as your **Version 1 README**. You can paste it directly into `README.md`.

# AI RFQ & Quote Automation

A B2B procurement automation platform designed to reduce the manual effort involved in processing **Requests for Quotation (RFQs), product pricing, quotation generation, and approval workflows**.

The initial version focuses on building a reliable backend foundation. AI and workflow automation will be introduced in later phases.

---

## 1. Problem

Many businesses receive RFQs through email, PDF, Excel files, or other channels.

A typical process looks like:

```text
Customer sends RFQ
        ↓
Sales/Procurement team reads RFQ
        ↓
Products and quantities are identified
        ↓
Prices are searched manually
        ↓
Quotation is prepared
        ↓
Quotation is reviewed
        ↓
Quotation is sent to customer
```

This process can become slow and error-prone when companies handle a large number of RFQs.

Common problems include:

* Manual data entry
* Searching product prices manually
* Repeated quotation preparation
* Pricing errors
* Lack of workflow visibility
* Difficulty tracking quotation history
* Slow response to customers

---

## 2. Proposed Solution

Build a backend platform that manages the complete RFQ-to-Quote workflow.

The initial system will support:

```text
RFQ
 ↓
RFQ Items
 ↓
Product Catalog
 ↓
Price List
 ↓
Pricing Rules
 ↓
Quote Generation
 ↓
Quote Items
 ↓
Workflow Events
```

Future versions will support:

```text
Email / PDF / Excel RFQ
        ↓
AI Extraction
        ↓
Structured RFQ
        ↓
Product Matching
        ↓
Price Selection
        ↓
Quote Generation
        ↓
Approval Workflow
        ↓
Email / ERP / API
```

---

## 3. Target Customers

The initial target customers are B2B companies that regularly receive and process RFQs.

Potential segments include:

* Distributors
* Wholesalers
* Manufacturing companies
* Industrial suppliers
* IT hardware/software resellers
* Electrical equipment suppliers
* Procurement teams
* B2B trading companies

The exact initial customer segment will be validated through customer interviews and pilot discussions.

---

## 4. POC Objective

The objective of this POC is **not** to build a complete SaaS product.

The objective is to prove that the backend can reliably perform:

1. Create an RFQ
2. Add RFQ items
3. Maintain a product catalog
4. Maintain product pricing
5. Generate a quotation
6. Calculate quotation totals
7. Record workflow events
8. Retrieve RFQ and quotation information

---

## 5. Core Business Workflow

### RFQ Creation

A customer request is represented as an RFQ.

```text
RFQ
 ├── Customer
 ├── RFQ Number
 ├── Status
 ├── Currency
 ├── Created Date
 └── Items
```

### RFQ Items

Each RFQ contains one or more requested products.

```text
RFQ Item
 ├── Product
 ├── Quantity
 └── Requested Specifications
```

### Product Catalog

The product catalog contains products that can be quoted.

```text
Product
 ├── SKU
 ├── Name
 ├── Description
 ├── Category
 └── Unit
```

### Price List

The pricing system stores prices for products.

```text
Price List
 ├── Product
 ├── Unit Price
 ├── Currency
 ├── Effective From
 └── Effective To
```

### Quote

A quote is generated from an RFQ using applicable product pricing.

```text
Quote
 ├── Quote Number
 ├── RFQ
 ├── Status
 ├── Subtotal
 ├── Tax
 ├── Total
 └── Quote Items
```

---

## 6. Initial Business Rules

The first version will enforce rules such as:

* An RFQ must contain at least one item.
* RFQ item quantity must be greater than zero.
* Every quoted product must exist in the product catalog.
* A valid price must exist before generating a quote.
* Quote subtotal is calculated from quantity × unit price.
* Tax is calculated separately.
* Quote total = subtotal + applicable tax.
* Important state changes are recorded as workflow events.
* Invalid RFQs cannot generate quotations.

These rules will evolve as customer requirements are discovered.

---

## 7. Initial Database Model

The initial database will contain:

```text
customer
supplier
rfq
rfq_item
product_catalog
price_list
price_list_item
quote
quote_item
workflow_event
```

High-level relationship:

```text
Customer
   │
   └── RFQ
        │
        └── RFQ Item
              │
              └── Product
                    │
                    └── Price List

RFQ
 │
 └── Quote
      │
      └── Quote Item

RFQ / Quote
      │
      └── Workflow Event
```

---

## 8. Initial API

### Create RFQ

```http
POST /api/v1/rfqs
```

### Get RFQ

```http
GET /api/v1/rfqs/{id}
```

### Add RFQ Item

```http
POST /api/v1/rfqs/{id}/items
```

### Generate Quote

```http
POST /api/v1/rfqs/{id}/generate-quote
```

### Get Quote

```http
GET /api/v1/quotes/{id}
```

---

## 9. Example Workflow

Example RFQ:

```text
Customer:
ABC Manufacturing

Requested Products:

1. Industrial Sensor
   Quantity: 100

2. Control Module
   Quantity: 20

3. Power Supply
   Quantity: 50
```

The system should:

```text
Create RFQ
    ↓
Add RFQ Items
    ↓
Find Products
    ↓
Find Applicable Prices
    ↓
Calculate Item Prices
    ↓
Generate Quote
    ↓
Calculate Subtotal
    ↓
Calculate Tax
    ↓
Calculate Total
    ↓
Store Workflow Event
```

---

## 10. Technology Stack

### Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* Bean Validation

### Database

* PostgreSQL
* Flyway

### Development

* Git
* GitHub
* Maven
* Docker (later)

### Future

* AI/LLM integration
* n8n / Activepieces
* Redis
* Kafka
* Object Storage
* Authentication & Authorization
* ERP integrations
* Email integration
* Observability

---

## 11. Architecture

Initial architecture:

```text
                REST API
                   │
                   ▼
              Controller
                   │
                   ▼
               Service
                   │
                   ▼
              Repository
                   │
                   ▼
              PostgreSQL
```

Business logic should remain in the service/domain layer rather than inside controllers.

Future architecture:

```text
                    ┌───────────────┐
Email / PDF / Excel │ AI Extraction │
        ───────────►│ & Processing  │
                    └───────┬───────┘
                            │
                            ▼
                      RFQ Platform
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
        Product Match   Pricing Engine   Workflow
              │             │             │
              └─────────────┼─────────────┘
                            ▼
                       Quote Engine
                            │
                   ┌────────┴────────┐
                   ▼                 ▼
                 Email              ERP/API
```

---

## 12. Development Roadmap

### Phase 1 — Backend Foundation

* [ ] Spring Boot project
* [ ] PostgreSQL connection
* [ ] Flyway migrations
* [ ] Domain entities
* [ ] DTOs
* [ ] RFQ APIs
* [ ] Product catalog
* [ ] Price lists
* [ ] Quote generation
* [ ] Workflow events
* [ ] Unit/integration tests

### Phase 2 — Real Business Workflow

* [ ] Customer management
* [ ] Supplier management
* [ ] Advanced pricing rules
* [ ] Discounts
* [ ] Tax rules
* [ ] Approval workflow
* [ ] Quote versioning
* [ ] Audit trail

### Phase 3 — Automation

* [ ] Email integration
* [ ] PDF/Excel RFQ ingestion
* [ ] Workflow automation
* [ ] Notifications
* [ ] Automated quote delivery

### Phase 4 — AI

* [ ] RFQ document extraction
* [ ] Product matching
* [ ] Specification matching
* [ ] Pricing assistance
* [ ] Quote generation assistance
* [ ] Human approval for uncertain results

### Phase 5 — Production SaaS

* [ ] Authentication
* [ ] Multi-tenancy
* [ ] Role-based access
* [ ] Billing
* [ ] Monitoring
* [ ] Security
* [ ] Deployment
* [ ] Customer onboarding

---

## 13. What This POC Is NOT

The initial POC will intentionally exclude:

* Frontend
* AI
* n8n/Activepieces
* Production deployment
* Complex authentication
* Multi-tenancy
* Advanced analytics

The goal is to first establish a reliable backend business workflow.

---

## 14. Success Criteria

The POC will be considered successful when a complete RFQ can be processed without manually calculating the quotation.

Example:

```text
Create RFQ
    ↓
Add Products
    ↓
Select Applicable Prices
    ↓
Generate Quote
    ↓
Calculate:
    Subtotal
    Tax
    Total
    ↓
Store Quote
    ↓
Record Workflow Events
```

The entire flow should be demonstrable through Swagger/Postman.

---

## 15. Business Validation

Technical development alone does not validate the business.

After the core POC works, the next objective is to validate:

* Who experiences this problem?
* How frequently does it occur?
* How much manual work does it create?
* What errors occur today?
* What existing tools are being used?
* What would a customer pay to reduce this work?
* Which industry has the strongest need?
* Which workflow should become the initial product?

The product direction will be refined based on customer feedback rather than assumptions.

---

## 16. Long-Term Vision

The long-term goal is to build an intelligent B2B quote automation platform that can transform unstructured customer RFQs into accurate, reviewable, and actionable quotations.

```text
Unstructured RFQ
      ↓
Understand
      ↓
Structure
      ↓
Match
      ↓
Price
      ↓
Validate
      ↓
Approve
      ↓
Quote
      ↓
Learn
```

The system should keep humans in control of important pricing and commercial decisions while automating repetitive operational work.

---

## 17. Current Status

**Status:** POC — Backend Foundation

**Current Focus:**

> Build the RFQ → Pricing → Quote workflow before adding AI or automation.
