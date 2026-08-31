# social-media-app

This guide walks you through setting up and running the application on your local machine using Java, Maven and
PostgreSQL.

---

## Prerequisites

Make sure the following tools are installed on your system:

1. **Java 25**
   Download
   from: [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

2. **Apache Maven**
   Download from: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

3. **PostgreSQL**
   Download from: [https://www.postgresql.org/download/](https://www.postgresql.org/download/)

You can verify that the required tools are installed by running:

```bash
java -version
```

```bash
mvn -version
```

```bash
psql --version
```

## Database Setup

Make sure PostgreSQL is running on your system.

1. Create a database

Create a database for the application:

```postgresql
CREATE DATABASE <database_name>;
```

2. Create a PostgreSQL user (optional)

Create a PostgreSQL user for the application:

```postgresql
CREATE USER <username> WITH PASSWORD '<password>';
```

3. Grant database access

Grant the user access to the database:

```postgresql
GRANT ALL PRIVILEGES ON DATABASE <database_name> TO <username>;
```

## Application Configuration

Configure the PostgreSQL connection in: `src/main/resources/application.properties`

For example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<database_name>
spring.datasource.username=<username>
spring.datasource.password=<password>
```

Replace <database_name>, <username>, and <password> with your PostgreSQL configuration.

## Build the Application

From the root directory of the project, run:

```bash
mvn clean package
```

This will compile the application, run the tests, and create the application JAR file.

## Run the Application

Run the application using Maven:

```bash
mvn spring-boot:run
```

Alternatively, you can run the generated JAR file:

```bash
java -jar target/<app-name>.jar
```

The application should now be running.

## Access the Application

By default, the application will be available at: http://localhost:8080

If a different port is configured in `application.properties`, use that port instead.

## License & Attribution

- This project is provided for learning and personal use.
- Do not claim this project or its code as your own work.
- If you use or modify this project, please provide appropriate credit to the original author.
