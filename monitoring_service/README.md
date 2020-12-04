# Monitoring Service

## Technologies
- Spring Boot
- Spring MVC
- Spring Rest
- Maven
- MySQL

## Configuration
Inside `src/main/resources/` Copy `application-example.properties` to  `application.properties` and add your local configuration variables. Also copy `hibernate.cfg-example.xml` to  `hibernate.cfg.xml` and add your local configuration variables 

## Start project
```
$ mvn clean install
```

```
$ mvn spring-boot:run
```

Visit [http://localhost:8081/](http://localhost:8081/)

## Generate WAR file
```
$ mvn package
```