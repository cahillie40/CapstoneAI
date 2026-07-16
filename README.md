# CapstoneAI

CapstoneAI is a full-stack football analytics application built with **Spring Boot**, **MySQL**, **Angular**, and **Tribuo**.  
It is designed to help explore **player performance prediction**, **trend evaluation**, and **data-driven squad analysis** using football statistics and advanced performance metrics.

## Project Overview

The main goal of the project is to support the prediction of whether a football player's performance is likely to improve or decline, while also providing a usable dashboard for:
- storing and managing player records
- importing player datasets from CSV
- running performance predictions
- saving prediction history
- visualising player data
- experimenting with Tribuo-based model training and evaluation

The system combines:
- a **Java / Spring Boot backend** for REST APIs and business logic
- an **Angular frontend** for the dashboard and user workflows
- a **MySQL database** for persistent storage
- **Tribuo** for machine learning experimentation and evaluation

---

## Main Features

### Player Management
- Add new players
- Edit player records
- View player details
- Filter and search players
- Paginated player tables

### Data Ingestion
- CSV import for player records
- Database-backed player storage

### Prediction Workflow
- Run performance predictions for selected players
- Show predicted form rating
- Display explanation factors and score breakdown
- Save prediction results to history

### Dashboard & Visualisation
- Dashboard summary statistics
- Charts and comparison views
- Prediction history page

### Tribuo Machine Learning Features
- Tribuo Training page
- Tribuo Evaluation page
- Database-backed player data for training/evaluation workflows
- Experimental model metrics and player trend analysis

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Tribuo

### Frontend
- Angular 22
- TypeScript
- RxJS
- Chart.js
- ng2-charts

### Tooling
- Docker / Docker Compose
- GitHub
- IntelliJ IDEA

---

## Project Structure

```text
CapstoneAI/
├── frontend/      # Angular frontend
├── playerai/      # Spring Boot backend
├── docker-compose.yml
└── README.md
```

### Backend folder
`playerai/`
- REST controllers
- service layer
- repositories
- DTOs
- entities
- Tribuo-related services

### Frontend folder
`frontend/`
- Angular pages/components
- services
- models
- charts/dashboard UI
- prediction and training/evaluation screens

---

## Key Functional Areas

### 1. Player Data Layer
Stores football player records including:
- name
- age
- team
- position
- goals
- assists
- minutes played
- injury status
- pass accuracy
- form rating
- expected goals / expected assists
- advanced metrics such as key passes, progressive passes, interceptions, ball recoveries, and recent match load

### 2. Prediction Layer
Provides:
- rule-based / scoring-based prediction workflows
- prediction explanation and factor breakdown
- prediction history persistence

### 3. Tribuo ML Layer
Provides:
- model training workflows
- evaluation workflows
- machine learning experimentation using player records from the database

### 4. Dashboard Layer
Provides:
- aggregate metrics
- visual summaries
- player comparison and history views

---

## Running the Project

## Option 1: Docker Compose

### Start everything
```bash
docker compose up --build
```

### Stop containers
```bash
docker compose down
```

### Rebuild backend without cache
```bash
docker compose build --no-cache backend
docker compose up
```

---
### Import players - Data for the UI
go to import CSV UI and select file in -- src/main/resources/CSV/players-statsbomb-template-50-players.csv
this will upload 50 players into the database and the UI.


## Option 2: Run locally

### Backend
From the `playerai` folder:

```bash
./mvnw spring-boot:run
```

or on Windows:

```bash
mvnw spring-boot:run
```

### Frontend
From the `frontend` folder:

```bash
npm install
ng serve
```

Frontend URL:

```text
http://localhost:4200/
```

---

## Database Access

Example MySQL access inside Docker:

```bash
docker exec -it playerai-mysql mysql -u appuser -p
```

Password:

```text
apppass
```

Then:

```sql
USE player_ai;
SHOW TABLES;
SELECT * FROM players;
```

---

## Example User Workflow

1. Import player data using CSV or create players manually.
2. Browse and filter players in the Players page.
3. Open the prediction form and select a player.
4. Run a performance prediction.
5. Save the prediction result to history.
6. Review charts, dashboard summaries, and historical predictions.
7. Use Tribuo Training and Tribuo Evaluation pages to explore ML-based workflows.

---

## Current Status

This project is already functioning as a substantial full-stack capstone system with:
- CRUD operations for players
- CSV ingestion
- prediction history
- dashboard pages
- Tribuo training and evaluation views
- REST API integration between frontend and backend

### Areas still being improved
- stronger machine learning evaluation
- clearer improvement/decline temporal logic
- expanded testing and validation
- better UI consistency

---

## API Overview

Example backend areas include:

### Players
- `GET /players`
- `GET /players/{id}`
- `POST /players`
- `PUT /players/{id}`
- `DELETE /players/{id}`
- `GET /players/search`

### Predictions
- `POST /predictions/form-rating`
- `POST /predictions/save`
- `GET /predictions/history`
- `GET /predictions/history/paged`
- `GET /predictions/history/{playerId}`

### CSV Import
- `POST /import/players/csv`

### Tribuo
- training and evaluation endpoints under the Tribuo backend routes

---

## Suggested Improvements / Future Work

- Add Flyway or Liquibase migrations
- Add stronger backend validation
- Improve test coverage
- Standardise frontend styling across all pages

---

## Development Notes

### Frontend README
Additional Angular CLI details are available in:

```text
frontend/README.md
```

### Backend build file
Backend dependencies and Tribuo configuration are managed in:

```text
playerai/pom.xml
```

---
