Email Management Service
========================

This application allows for managing emails through a REST API. Emails can be stored along
with their headers and bodies. It is also possible to create editable drafts and mark emails
as spam.

_Disclaimer: This project has been created as part of a job interview and should be treated as such._

## Used technologies

### [Spring Boot](https://spring.io/projects/spring-boot)

Easy-to-use framework supporting various protocols, dependency injection, testing, and much more.

### [Spring Data REST](https://spring.io/projects/spring-data-rest)

Obvious choice when exposing JPA entities via REST endpoints.

### [PostgreSQL](https://www.postgresql.org/)

Mature relational database used in many production environments.

### [Liquibase](https://www.liquibase.com/)

Initializes the database and keeps track of schema changes.

## Starting the application

The most simple way is to run the Gradle bootrun task:

```shell
$ ./gradlew bootRun
```

This will start a Tomcat server on port 8080 providing a REST under http://localhost:8080/emails

## Deployment in an Application Container

In order to deploy this project in an application container like Apache Tomcat, first create the WAR file:

```shell
$ ./gradlew war
```

It is located in the `build/libs` directory and can be easily deployed.

In order to use an external PostgreSQL database, which is recommended for production environments,
add an `appliation.properties` file to Tomcat's `lib/config` directory and configure the connection
settings accordingly:

```properties
spring.datasource.url=jdbc:postgresql://db-server/emails
spring.datasource.username=postgres
spring.datasource.password=biKLzTV3uzf6H6LyaDSuleSt
```

## Deployment as a Docker Image

The Docker image can be created with the `bootBuildImage` task:

```shell
$ ./gradlew bootBuildImage
```

The resulting image is tagged as `docker.io/library/email-management:0.0.1-SNAPSHOT`.

In order to use an external PostgreSQL database, which is recommended for production environments,
the database connection has to be configured using environment variables. The Docker-Compose file
below shows a working example:

```yaml
services:

  email-management:
    image: docker.io/library/email-management:0.0.1-SNAPSHOT
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db-server/emails
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: biKLzTV3uzf6H6LyaDSuleSt
```

## Access Control

By default, a user with the name "user" and a random password is generated every time the application starts.
These credentials are necessary for accessing any REST endpoint.

The password is visible in the application log. However, this setup is only suitable for development purposes.
In a production environment, the password should be set with the property

```properties
spring.security.user.password=zBiFhCtGGnSxd4rwCaxED6VN
```

or the environment variable

```shell
SPRING_SECURITY_USER_PASSWORD=zBiFhCtGGnSxd4rwCaxED6VN
```

## Batch Operations

There are several methods available for handling multiple e-mails in one request.

### Querying multiple emails

Example: http://localhost:8080/emails?ids=585,6966

### Inserting multiple emails

* URL: http://localhost:8080/emails/batch-insert
* Method: POST
* Example JSON:
```json
[
    {
        "from": "stephen.franklin@earth-alliance.org",
        "to": "john.sheridan@earth-alliance.org",
        "subject": "New medical equipment required"
    },
    {
        "from": "michael.garibaldi@earth-alliance.org",
        "to": "zack.allan@earth-alliance.org",
        "subject": "Improve security in brown sector"
    }
]
```

### Deleting multiple emails

* URL: http://localhost:8080/emails/batch-delete
* Method: POST
* Example JSON:
```json
[ 1, 2 ]