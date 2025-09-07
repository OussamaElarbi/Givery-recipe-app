# -------- BUILD STAGE --------
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.11.6_3.7.2 AS build

WORKDIR /app

# 1️⃣ Copy only dependency files first to leverage caching
COPY build.sbt ./
COPY project/ ./project/

# Download dependencies (cached layer)
RUN sbt update

# 2️⃣ Copy the rest of the source code
COPY . .

# Build Play distribution (production mode)
RUN sbt dist

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Install unzip once and clean apt cache
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy built Play distribution
COPY --from=build /app/target/universal/*.zip /tmp/app.zip
RUN unzip /tmp/app.zip -d /app/app && rm /tmp/app.zip

WORKDIR /app/app

# Use shell form CMD so environment variables interpolate correctly
CMD bin/recipe-application-givery \
    -Dplay.http.secret.key=$PLAY_SECRET \
    -Dhttp.port=$PORT \
    -Dconfig.resource=application.conf