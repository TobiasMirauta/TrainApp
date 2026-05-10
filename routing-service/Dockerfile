# Pasul 1: Folosim o imagine ușoară de Java
FROM eclipse-temurin:17-jdk-alpine

# Pasul 2: Setăm folderul de lucru în container
WORKDIR /app

# Pasul 3: Copiem fișierul JAR generat de Maven în container
# (Presupunem că ai rulat 'mvn clean package' înainte)
COPY target/*.jar app.jar

# Pasul 4: Comanda de pornire
ENTRYPOINT ["java", "-jar", "app.jar"]