
#
# Oracle Java 8 Dockerfile
#
# https://github.com/dockerfile/java
# https://github.com/dockerfile/java/tree/master/oracle-java8
#

FROM mozilla/sbt as build-stage
COPY . .
RUN pwd
RUN ls -al
RUN sbt assembly

# Pull base image.
FROM java:8

# Define working directory.
WORKDIR /data
COPY --from=build-stage target/scala-2.12/gunplay_from_scratch-assembly-1.0.jar /data
EXPOSE 8080

# Define default command.
CMD ["java", "-jar", "-server", "gunplay_from_scratch-assembly-1.0.jar"]