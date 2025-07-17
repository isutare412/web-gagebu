ARG PLATFORM=linux/amd64

FROM eclipse-temurin:21-alpine AS builder

WORKDIR /workspace
COPY gradle gradle
COPY build.gradle build.gradle
COPY gradlew gradlew
COPY settings.gradle settings.gradle
COPY src src

RUN ./gradlew bootJar --no-build-cache --debug

FROM --platform=${PLATFORM} gcr.io/distroless/java21-debian12:debug

WORKDIR /app
COPY --from=builder /workspace/build/libs/app.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
CMD [ "--spring.profiles.active", "prod", "--spring.config.additional-location", "file:/config/application-prod.yaml" ]
