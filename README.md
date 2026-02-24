# Real Estate DX - Property Analysis Platform

Upload PDF property documents and extract structured real estate information using LLM.

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Frontend | Angular 18, Tailwind CSS            |
| Backend  | Spring Boot 3.5, Java 17, MongoDB   |
| Database | MongoDB 7.0                         |
| LLM      | External API (AWS Lambda)           |

## Project Structure

```
realestate_dx/
├── backend/          # Spring Boot REST API
├── frontend/         # Angular SPA
├── docker-compose.yml
└── mock.json         # Property field template
```

## Prerequisites

- **Java 17** (for backend)
- **Node.js 20+** & **npm** (for frontend)
- **MongoDB 7.0** (local or Docker)
- **Maven 3.9+** (or use included mvnw)

## Quick Start (Docker)

Start all services with one command:

```bash
docker-compose up --build
```

| Service  | URL                    |
|----------|------------------------|
| Frontend | http://localhost:4200   |
| Backend  | http://localhost:8080   |
| MongoDB  | localhost:27017         |

## Local Development

### 1. Start MongoDB

```bash
docker run -d --name realestate-mongo \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=rootpassword \
  -e MONGO_INITDB_DATABASE=realestate_dx \
  mongo:7.0
```

### 2. Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on **http://localhost:8080**.

### 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs on **http://localhost:4200** with API proxy to backend.

## Default Accounts

| Username | Password   | Role       |
|----------|------------|------------|
| admin    | admin123   | ROLE_ADMIN |
| user1    | abcxyz@1   | ROLE_USER  |
| user2    | abc123@2   | ROLE_USER  |

## Configuration

Key settings in `backend/src/main/resources/application.properties`:

```properties
# LLM mode: true = mock data, false = real API
app.llm.mock=false

# LLM API endpoint
app.llm.api-url=https://your-llm-endpoint/api/v1/chat

# Callback URL for LLM results
app.callback.base-url=http://localhost:8080
```

## API Endpoints

| Method | Endpoint                        | Description                   |
|--------|---------------------------------|-------------------------------|
| POST   | `/api/extract`                  | Upload PDF for extraction     |
| GET    | `/api/sessions?days=7`          | List sessions (optional filter) |
| GET    | `/api/sessions/{id}`            | Get session with messages     |
| POST   | `/api/sessions/{id}/messages`   | Send chat message to LLM     |
| GET    | `/api/sessions/{id}/property`   | Get extracted property details|
| POST   | `/api/callback/property`        | LLM callback (internal)      |
