# Time Tracker Server (under development)

## Overview
The Time Tracker Server is a Java-based backend application developed as part of a school project with Jönköping University. The server-side application serves as the backend for the Time Tracker client, handling data storage, authentication, and authorization. The client can be found - https://github.com/D-Hankin/timetrackerClient or live - https://sea-lion-app-6y5s4.ondigitalocean.app/

## Technologies Used
- Java: Core programming language for backend development.
- Quarkus: Framework for building cloud-native applications in Java.
- Jakarta EE: Platform for developing enterprise Java applications.
- MongoDB Atlas: Cloud-hosted NoSQL database service for storing application data.
- Bcrypt: Password hashing algorithm for secure password storage.
- JWT (JSON Web Tokens): Authentication mechanism for stateless and secure user authentication.
- Role-Based Access Control (RBAC): Authorization mechanism based on user roles for accessing application resources.
- Environmental Variables: Secure storage of sensitive information such as database connection URI and JWT secret key.

## Features
- User Authentication: Users can securely authenticate using JWT tokens generated upon successful login.
- Role-Based Access Control (RBAC): Access to application resources is restricted based on user roles (e.g., admin, user). Role-based authorization ensures that users can only access the functionality relevant to their role.
- Password Hashing: User passwords are securely hashed using the bcrypt algorithm for enhanced security.
- RESTful APIs: Server provides RESTful APIs for communication with the client-side application.
- Data Storage: Application data is stored in MongoDB Atlas for persistence and retrieval.

## Database Setup
1. **MongoDB Atlas Cluster Setup**: 
   - Sign up for a MongoDB Atlas account (if you haven't already) and create a new cluster.
   - Configure your MongoDB Atlas cluster settings, including network access, security, and user authentication.

2. **Environmental Variables**:
   - The server relies on environmental variables to securely store sensitive information such as database connection URI, JWT secret key, etc.
   - Create a `.env` file in the root directory of the project to store your environmental variables. Here's an example of the environmental variables you might need:
     ```
     MONGODB_URI=<Your MongoDB Atlas Connection URI>
     JWT_SECRET=<Your JWT Secret Key>
     ```

3. **Configuration**:
   - Update the `application.properties` file or the application configuration file to load the environmental variables at runtime.
   - Refer to the documentation of your chosen Java framework (Quarkus) for instructions on loading environmental variables.

## Usage
To run the Time Tracker Server locally:
1. Clone this repository.
2. Open the project in your preferred Java IDE (e.g., Visual Studio Code, IntelliJ IDEA).
3. Make sure you have Java and Maven installed on your system.
4. Configure the database connection settings in the application.properties file.
5. Build the project: `mvn clean install`.
6. Run the server: `mvn quarkus:dev`.
7. Navigate to localhost:8080 to access the Quarkus dev page.


**Everything below is automatically generated by Quarkus.**

# timetracker_server

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/timetracker_server-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- Narayana JTA - Transaction manager ([guide](https://quarkus.io/guides/transaction)): Offer JTA transaction support (included in Hibernate ORM)
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
