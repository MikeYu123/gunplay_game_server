
#
# Oracle Java 8 Dockerfile
#
# https://github.com/dockerfile/java
# https://github.com/dockerfile/java/tree/master/oracle-java8
#

# Pull base image.
FROM java:8

# Define working directory.
WORKDIR /data
COPY bin /data

# Define default command.
CMD ["java", "-jar", "gunplay_from_scratch-assembly-1.0.jar"]