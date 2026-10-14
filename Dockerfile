FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S -G app app
COPY --from=build /build/target/estoque-inteligente-*.jar app.jar
USER app
ENV JAVA_TOOL_OPTIONS="-Xms24m -Xmx48m -XX:MaxMetaspaceSize=64m -XX:ReservedCodeCacheSize=12m -Xss256k -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
