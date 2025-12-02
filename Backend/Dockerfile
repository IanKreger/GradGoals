# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# This creates two jars: the shaded one and the "original" one
RUN mvn -q clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk
WORKDIR /app
# CHANGE HERE: We specifically copy the jar starting with 'gradgoals' 
# This ensures we DO NOT copy the one named 'original-gradgoals...'
COPY --from=build /app/target/gradgoals-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]