# Use Java 17 (compatible with Play 3.x + Scala 3.3.6)
FROM eclipse-temurin:17-jdk

# Install sbt
RUN apt-get update && apt-get install -y curl gnupg apt-transport-https \
  && echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list \
  && curl -sL https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823 | gpg --dearmor > /etc/apt/trusted.gpg.d/sbt.gpg \
  && apt-get update && apt-get install -y sbt

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Build the app using Play’s stage task (creates start scripts under /target/universal/stage)
RUN sbt stage

# Run the staged app
CMD ["./target/universal/stage/bin/recipe-application-givery", "-Dplay.http.secret.key=${PLAY_SECRET}", "-Dhttp.port=9000"]