# -------- BUILD STAGE --------
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.11.6_3.7.2 as build

WORKDIR /app
COPY . .

RUN sbt dist
# Build Play distribution
RUN sbt dist

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:17-jre as runtime

WORKDIR /app
COPY --from=build /app/target/universal/*.zip /tmp/app.zip

RUN apt-get update && apt-get install -y unzip && \
    unzip /tmp/app.zip -d /app && \
    mv /app/* /app/app && \
    rm /tmp/app.zip

WORKDIR /app/app

# Run Play app
CMD ["bin/recipe-application-givery", "-Dplay.http.secret.key=${PLAY_SECRET}", "-Dconfig.resource=application.conf"]