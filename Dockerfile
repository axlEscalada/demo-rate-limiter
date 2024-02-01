FROM openjdk:17 AS runner
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n
COPY /build/libs/*.jar app.jar

CMD ["java","-jar","app.jar"]
