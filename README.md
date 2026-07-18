# CapstoneAI

CapstoneAI is a full-stack football analytics project built with **Spring Boot**, **Angular**, **MySQL**, and **Tribuo**.

The aim of the project is to store football player data, generate prediction-based performance insights, and provide a dashboard for reviewing players, predictions, and machine learning-related outputs.

## What the project does

The application allows users to:
- create and manage player records
- import player data from CSV files
- run player performance predictions
- save prediction results to history
- view dashboard summaries and charts
- use Tribuo-based training, prediction, and evaluation features

## Tech stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Tribuo

### Frontend
- Angular
- TypeScript
- RxJS
- Chart.js
- ng2-charts

### Other tools
- Docker / Docker Compose
- GitHub

## Project structure

```text
CapstoneAI/
├── frontend/            # Angular frontend
├── playerai/            # Spring Boot backend
├── docker-compose.yml   # container setup
└── README.md
```

## Running the project

### Option 1: Docker Compose

From the project root, run:

```bash
docker compose up --build
```

This starts the frontend, backend, and database containers.

To stop the containers:

```bash
docker compose down
```

### Option 2: Run locally

#### Backend
From the `playerai` folder:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw spring-boot:run
```

You can also build the jar and run it directly:

```bash
mvn clean package
java -jar target/playerai-0.0.1-SNAPSHOT.jar
```

#### Frontend
From the `frontend` folder:

```bash
npm install
ng serve
```

The frontend usually runs at:

```text
http://localhost:4200/
```

## Database access

Example MySQL access inside Docker:

```bash
docker exec -it playerai-mysql mysql -u appuser -p
```

Password:

```text
apppass
```

Then you can run:

```sql
USE player_ai;
SHOW TABLES;
SELECT * FROM players;
```

## Importing sample player data

A sample CSV file is included in the project:

```text
src/main/resources/CSV/players-statsbomb-template-50-players.csv
```

To use it:
1. Open the CSV import page in the frontend
2. Select the sample file
3. Upload it

This will load 50 player records into the system.

## Main features

### Player management
- add new players
- edit player details
- view individual player records
- search and filter players
- browse paginated player tables

### CSV support
- import player records from CSV
- export prediction history as CSV

### Prediction workflow
- generate player performance predictions
- view prediction scores and explanations
- save predictions to history

### Dashboard and visualisation
- dashboard summary statistics
- charts and player comparisons
- prediction history views

### Tribuo features
- training page
- evaluation page
- prediction page
- model-related summaries based on stored player data

## Example workflow

A typical workflow in the app is:

1. Import player data from CSV or add players manually
2. Browse and filter player records
3. Open the prediction page
4. Run a prediction for a player
5. Save the prediction result
6. Review prediction history and dashboard views
7. Use the Tribuo training and evaluation pages if needed

## API overview

Some of the main backend routes include:

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

### CSV import
- `POST /import/players/csv`

### Tribuo
- training, evaluation, prediction, and history routes under `/ml/tribuo`

## Notes

This project was built as a capstone-style full-stack application, so the main focus was on building a working end-to-end system rather than only training a standalone machine learning model.

Areas that could still be improved include:
- stronger model benchmarking
- more detailed validation
- cleaner UI consistency across all pages
- additional automated testing

## Useful files

Frontend-specific notes are available in:

```text
frontend/README.md
```

Backend dependencies and configuration are defined in:

```text
playerai/pom.xml
```