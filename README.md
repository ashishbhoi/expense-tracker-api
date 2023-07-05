# This is a Spring Boot API for Expense Tracking

## Features
- User can register and login `POST /api/users/register` `POST /api/users/login`
- User can create, update and delete categories `POST, PUT, DELETE /api/categories`
- User can create, update and delete transactions `POST, PUT, DELETE /api/categories/{category-id}/transactions`
- User can view all categories with total expense `GET /api/categories`
- User can view all transactions `GET /api/categories/{category-id}/transactions`

# How to run the application

1. Clone the repository
    ```bash
    git clone https://github.com/ashishbhoi/expense-tracker.git
    ```
   
2. Use Docker to run the Postgres database
    ```bash
    docker run --name postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres
    ```
   
3. Create a Postgres database with sql file `expensetracker_db.sql` in the root directory.
    ```bash
    psql -U postgres -f expensetracker_db.sql
    ```
   
4. Set Environment variable
    ```bash
    export JDBC_URL="jdbc:postgresql://172.0.0.1:5432/expensetrackerdb"
    export API_SECRET_KEY="<your-api-secret-key>"
    export JDBC_USER="expensetracker"
    export JDBC_PASS="expensetracker"
    ```
   > secret key can be any string of your choice.
5. Run the application
    ```bash
    mvn spring-boot:run
    ```
   if you don't have maven installed, you can use the `mvnw` file in the root directory.
    ```bash
    ./mvnw spring-boot:run
    ```