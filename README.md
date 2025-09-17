# Recipe Management API

A RESTful Recipe Management API built with **Play Framework (Scala 3)** following clean architecture principles and REST constraints.

## 🚀 Tech Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Framework** | Play Framework 3.0 (Scala 3) | RESTful web services |
| **Database** | PostgreSQL + JPA/Hibernate | Data persistence |
| **API Spec** | OpenAPI 3.0 | Contract-driven development |
| **Testing** | ScalaTest + Mockito | Unit & integration testing |
| **Build** | SBT | Build automation |
| **Deployment** | Docker + Render | Cloud deployment |

## 📋 Features

- ✅ **Full CRUD Operations** for recipes
- ✅ **REST Compliance** with proper HTTP methods and status codes
- ✅ **HATEOAS** support with navigation links
- ✅ **Caching** with Cache-Control headers
- ✅ **Data Validation** with comprehensive error handling
- ✅ **100% Test Coverage** with unit and integration tests
- ✅ **Production Ready** with environment-based configuration

## 🔗 REST API Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| `GET` | `/recipes` | List all recipes | 200, 500 |
| `POST` | `/recipes` | Create new recipe | 200, 400, 500 |
| `GET` | `/recipes/{id}` | Get recipe by ID | 200, 404, 500 |
| `PATCH` | `/recipes/{id}` | Update recipe | 200, 400, 404, 500 |
| `DELETE` | `/recipes/{id}` | Delete recipe | 200, 404, 500 |

### Sample Request/Response

**POST /recipes**
```json
{
  "title": "Tomato Soup",
  "making_time": "15 min",
  "serves": "5 people",
  "ingredients": "onion, tomato, seasoning, water",
  "cost": 450
}
```

**Response with HATEOAS**
```json
{
  "message": "Recipe successfully created!",
  "recipe": [{
    "id": 1,
    "title": "Tomato Soup",
    "making_time": "15 min",
    "serves": "5 people",
    "ingredients": "onion, tomato, seasoning, water",
    "cost": "450",
    "created_at": "2024-01-01 12:00:00",
    "updated_at": "2024-01-01 12:00:00",
    "_links": {
      "self": {"href": "/recipes/1", "method": "GET"},
      "update": {"href": "/recipes/1", "method": "PATCH"},
      "delete": {"href": "/recipes/1", "method": "DELETE"}
    }
  }]
}
```

## 🏗️ Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Controller  │───▶│   Service   │───▶│ Repository  │───▶│  Database   │
│ (Generated) │    │ (Business)  │    │ (Data)      │    │ (PostgreSQL)│
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

- **Controller Layer**: Generated from OpenAPI spec
- **Service Layer**: Business logic and validation (`RecipeApiImpl`)
- **Repository Layer**: Data access with JPA transactions (`RecipeRepository`)
- **Model Layer**: JPA entities (`Recipe`)

## 🚀 How to Run

### Prerequisites
- **Java 17+**
- **SBT 1.11+**
- **PostgreSQL** (or use Docker)

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd recipe-application
```

2. **Set up database**
```bash
# Using Docker
docker run --name postgres-recipe \
  -e POSTGRES_DB=recipes \
  -e POSTGRES_USER=recipe_user \
  -e POSTGRES_PASSWORD=recipe_pass \
  -p 5432:5432 -d postgres:15
```

3. **Set environment variables**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/recipes"
export DB_USER="recipe_user"
export DB_PASSWORD="recipe_pass"
export PLAY_SECRET="your-secret-key-here"
```

4. **Run the application**
```bash
sbt run
```

The API will be available at `http://localhost:9000`

### Using Docker

```bash
# Build image
docker build -t recipe-api .

# Run container
docker run -p 9000:9000 \
  -e DB_URL="your-db-url" \
  -e DB_USER="your-db-user" \
  -e DB_PASSWORD="your-db-password" \
  -e PLAY_SECRET="your-secret" \
  recipe-api
```

## 🧪 Testing

### Run All Tests
```bash
sbt test
```

### Run with Coverage
```bash
sbt clean coverage test coverageReport
```

### Test Structure
- **Unit Tests**: Service layer with mocked dependencies
- **Integration Tests**: End-to-end workflow testing
- **Repository Tests**: JPA transaction verification
- **Coverage**: 100% line coverage achieved

## 📮 Postman Collection

A complete Postman collection is available in the `postman/` folder:

```
postman/
└── Recipe Application.postman_collection.json
```

**Import Instructions:**
1. Open Postman
2. Click "Import" → "Upload Files"
3. Select `Recipe Application.postman_collection.json`
4. Update the base URL variable if needed

**Collection includes:**
- ✅ All CRUD operations
- ✅ Error scenarios
- ✅ Environment variables
- ✅ Pre-request scripts
- ✅ Response validation tests

## 🔧 Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/recipes` |
| `DB_USER` | Database username | `recipe_user` |
| `DB_PASSWORD` | Database password | `recipe_pass` |
| `PLAY_SECRET` | Application secret key | `your-secret-key` |
| `PORT` | HTTP port (optional) | `9000` |

### Application Configuration

Key configuration files:
- `conf/application.conf` - Main configuration
- `conf/openapi/recipes-api.yaml` - API specification
- `conf/evolutions/default/` - Database migrations

## 📊 Database Schema

```sql
CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    making_time VARCHAR(100) NOT NULL,
    serves VARCHAR(100) NOT NULL,
    ingredients VARCHAR(300) NOT NULL,
    cost INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔍 API Documentation

- **OpenAPI Spec**: Available at `/docs` when running
- **Interactive UI**: Swagger UI available at `/api-docs`
- **Postman Collection**: Complete collection in `postman/` folder

## 🚀 Deployment

The application is deployed on **Render** with:
- Automatic deployments from main branch
- Environment-based configuration
- PostgreSQL database
- Health checks and monitoring

**Live API**: `https://givery-recipe-app.onrender.com`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

---

**Built with ❤️ using Play Framework and Scala 3**