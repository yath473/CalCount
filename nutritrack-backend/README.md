# NutriTrack Backend — Java Spring Boot

REST API backend for the NutriTrack calorie tracking web application.

---

## Tech Stack

| Layer       | Technology               |
|-------------|--------------------------|
| Language    | Java 17                  |
| Framework   | Spring Boot 3.2          |
| ORM         | Spring Data JPA / Hibernate |
| Database    | H2 (dev) / PostgreSQL (prod) |
| Build tool  | Maven                    |
| Validation  | Jakarta Bean Validation  |

---

## Project Structure

```
src/main/java/com/nutritrack/
├── NutriTrackApplication.java     ← Main entry point
├── controller/
│   ├── PatientController.java     ← Patient profile endpoints
│   ├── FoodController.java        ← Food database & search endpoints
│   ├── DailyLogController.java    ← Meal logging, weekly view, streak
│   └── NutritionGoalController.java ← Goals & notification prefs
├── service/
│   ├── PatientService.java        ← Patient business logic + BMI calc
│   ├── FoodService.java           ← Food search & filter logic
│   ├── DailyLogService.java       ← Core logging, streak, notifications
│   └── NutritionGoalService.java  ← Goal presets & custom goals
├── model/
│   ├── Patient.java               ← Patient entity (demographics, conditions)
│   ├── Food.java                  ← Food entity (macros, dietary flags)
│   ├── DailyLog.java              ← One day of eating for a patient
│   ├── MealEntry.java             ← Single food item in a meal
│   └── NutritionGoal.java         ← Daily nutrition targets
├── repository/
│   └── Repositories.java          ← JPA repositories for all entities
└── config/
    ├── DataSeeder.java             ← Seeds 75+ foods on startup
    ├── WebConfig.java              ← CORS configuration
    └── GlobalExceptionHandler.java ← Clean JSON error responses
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Run in development (H2 in-memory DB)
```bash
mvn spring-boot:run
```
Server starts at: http://localhost:8080  
H2 Console: http://localhost:8080/h2-console

### Build a JAR
```bash
mvn clean package
java -jar target/nutritrack-backend-1.0.0.jar
```

### Switch to PostgreSQL (production)
1. Edit `src/main/resources/application.properties`
2. Comment out the H2 lines, uncomment the PostgreSQL lines
3. Fill in your DB host, username, and password
4. Change dialect to `org.hibernate.dialect.PostgreSQLDialect`

---

## API Reference

### Patients — `/api/patients`

| Method | Endpoint                          | Description                        |
|--------|-----------------------------------|------------------------------------|
| POST   | `/api/patients`                   | Create a new patient profile       |
| GET    | `/api/patients`                   | Get all patients                   |
| GET    | `/api/patients/{id}`              | Get patient by ID                  |
| PUT    | `/api/patients/{id}`              | Update patient profile             |
| DELETE | `/api/patients/{id}`              | Delete patient                     |
| GET    | `/api/patients/{id}/bmi`          | Get BMI summary                    |
| GET    | `/api/patients/condition/{name}`  | Find patients by medical condition |

### Foods — `/api/foods`

| Method | Endpoint                        | Description                             |
|--------|---------------------------------|-----------------------------------------|
| GET    | `/api/foods`                    | All foods (add `?query=` or `?category=`) |
| GET    | `/api/foods/{id}`               | Get food by ID                          |
| GET    | `/api/foods/categories`         | All cuisine categories                  |
| GET    | `/api/foods/vegetarian`         | Vegetarian foods                        |
| GET    | `/api/foods/vegan`              | Vegan foods                             |
| GET    | `/api/foods/gluten-free`        | Gluten-free foods                       |
| GET    | `/api/foods/halal`              | Halal foods                             |
| GET    | `/api/foods/low-calorie?max=300`| Foods under calorie threshold           |
| GET    | `/api/foods/high-protein?min=20`| High-protein foods                      |
| POST   | `/api/foods`                    | Add food to database                    |
| PUT    | `/api/foods/{id}`               | Update food                             |
| DELETE | `/api/foods/{id}`               | Delete food                             |

### Daily Logs — `/api/logs`

| Method | Endpoint                               | Description                        |
|--------|----------------------------------------|------------------------------------|
| GET    | `/api/logs/{patientId}/today`          | Get or create today's log          |
| GET    | `/api/logs/{patientId}/date/{date}`    | Get log for a specific date        |
| GET    | `/api/logs/{patientId}/all`            | All logs for patient               |
| POST   | `/api/logs/{patientId}/entries`        | Add food to a meal                 |
| DELETE | `/api/logs/{patientId}/entries/{id}`   | Remove food from meal              |
| GET    | `/api/logs/{patientId}/week`           | 7-day weekly summary + streak      |
| GET    | `/api/logs/{patientId}/range`          | Logs for custom date range         |
| GET    | `/api/logs/{patientId}/streak`         | Current streak count               |
| GET    | `/api/logs/{patientId}/notifications`  | Active alerts for today            |

#### Add meal entry — request body:
```json
{
  "foodId": 3,
  "mealType": "LUNCH",
  "servings": 1.5
}
```
Valid mealType values: `BREAKFAST`, `MORNING_SNACK`, `LUNCH`, `AFTERNOON_SNACK`, `DINNER`

### Goals — `/api/goals`

| Method | Endpoint                              | Description                     |
|--------|---------------------------------------|---------------------------------|
| GET    | `/api/goals/{patientId}`              | Get current goal                |
| POST   | `/api/goals/{patientId}/preset/{type}`| Apply preset goal               |
| PUT    | `/api/goals/{patientId}`              | Save custom goal                |

Valid preset types: `LOSE_WEIGHT`, `MAINTAIN`, `GAIN_WEIGHT`, `MUSCLE_BUILDING`

---

## Connecting Frontend to Backend

In your `index.html`, replace localStorage calls with `fetch()` calls to this API.

Example — load today's log:
```javascript
const BASE_URL = 'http://localhost:8080'; // or your deployed backend URL

async function loadTodayLog(patientId) {
  const res = await fetch(`${BASE_URL}/api/logs/${patientId}/today`);
  const log = await res.json();
  // update UI with log.totalCalories, log.mealEntries, etc.
}
```

Example — add a food:
```javascript
async function addFood(patientId, foodId, mealType, servings) {
  await fetch(`${BASE_URL}/api/logs/${patientId}/entries`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ foodId, mealType, servings })
  });
}
```
