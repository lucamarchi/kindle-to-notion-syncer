FROM azul/zulu-openjdk-alpine:17.0.2-jre-headless
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
CMD exec java ${JAVA_OPTIONS} -jar app.jar