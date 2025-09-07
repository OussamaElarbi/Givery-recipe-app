# -------- BUILD STAGE --------
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.11.6_3.7.2 AS build

WORKDIR /app
COPY . .

# Build Play distribution once
RUN sbt dist

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Install unzip and clean apt cache
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/universal/*.zip /tmp/app.zip
RUN unzip /tmp/app.zip -d /app/app && rm /tmp/app.zip

WORKDIR /app/app

# Run Play app with env variable interpolation
CMD bin/recipe-application-givery -Dplay.http.secret.key=$PLAY_SECRET -Dhttp.port=$PORT -Dconfig.resource=application.conf