# Security Operations Dashboard

A defensive, local-first dashboard for reviewing security alerts, asset exposure, and operational risk. It includes a Spring Boot API, a responsive browser UI, authentication, and a deterministic sample data set. It does not scan networks, contact external threat feeds, or perform actions against real infrastructure.

## Requirements

- Java 17+
- Maven 3.9+

## Run locally

Set a unique username and a strong password in your shell. Credentials are required; the app intentionally has no built-in default account.

```powershell
$env:DASHBOARD_USERNAME = "analyst"
$env:DASHBOARD_PASSWORD = "replace-with-a-long-unique-password"
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) and sign in with those credentials. To run the packaged application:

```powershell
mvn clean package
java -jar target/security-operations-dashboard-1.0.0.jar
```

## Features

- Authenticated dashboard with summary counts and severity distribution
- Searchable and filterable alert queue
- Alert acknowledgement workflow with state validation
- Asset inventory with risk score, exposure, and patch status
- JSON API protected by the same authentication and CSRF controls as the UI
- Responsive layout, accessible status labels, and safe text rendering
- Deterministic, fictional sample data for offline demonstrations

## API

All API routes require authentication (browser form session or HTTP Basic). Mutating requests also require a CSRF token. The dashboard UI obtains the token from its rendered page and sends it in the `X-XSRF-TOKEN` header.

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/api/dashboard` | Summary metrics and alert counts by severity |
| `GET` | `/api/alerts` | Alert list; optional `severity`, `status`, and `search` filters |
| `POST` | `/api/alerts/{id}/acknowledge` | Acknowledge an open alert |
| `GET` | `/api/assets` | Asset inventory ordered by risk |

The in-memory data resets whenever the application restarts. Before connecting production data sources, add organization-approved authorization roles, audit logging, durable storage, secrets management, and operational monitoring. Do not expose the demo deployment to the public internet.

## Verify

```powershell
mvn test
```
