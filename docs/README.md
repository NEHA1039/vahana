# Vahana — Vehicle Tracking System

A full-stack system that ingests real-time GPS location data from vehicles,
validates and stores it, and exposes it via REST APIs for a live dashboard.

## Stack
- Backend: Java 17, Spring Boot 3, Spring Data JPA/Hibernate
- Database: MySQL
- Frontend: React (Vite)
- Simulator: Python (`requests`) — makes the demo look real-time

## Project structure
```
backend/    Spring Boot app (entities, repositories, service, controller)
frontend/   React app (Vite)
database/   schema.sql (source of truth for the schema + seed data)
simulator/  simulate_gps.py — sends fake GPS pings to the running backend
docs/       this file
```

## 1. Database setup
Make sure MySQL is running locally, then either:
- let Hibernate create the schema automatically (`ddl-auto: update` is already
  set in `application.yml`), or
- run `database/schema.sql` manually for full control over indexes/seed data:
  ```
  mysql -u root -p < database/schema.sql
  ```

Update `backend/src/main/resources/application.yml` with your actual MySQL
username/password before running.

## 2. Run the backend
```
cd backend
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`.

Quick check:
```
curl http://localhost:8080/api/vehicles
```

## 3. Run the frontend
```
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173` and polls the backend every 4s.

## 4. Run the GPS simulator
With the backend running:
```
cd simulator
pip install requests
python simulate_gps.py
```
This registers 3 demo vehicles (if not already present) and sends location
pings along a route every few seconds — refresh the frontend and watch the
positions update.

## API summary
| Method | Endpoint                          | Purpose                          |
|--------|------------------------------------|-----------------------------------|
| POST   | `/api/vehicles`                    | Register a new vehicle            |
| GET    | `/api/vehicles`                    | List vehicles + latest position   |
| POST   | `/api/vehicles/{id}/location`      | Ingest a GPS ping (validated)     |
| GET    | `/api/vehicles/{id}/history?limit=`| Recent location history           |

## Validation rules (on `/location`)
- latitude ∈ [-90, 90], longitude ∈ [-180, 180]
- timestamp cannot be in the future
- vehicleId must exist, or the request is rejected with a 400 and a clear message

## Design notes
- `location_pings` has a composite index on `(vehicle_id, timestamp)` so that
  fetching a vehicle's recent history stays fast as the table grows — this is
  the main "optimized query performance" piece of the project.
- The service layer (`VehicleService`) keeps validation/business logic out of
  the controller, and marks a vehicle `ACTIVE` automatically the first time it
  reports a location.
