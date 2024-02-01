FROM gradle:7.6-jdk17-focal AS builder

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n
WORKDIR /builder
COPY ./ ./
RUN gradle clean build -x test && rm -f /builder/build/libs/*-plain.jar

FROM openjdk:17 AS runner

WORKDIR /app

COPY --from=builder /builder/build/libs/*.jar app.jar

CMD ["java","-jar","app.jar"]

