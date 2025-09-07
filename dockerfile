# -------- BUILD STAGE --------
FROM eclipse-temurin:17-jdk as build

# Install sbt
RUN apt-get update && apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823 | apt-key add && \
    apt-get update && apt-get install -y sbt unzip

WORKDIR /app
COPY . .

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