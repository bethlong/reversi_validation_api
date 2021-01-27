# Reversi Validation API
A REST API Java SpringBoot implementation of management and play for [Reversi](https://en.wikipedia.org/wiki/Reversi). Designed for two competing bots to connect to play against each other.

## How to Build the JAR

Requirements:
* Maven 3+
* Java 11+

1. Download the Repository
2. Open the root directory of the repository.
3. Run:
```
mvn test clean package
```
4. Find the JAR in the <root>/target/ folder.

Note to IntelliJ users: There is a "Build JAR" run configuration setup in the repository if you have it downloaded.

## How to Run

Requirements:
* Java 11+

In the root directory...

First time running the JAR? Make sure to run this command the first time:
```
/path/to/java/java -jar chess-validation-api-1.0-SNAPSHOT.jar -Dspring.jpa.hibernate.ddl-auto=create-drop
```

Otherwise:
```
/path/to/java/java -jar chess-validation-api-1.0-SNAPSHOT.jar
```