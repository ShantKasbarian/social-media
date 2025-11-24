FROM openjdk:21
LABEL authors="Shant"
ADD target/social-media-0.0.1-SNAPSHOT.jar social-media-app-v1.jar

ENTRYPOINT ["java", "-jar", "social-media-app-v1.jar"]